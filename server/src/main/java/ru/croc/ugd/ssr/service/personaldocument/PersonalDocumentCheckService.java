package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestApartmentPersonalDocumentDto;
import ru.croc.ugd.ssr.generated.dto.personaldocument.ExternalRestCheckPersonalDocumentDto;
import ru.croc.ugd.ssr.mapper.PersonalDocumentMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentRequestDocumentService;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentType;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentTypeService;
import ru.croc.ugd.ssr.service.validator.impl.personaldocument.PersonalDocumentPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.GenericValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonalDocumentCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.personal.document.person.notApplicant";

    private final PersonDocumentService personDocumentService;
    private final DocumentConverterService documentConverterService;
    private final FlatService flatService;
    private final MessageSource messageSource;
    private final PersonDocumentUtils personDocumentUtils;
    private final PersonalDocumentPersonValidator<ValidatablePersonData> personalDocumentPersonValidator;
    private final PersonalDocumentMapper personalDocumentMapper;
    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonalDocumentRequestDocumentService personalDocumentRequestDocumentService;
    private final SsrPersonalDocumentTypeService ssrPersonalDocumentTypeService;

    public ExternalRestCheckPersonalDocumentDto getCheckResult(final String snils, final String ssoId) {
        final List<PersonDocument> personDocumentList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        return check(personDocumentList, personalDocumentPersonValidator::validate);
    }

    private ExternalRestCheckPersonalDocumentDto check(
        final List<PersonDocument> personDocumentList,
        final Function<ValidatablePersonData, ValidationResult> validator
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personDocumentUtils
            .mapPersonsToExpandedPersonTypes(personDocumentList);
        if (CollectionUtils.isEmpty(validatablePersonDataList)) {
            return getPersonalDocumentCheckResponseNotApplicantDto();
        }

        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults = validatablePersonDataList
            .stream()
            .map(validatablePersonData -> Pair.of(validator.apply(validatablePersonData), validatablePersonData))
            .collect(Collectors.toList());

        if (shouldIgnoreAllApartment(validationResults)) {
            return getPersonalDocumentCheckResponseNotAdultApplicantDto();
        }

        final List<ExternalRestApartmentPersonalDocumentDto> apartmentDtos = validationResults
            .stream()
            .map(pair -> getApartmentDto(pair.getRight(), pair.getLeft()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return getPersonalDocumentCheckResponseDto(apartmentDtos, validationResults);
    }

    private ExternalRestApartmentPersonalDocumentDto getApartmentDto(
        final ValidatablePersonData validatablePersonData, final ValidationResult validationResult
    ) {
        if (shouldIgnoreApartment(validationResult)) {
            return null;
        }

        final PersonalDocumentRequest personalDocumentRequest =
            retrieveDocumentRequest(validatablePersonData.getPersonId());
        final boolean existsApplication = personalDocumentApplicationDocumentService
            .existsByPersonDocumentId(validatablePersonData.getPersonId());

        return personalDocumentMapper.toExternalRestApartmentDto(
            validatablePersonData.getPerson(),
            personalDocumentRequest,
            existsApplication,
            this::retrieveAddress,
            this::retrieveFlatNumber,
            this::retrieveLivers,
            this::retrieveSsrPersonalDocumentType
        );
    }

    private String retrieveFlatNumber(final PersonType personType) {
        if (isNull(personType)) {
            return null;
        }
        return ofNullable(personType.getFlatNum())
            .orElseGet(() -> ofNullable(personType.getFlatID())
                .map(flatService::fetchFlat)
                .map(FlatType::getFlatNumber)
                .orElse(null)
            );
    }

    private String retrieveAddress(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getUNOM)
            .map(BigInteger::toString)
            .map(documentConverterService::getAddressByUnom)
            .orElse(null);
    }

    private List<PersonDocument> retrieveLivers(final String affairId) {
        return personDocumentService.fetchByAffairId(affairId);
    }

    private SsrPersonalDocumentType retrieveSsrPersonalDocumentType(final String typeCode) {
        return ssrPersonalDocumentTypeService.getTypeByCode(typeCode);
    }

    private PersonalDocumentRequest retrieveDocumentRequest(final String personDocumentId) {
        return personalDocumentRequestDocumentService.findLastActiveRequestByPersonDocumentId(personDocumentId)
            .map(PersonalDocumentRequestDocument::getDocument)
            .orElse(null);
    }

    private boolean shouldIgnoreAllApartment(
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        return validationResults
            .stream()
            .map(Pair::getLeft)
            .anyMatch(this::shouldIgnoreAllApartment);
    }

    private boolean shouldIgnoreAllApartment(final ValidationResult validationResult) {
        final String notAdultMessage = messageSource.getMessage(
            PersonalDocumentPersonValidator.NOT_ADULT_MESSAGE, null, Locale.getDefault()
        );
        final String joinedValidationMessage = validationResult.getJoinedValidationMessage();
        return joinedValidationMessage.contains(notAdultMessage);
    }

    private boolean shouldIgnoreApartment(final ValidationResult validationResult) {
        final String noResettlementNotificationMessage = messageSource.getMessage(
            PersonalDocumentPersonValidator.NO_RESETTLEMENT_NOTIFICATION_MESSAGE, null, Locale.getDefault()
        );
        final String joinedValidationMessage = validationResult.getJoinedValidationMessage();
        return joinedValidationMessage.contains(noResettlementNotificationMessage);
    }

    private ExternalRestCheckPersonalDocumentDto getPersonalDocumentCheckResponseNotApplicantDto() {
        final ExternalRestCheckPersonalDocumentDto checkDto = new ExternalRestCheckPersonalDocumentDto();
        checkDto.setIsPossible(false);
        checkDto.setReason(messageSource.getMessage(NOT_APPLICANT_MESSAGE, null, Locale.getDefault()));
        return checkDto;
    }

    private ExternalRestCheckPersonalDocumentDto getPersonalDocumentCheckResponseNotAdultApplicantDto() {
        final ExternalRestCheckPersonalDocumentDto checkDto = new ExternalRestCheckPersonalDocumentDto();
        checkDto.setIsPossible(false);
        checkDto.setReason(
            messageSource.getMessage(PersonalDocumentPersonValidator.NOT_ADULT_MESSAGE, null, Locale.getDefault())
        );
        return checkDto;
    }

    private ExternalRestCheckPersonalDocumentDto getPersonalDocumentCheckResponseDto(
        final List<ExternalRestApartmentPersonalDocumentDto> apartmentDtos,
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        final Boolean isPossible = !apartmentDtos.isEmpty();

        final ExternalRestCheckPersonalDocumentDto checkDto = new ExternalRestCheckPersonalDocumentDto();

        checkDto.setIsPossible(isPossible);
        if (!isPossible) {
            checkDto.setReason(validationResults
                .stream()
                .map(Pair::getLeft)
                .map(GenericValidationResult::getFirstValidationMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null));
        } else {
            checkDto.setApartments(apartmentDtos);
        }
        return checkDto;
    }
}
