package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentRequestDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentRequestDto;
import ru.croc.ugd.ssr.integration.service.notification.PersonalDocumentRequestElkNotificationService;
import ru.croc.ugd.ssr.mapper.PersonalDocumentApplicationMapper;
import ru.croc.ugd.ssr.mapper.PersonalDocumentRequestMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequestType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentRequestDocumentService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestPersonalDocumentRequestService implements RestPersonalDocumentRequestService {

    private final PersonalDocumentRequestDocumentService personalDocumentRequestDocumentService;
    private final PersonalDocumentRequestMapper personalDocumentRequestMapper;
    private final PersonDocumentService personDocumentService;
    private final PersonalDocumentRequestElkNotificationService personalDocumentRequestElkNotificationService;
    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonalDocumentDocumentService personalDocumentDocumentService;
    private final PersonalDocumentApplicationMapper personalDocumentApplicationMapper;
    private final UserService userService;

    @Override
    public RestPersonalDocumentRequestDto create(final RestCreatePersonalDocumentRequestDto body) {
        final String initiatorLogin = userService.getCurrentUserLogin();

        final PersonalDocumentRequestDocument personalDocumentRequestDocument = personalDocumentRequestMapper
            .toPersonalDocumentRequestDocument(body, this::retrievePerson, initiatorLogin);

        personalDocumentRequestDocumentService.createDocument(personalDocumentRequestDocument, true, null);

        final PersonDocument personDocument = personDocumentService.fetchDocument(body.getPersonDocumentId());
        personalDocumentRequestElkNotificationService.sendNotification(personDocument, personalDocumentRequestDocument);

        return personalDocumentRequestMapper.toRestPersonalDocumentRequestDto(personalDocumentRequestDocument);
    }

    @Override
    public List<RestPersonalDocumentRequestDto> findAll(final String personDocumentId) {
        final List<PersonalDocumentRequestDocument> personalDocumentRequestDocuments =
            personalDocumentRequestDocumentService.findByPersonDocumentId(personDocumentId);
        return personalDocumentRequestMapper.toRestPersonalDocumentRequestDtos(
            personalDocumentRequestDocuments,
            this::retrievePersonalDocumentApplication,
            this::toRestPersonalDocumentApplicationDto
        );
    }

    private Person retrievePerson(final String personDocumentId) {
        return personDocumentService.fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .orElse(null);
    }

    private PersonalDocumentApplicationDocument retrievePersonalDocumentApplication(
        final PersonalDocumentRequestDocument personalDocumentRequestDocument
    ) {
        return ofNullable(personalDocumentRequestDocument)
            .map(PersonalDocumentRequestDocument::getDocument)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .map(PersonalDocumentRequestType::getApplicationDocumentId)
            .map(personalDocumentApplicationDocumentService::fetchDocument)
            .orElse(null);
    }

    private RestPersonalDocumentApplicationDto toRestPersonalDocumentApplicationDto(
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument
    ) {
        final PersonalDocumentDocument personalDocumentDocument = retrievePersonalDocument(
            personalDocumentApplicationDocument
        );
        return personalDocumentApplicationMapper.toRestPersonalDocumentApplicationDto(
            personalDocumentApplicationDocument, personalDocumentDocument
        );
    }

    private PersonalDocumentDocument retrievePersonalDocument(
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument
    ) {
        return ofNullable(personalDocumentApplicationDocument)
            .map(PersonalDocumentApplicationDocument::getId)
            .flatMap(personalDocumentDocumentService::findByApplicationDocumentId)
            .orElse(null);
    }
}
