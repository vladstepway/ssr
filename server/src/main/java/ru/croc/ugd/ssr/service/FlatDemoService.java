package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.FlatDemo.FlatDemoItem;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatDemoParticipantDto;
import ru.croc.ugd.ssr.dto.flatdemo.RestCreateFlatDemoDto;
import ru.croc.ugd.ssr.dto.flatdemo.RestFlatDemoDto;
import ru.croc.ugd.ssr.integration.service.flows.FlatViewStatusService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.mapper.FlatDemoMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPViewStatusType;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.flatappointment.FlatAppointmentService;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.ldap.model.UserBean;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class FlatDemoService {

    private static final String FLAT_DEMO_RESETTLEMENT_EVENT_ID = "4";
    private static final String FLAT_DEMO_RELOCATION_STATUS_ID = "3";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final FlatDemoMapper flatDemoMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;
    private final FlatAppointmentService flatAppointmentService;
    private final FlatViewStatusService flatViewStatusService;
    private final UserService userService;
    private final ElkUserNotificationService elkUserNotificationService;

    @Transactional
    public RestFlatDemoDto create(@NonNull final RestCreateFlatDemoDto createFlatDemoDto) {
        final String userLogin = userService.getCurrentUserLogin();
        final UserBean userBean = userService.getUserBeanByLogin(userLogin);

        final FlatDemoItem flatDemoItem = createFlatDemoDto.isPerformed()
            ? flatDemoMapper.toFlatDemoItem(createFlatDemoDto, userBean)
            : null;

        final Optional<FlatAppointmentDocument> optionalFlatAppointmentDocument =
            retrieveFlatAppointmentDocument(createFlatDemoDto);

        if (createFlatDemoDto.isPerformed()) {
            final List<PersonDocument> participantDocumentList = retrieveParticipantPersonDocuments(createFlatDemoDto);
            final List<String> participantDocumentIds = retrieveParticipantPersonDocumentIds(createFlatDemoDto);
            final String resettlementHistoryAnnotation = createParticipantSummary(participantDocumentList);
            flatDemoItem.setParticipantSummary(resettlementHistoryAnnotation);

            participantDocumentList.forEach(participantPersonDocument ->
                processParticipantFlatDemo(participantPersonDocument, flatDemoItem, resettlementHistoryAnnotation)
            );

            final String cipId = participantDocumentList
                .stream()
                .map(personDocument -> PersonUtils.getOfferLetter(personDocument, createFlatDemoDto.getLetterId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(PersonType.OfferLetters.OfferLetter::getIdCIP)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

            elkUserNotificationService.sendNotificationFlatInspectionByPersonIds(
                participantDocumentIds, cipId, createFlatDemoDto.getLetterId()
            );

            optionalFlatAppointmentDocument.ifPresent(document ->
                flatAppointmentService.processPerformedAppointment(document, flatDemoItem, participantDocumentList)
            );
        } else {
            optionalFlatAppointmentDocument.ifPresent(flatAppointmentService::processNotPerformedAppointment);
        }

        return flatDemoMapper.toRestFlatDemoDto(
            flatDemoItem,
            optionalFlatAppointmentDocument.orElse(null),
            createFlatDemoDto.getParticipants()
        );
    }

    private Optional<FlatAppointmentDocument> retrieveFlatAppointmentDocument(
        final RestCreateFlatDemoDto createFlatDemoDto
    ) {
        return ofNullable(createFlatDemoDto)
            .map(RestCreateFlatDemoDto::getFlatAppointmentId)
            .map(flatAppointmentDocumentService::fetchDocument);
    }

    private List<PersonDocument> retrieveParticipantPersonDocuments(final RestCreateFlatDemoDto createFlatDemoDto) {
        return ofNullable(createFlatDemoDto)
            .map(RestCreateFlatDemoDto::getParticipants)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(RestFlatDemoParticipantDto::getPersonDocumentId)
            .map(personDocumentService::fetchDocument)
            .collect(Collectors.toList());
    }

    private List<String> retrieveParticipantPersonDocumentIds(final RestCreateFlatDemoDto createFlatDemoDto) {
        return ofNullable(createFlatDemoDto)
            .map(RestCreateFlatDemoDto::getParticipants)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(RestFlatDemoParticipantDto::getPersonDocumentId)
            .collect(Collectors.toList());
    }

    private void processParticipantFlatDemo(
        final PersonDocument participantPersonDocument,
        final FlatDemoItem flatDemoItem,
        final String resettlementHistoryAnnotation
    ) {
        final PersonType participantPerson = participantPersonDocument
            .getDocument()
            .getPersonData();

        participantPerson.setRelocationStatus(FLAT_DEMO_RELOCATION_STATUS_ID);

        participantPersonDocument.addFlatDemoItem(flatDemoItem);

        participantPersonDocument.addResettlementHistory(
            FLAT_DEMO_RESETTLEMENT_EVENT_ID,
            flatDemoItem.getDataId(),
            resettlementHistoryAnnotation
        );

        personDocumentService.updateDocument(
            participantPersonDocument.getId(),
            participantPersonDocument,
            true,
            true,
            null
        );

        final SuperServiceDGPViewStatusType viewStatus = flatDemoMapper
            .toViewStatus(participantPersonDocument, flatDemoItem);
        flatViewStatusService.sendFlatViewStatus(viewStatus, participantPersonDocument);
    }

    private String createParticipantSummary(final List<PersonDocument> personDocuments) {
        final String participantsString = personDocuments.stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(person -> ofNullable(PersonUtils.getFullName(person))
                .map(fullName -> fullName + getFormattedBirthday(person))
                .orElse(null)
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));

        return "Перечень жителей: " + participantsString;
    }

    private String getFormattedBirthday(final PersonType person) {
        return ofNullable(person.getBirthDate())
            .map(DATE_FORMATTER::format)
            .map(birthday -> " (" + birthday + ")")
            .orElse("");
    }

    public List<RestFlatDemoDto> getAll(final String personDocumentId) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);

        final List<FlatDemoItem> flatDemoItemList = retrieveFlatDemoItemList(personDocument);
        final List<PersonDocument> allParticipantDocuments = retrieveAllParticipantPersonDocuments(
            flatDemoItemList, personDocumentId
        );
        allParticipantDocuments.add(personDocument);

        final List<FlatAppointmentDocument> flatAppointmentDocuments = flatAppointmentDocumentService
            .findAll(personDocumentId, true);

        final Map<String, FlatAppointmentDocument> flatAppointmentWithDemoMap =
            retrieveFlatAppointmentWithDemoMap(flatAppointmentDocuments);

        final List<RestFlatDemoDto> flatDemoDtoWithAppointmentList = flatDemoItemList.stream()
            .map(flatDemoItem -> {
                final FlatAppointmentDocument demoFlatAppointmentDocument = flatAppointmentWithDemoMap
                    .get(flatDemoItem.getDataId());

                final List<String> demoParticipantIdList = of(flatDemoItem.getParticipants())
                    .map(FlatDemoItem.Participants::getPersonID)
                    .orElseGet(Collections::emptyList);

                final List<RestFlatDemoParticipantDto> participantDtoList = allParticipantDocuments.stream()
                    .filter(document -> demoParticipantIdList.contains(document.getId()))
                    .map(flatDemoMapper::toRestFlatDemoParticipantDto)
                    .collect(Collectors.toList());

                return flatDemoMapper.toRestFlatDemoDto(flatDemoItem, demoFlatAppointmentDocument, participantDtoList);
            })
            .collect(Collectors.toList());

        final List<RestFlatDemoDto> flatAppointmentDtoWithoutDemoList = flatAppointmentDocuments
            .stream()
            .filter(flatAppointmentDocument -> isNull(flatAppointmentDocument
                .getDocument()
                .getFlatAppointmentData()
                .getDemoId()
            ))
            .map(flatDemoMapper::toRestFlatDemoDto)
            .collect(Collectors.toList());

        flatDemoDtoWithAppointmentList.addAll(flatAppointmentDtoWithoutDemoList);

        return flatDemoDtoWithAppointmentList;
    }

    private Map<String, FlatAppointmentDocument> retrieveFlatAppointmentWithDemoMap(
        final List<FlatAppointmentDocument> flatAppointmentDocuments
    ) {
        return flatAppointmentDocuments
            .stream()
            .filter(flatAppointmentDocument -> nonNull(flatAppointmentDocument
                .getDocument()
                .getFlatAppointmentData()
                .getDemoId()
            ))
            .collect(Collectors.toMap(
                flatAppointmentDocument -> flatAppointmentDocument
                    .getDocument()
                    .getFlatAppointmentData()
                    .getDemoId(),
                Function.identity(),
                (f1, f2) -> f1
            ));
    }

    private List<FlatDemoItem> retrieveFlatDemoItemList(final PersonDocument personDocument) {
        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .map(PersonType::getFlatDemo)
            .map(PersonType.FlatDemo::getFlatDemoItem)
            .orElse(Collections.emptyList());
    }

    private List<PersonDocument> retrieveAllParticipantPersonDocuments(
        final List<FlatDemoItem> flatDemoItemList, final String personDocumentId
    ) {
        return flatDemoItemList.stream()
            .map(FlatDemoItem::getParticipants)
            .map(FlatDemoItem.Participants::getPersonID)
            .flatMap(List::stream)
            .distinct()
            .filter(StreamUtils.not(personDocumentId::equals))
            .map(personDocumentService::fetchDocument)
            .collect(Collectors.toList());
    }
}
