package ru.croc.ugd.ssr.service.disabledperson;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.DisabledPersonDetailsProjection;
import ru.croc.ugd.ssr.disabledperson.DisabledPerson;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonData;
import ru.croc.ugd.ssr.dto.disabledperson.DisabledPersonWithFlatIdDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDetailsDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.flows.DisabledPersonFlowService;
import ru.croc.ugd.ssr.mapper.DisabledPersonMapper;
import ru.croc.ugd.ssr.mapper.PersonMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.document.DisabledPersonDocumentService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultDisabledPersonService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultDisabledPersonService implements DisabledPersonService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DisabledPersonDocumentService disabledPersonDocumentService;
    private final DisabledPersonMapper disabledPersonMapper;
    private final DisabledPersonFlowService disabledPersonFlowService;
    private final PersonMapper personMapper;
    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;

    @Override
    public RestDisabledPersonDetailsDto fetchDisabledPersonDetailsByUnomAndFlatNumber(
        final String unom, final String flatNumber, final String personDocumentId
    ) {
        final List<DisabledPersonDetailsProjection> projections = disabledPersonDocumentService
            .fetchDisabledPersonDetailsByUnomAndFlatNumber(unom, flatNumber);

        if (!projections.isEmpty()) {
            final boolean isWheelchairUserDetected = projections.stream()
                .anyMatch(DisabledPersonDetailsProjection::getUsingWheelchair);

            return projections.stream()
                .filter(projection -> Objects.equals(personDocumentId, projection.getPersonDocumentId()))
                .findFirst()
                .map(projection -> RestDisabledPersonDetailsDto.builder()
                    .wheelchairUserDetected(isWheelchairUserDetected)
                    .usingWheelchair(projection.getUsingWheelchair())
                    .disabledPerson(true)
                    .build()
                )
                .orElse(RestDisabledPersonDetailsDto.builder()
                    .disabledPerson(false)
                    .wheelchairUserDetected(isWheelchairUserDetected)
                    .build());
        } else {
            return RestDisabledPersonDetailsDto.builder()
                .disabledPerson(false)
                .wheelchairUserDetected(false)
                .build();
        }
    }

    @Override
    @Transactional
    public void saveDisabledPersons(
        final List<RestDisabledPersonDto> disabledPersonDtos, final String disabledPersonImportDocumentId
    ) {
        checkUnomAndFlatNumber(disabledPersonDtos);

        checkDuplicates(disabledPersonDtos);

        final List<DisabledPersonDocument> disabledPersonDocuments =
            fetchAndUpdateDisabledPersonDocuments(disabledPersonDtos, disabledPersonImportDocumentId);

        checkDuplicates(disabledPersonDtos, disabledPersonDocuments);

        final List<DisabledPersonWithFlatIdDto> disabledPersonWithFlatIdDtos = disabledPersonDtos.stream()
            .map(dto -> disabledPersonMapper.toDisabledPersonWithFlatIdDto(dto, getFlatId(dto)))
            .collect(Collectors.toList());

        checkPersonDuplicates(disabledPersonWithFlatIdDtos);

        disabledPersonWithFlatIdDtos.stream()
            .collect(Collectors.groupingBy(dto -> dto.getDisabledPerson().getUnom()))
            .forEach((unom, dtos) -> {
                if (StringUtils.isNotEmpty(disabledPersonImportDocumentId)) {
                    final List<DisabledPersonDocument> deletedDisabledPersonDocuments = disabledPersonDocuments.stream()
                        .filter(document -> document.getDocument().getDisabledPersonData().isDeleted())
                        .filter(document -> document.getDocument().getDisabledPersonData().getUnom().equals(unom))
                        .collect(Collectors.toList());
                    saveDisabledPersonsFromExcelAndSendFlow(
                        dtos, deletedDisabledPersonDocuments, disabledPersonImportDocumentId
                    );
                } else {
                    createOrUpdateDisabledPersonsAndSendFlow(dtos);
                }
            });
    }

    @Override
    public void unbindDisabledPersonsWithDeletedPersons(final List<String> deletedPersonsDocumentIds) {
        if (deletedPersonsDocumentIds.isEmpty()) {
            return;
        }
        final List<DisabledPersonDocument> disabledPersonDocuments =
            disabledPersonDocumentService.fetchAllByPersonDocumentIds(deletedPersonsDocumentIds);
        disabledPersonDocuments
            .forEach(document -> {
                document.getDocument().getDisabledPersonData().setPersonDocumentId(null);
                disabledPersonDocumentService.updateDocument(document, "unbindDisabledPersonsWithDeletedPersons");
            });
    }

    @Override
    public void bindDisabledPersonWithPerson(final PersonDocument personDocument) {
        final PersonType personData = personDocument.getDocument().getPersonData();
        final String unom = nonNull(personData.getUNOM()) ? personData.getUNOM().toString() : null;

        disabledPersonDocumentService.fetchByUnomAndFlatNumberAndFullNameAndBirthDate(
                unom, personData.getFlatNum(), personData.getFIO(), personData.getBirthDate()
            )
            .ifPresent(document -> {
                    document.getDocument()
                        .getDisabledPersonData()
                        .setPersonDocumentId(document.getId());
                    disabledPersonDocumentService.updateDocument(document, "bindDisabledPersonWithPerson");
                }
            );
    }

    private void saveDisabledPersonsFromExcelAndSendFlow(
        final List<DisabledPersonWithFlatIdDto> disabledPersonDtos,
        final List<DisabledPersonDocument> deletedDisabledPersonDocuments,
        final String disabledPersonImportDocumentId
    ) {
        final List<DisabledPersonDocument> savedDisabledPersonDocuments = disabledPersonDtos.stream()
            .filter(dto -> isNull(dto.getDisabledPerson().getDisabledPersonDocumentId()))
            .map(dto -> createOrUpdateDisabledPerson(dto, disabledPersonImportDocumentId))
            .collect(Collectors.toList());

        final List<DisabledPersonDocument> disabledPersonDocumentsForFlowSending = Stream.concat(
                deletedDisabledPersonDocuments.stream(), savedDisabledPersonDocuments.stream()
            )
            .collect(Collectors.toList());

        disabledPersonFlowService.sendDisabledPersonsInfoFlow(disabledPersonDocumentsForFlowSending);
    }

    private void createOrUpdateDisabledPersonsAndSendFlow(final List<DisabledPersonWithFlatIdDto> disabledPersonDtos) {
        final List<DisabledPersonDocument> disabledPersonDocuments = disabledPersonDtos.stream()
            .map(disabledPersonDto -> createOrUpdateDisabledPerson(disabledPersonDto, null))
            .collect(Collectors.toList());

        disabledPersonFlowService.sendDisabledPersonsInfoFlow(disabledPersonDocuments);
    }

    private DisabledPersonDocument createOrUpdateDisabledPerson(
        final DisabledPersonWithFlatIdDto disabledPersonDto, final String disabledPersonImportDocumentId
    ) {
        final String personDocumentId = retrievePersonDocumentId(disabledPersonDto);

        final DisabledPersonDocument preparedDisabledPersonDocument =
            ofNullable(disabledPersonDto.getDisabledPerson().getDisabledPersonDocumentId())
                .map(disabledPersonDocumentService::fetchDocument)
                .map(disabledPersonDocument -> disabledPersonMapper.toDisabledPersonDocument(
                    disabledPersonDocument, disabledPersonDto, personDocumentId, disabledPersonImportDocumentId
                ))
                .orElseGet(() -> disabledPersonMapper.toDisabledPersonDocument(
                    new DisabledPersonDocument(), disabledPersonDto, personDocumentId, disabledPersonImportDocumentId
                ));

        return disabledPersonDocumentService.createOrUpdateDocument(
            preparedDisabledPersonDocument, "createOrUpdateDisabledPerson"
        );
    }

    private String retrievePersonDocumentId(final DisabledPersonWithFlatIdDto disabledPersonDto) {
        return of(disabledPersonDto)
            .map(dto -> dto.getDisabledPerson().getPersonDocumentId())
            .orElseGet(() -> {
                final PersonDocument personDocument = personMapper.toPersonDocument(
                    disabledPersonDto, true
                );
                final PersonDocument savedPersonDocument = personDocumentService.createDocument(
                    personDocument, true, "createPersonFromDisabledPerson"
                );
                return savedPersonDocument.getId();
            });
    }

    private void checkUnomAndFlatNumber(final List<RestDisabledPersonDto> disabledPersonDtos) {
        if (disabledPersonDtos.stream()
            .anyMatch(disabledPersonDto -> StringUtils.isBlank(disabledPersonDto.getUnom())
                || StringUtils.isBlank(disabledPersonDto.getFlatNumber()))) {
            throw new SsrException("Заполнены не все unom или flatNumber");
        }
    }

    private String getFlatId(final RestDisabledPersonDto disabledPersonDto) {
        if (StringUtils.isNotBlank(disabledPersonDto.getPersonDocumentId())) {
            return null;
        }
        final String flatId = realEstateDocumentService.fetchFlatIdByUnomAndFlatNumber(
            disabledPersonDto.getUnom(), disabledPersonDto.getFlatNumber()
        );

        if (isNull(flatId)) {
            throw new SsrException("Не существует квартиры "
                + disabledPersonDto.getFlatNumber()
                + " в доме с УНОМ "
                + disabledPersonDto.getUnom());
        }

        return flatId;
    }

    private List<DisabledPersonDocument> fetchAndUpdateDisabledPersonDocuments(
        final List<RestDisabledPersonDto> disabledPersonDtos, final String disabledPersonImportDocumentId
    ) {
        final List<DisabledPersonDocument> disabledPersonDocuments = disabledPersonDtos.stream()
            .map(disabledPersonDto -> Pair.of(disabledPersonDto.getUnom(), disabledPersonDto.getFlatNumber()))
            .distinct()
            .map(flat -> disabledPersonDocumentService.fetchByUnomAndFlatNumber(flat.getLeft(), flat.getRight()))
            .flatMap(List::stream)
            .collect(Collectors.toList());

        if (StringUtils.isNotEmpty(disabledPersonImportDocumentId)) {
            final List<String> existsDisabledPersonDocumentIds = disabledPersonDtos.stream()
                .map(RestDisabledPersonDto::getDisabledPersonDocumentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            disabledPersonDocuments.stream()
                .filter(document -> !existsDisabledPersonDocumentIds.contains(document.getId()))
                .forEach(this::updateDeletedAfterExcelLoading);
        }

        return disabledPersonDocuments;
    }

    private void updateDeletedAfterExcelLoading(final DisabledPersonDocument disabledPersonDocument) {
        disabledPersonDocument.getDocument().getDisabledPersonData().setDeleted(true);
        disabledPersonDocumentService.updateDocument(disabledPersonDocument, "updateDeletedAfterExcelLoading");
    }

    private void checkDuplicates(final List<RestDisabledPersonDto> disabledPersonDtos) {
        final List<String> disabledPersonKeysBySnils = disabledPersonDtos.stream()
            .filter(dto -> isNull(dto.getDisabledPersonDocumentId()) && nonNull(dto.getSnils()))
            .map(this::getDisabledPersonKeyBySnils)
            .collect(Collectors.toList());

        final List<String> disabledPersonKeysByFioAndBirthDate = disabledPersonDtos.stream()
            .filter(dto -> isNull(dto.getDisabledPersonDocumentId())
                && nonNull(dto.getBirthDate())
                && nonNull(dto.getLastName())
                && nonNull(dto.getFirstName()))
            .map(this::getDisabledPersonKeyByFioAndBirthDate)
            .collect(Collectors.toList());

        final List<String> duplicates = disabledPersonKeysBySnils.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        duplicates.addAll(
            disabledPersonKeysByFioAndBirthDate.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );

        if (!duplicates.isEmpty()) {
            throw new SsrException("Имеются одинаковые записи: " + String.join(", ", duplicates));
        }
    }

    private void checkDuplicates(
        final List<RestDisabledPersonDto> disabledPersonDtos,
        final List<DisabledPersonDocument> disabledPersonDocuments
    ) {
        final List<DisabledPersonData> savedDisabledPersons = disabledPersonDocuments.stream()
            .map(DisabledPersonDocument::getDocument)
            .map(DisabledPerson::getDisabledPersonData)
            .filter(disabledPerson -> !disabledPerson.isDeleted())
            .collect(Collectors.toList());

        final List<String> disabledPersonKeysBySnils = savedDisabledPersons.stream()
            .filter(disabledPerson -> nonNull(disabledPerson.getSnils()))
            .map(this::getDisabledPersonKeyBySnils)
            .distinct()
            .collect(Collectors.toList());

        final List<String> disabledPersonKeysByFioAndBirthDate = savedDisabledPersons.stream()
            .filter(disabledPerson -> nonNull(disabledPerson.getBirthDate())
                && nonNull(disabledPerson.getLastName())
                && nonNull(disabledPerson.getFirstName()))
            .map(this::getDisabledPersonKeyByFioAndBirthDate)
            .distinct()
            .collect(Collectors.toList());

        final List<String> duplicates = disabledPersonDtos.stream()
            .filter(dto -> isNull(dto.getDisabledPersonDocumentId()) && nonNull(dto.getSnils()))
            .map(this::getDisabledPersonKeyBySnils)
            .filter(disabledPersonKeysBySnils::contains)
            .collect(Collectors.toList());

        duplicates.addAll(
            disabledPersonDtos.stream()
                .filter(dto -> isNull(dto.getDisabledPersonDocumentId())
                    && nonNull(dto.getBirthDate())
                    && nonNull(dto.getLastName())
                    && nonNull(dto.getFirstName()))
                .map(this::getDisabledPersonKeyByFioAndBirthDate)
                .filter(disabledPersonKeysByFioAndBirthDate::contains)
                .collect(Collectors.toList())
        );

        if (!duplicates.isEmpty()) {
            throw new SsrException("Следующие маломобильные граждане уже существуют: " + String.join(", ", duplicates));
        }
    }

    private void checkPersonDuplicates(final List<DisabledPersonWithFlatIdDto> disabledPersonDtos) {
        final List<String> duplicates = disabledPersonDtos.stream()
            .filter(dto -> isNull(dto.getDisabledPerson().getPersonDocumentId()))
            .map(this::checkPersonDuplicates)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            throw new SsrException("Уже существуют жители: " + String.join(", ", duplicates));
        }
    }

    private String checkPersonDuplicates(final DisabledPersonWithFlatIdDto dto) {
        final List<PersonType> persons = personDocumentService.fetchByFlatId(dto.getFlatId())
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());

        if (nonNull(dto.getDisabledPerson().getSnils()) && persons.stream()
            .anyMatch(person -> dto.getDisabledPerson().getSnils().equals(person.getSNILS()))
        ) {
            return getDisabledPersonKeyBySnils(dto);
        }

        if (persons.stream()
            .anyMatch(
                person -> Objects.equals(dto.getDisabledPerson().getBirthDate(), person.getBirthDate())
                    && Objects.equals(dto.getDisabledPerson().getFirstName(), person.getFirstName())
                    && Objects.equals(dto.getDisabledPerson().getMiddleName(), person.getMiddleName())
                    && Objects.equals(dto.getDisabledPerson().getLastName(), person.getLastName())
            )
        ) {
            return getDisabledPersonKeyByFioAndBirthDate(dto);
        }

        return null;
    }

    private String getDisabledPersonKeyBySnils(final RestDisabledPersonDto dto) {
        return String.join(" ", "СНИЛС", dto.getSnils(), "УНОМ", dto.getUnom(), "Квартира", dto.getFlatNumber());
    }

    private String getDisabledPersonKeyBySnils(final DisabledPersonWithFlatIdDto dto) {
        return String.join(
            " ",
            "СНИЛС",
            dto.getDisabledPerson().getSnils(),
            "УНОМ",
            dto.getDisabledPerson().getUnom(),
            "Квартира",
            dto.getDisabledPerson().getFlatNumber()
        );
    }

    private String getDisabledPersonKeyBySnils(final DisabledPersonData disabledPerson) {
        return String.join(
            " ",
            "СНИЛС",
            disabledPerson.getSnils(),
            "УНОМ",
            disabledPerson.getUnom(),
            "Квартира",
            disabledPerson.getFlatNumber()
        );
    }

    private String getDisabledPersonKeyByFioAndBirthDate(final RestDisabledPersonDto dto) {
        return Stream.of(
                dto.getLastName(),
                dto.getFirstName(),
                dto.getMiddleName(),
                dto.getBirthDate().format(DATE_FORMATTER),
                "УНОМ",
                dto.getUnom(),
                "Квартира",
                dto.getFlatNumber()
            )
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }

    private String getDisabledPersonKeyByFioAndBirthDate(final DisabledPersonWithFlatIdDto dto) {
        return Stream.of(
                dto.getDisabledPerson().getLastName(),
                dto.getDisabledPerson().getFirstName(),
                dto.getDisabledPerson().getMiddleName(),
                dto.getDisabledPerson().getBirthDate().format(DATE_FORMATTER),
                "УНОМ",
                dto.getDisabledPerson().getUnom(),
                "Квартира",
                dto.getDisabledPerson().getFlatNumber()
            )
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }

    private String getDisabledPersonKeyByFioAndBirthDate(final DisabledPersonData disabledPerson) {
        return Stream.of(
                disabledPerson.getLastName(),
                disabledPerson.getFirstName(),
                disabledPerson.getMiddleName(),
                disabledPerson.getBirthDate().format(DATE_FORMATTER),
                "УНОМ",
                disabledPerson.getUnom(),
                "Квартира",
                disabledPerson.getFlatNumber()
            )
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }
}
