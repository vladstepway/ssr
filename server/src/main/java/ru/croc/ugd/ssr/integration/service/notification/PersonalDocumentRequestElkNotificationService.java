package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequestType;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentOwnerType;
import ru.croc.ugd.ssr.personaldocument.RequestedDocumentType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentTypeService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис отправки сообщений в ЭЛК для запроса документов.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PersonalDocumentRequestElkNotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;
    private final SsrPersonalDocumentTypeService ssrPersonalDocumentTypeService;

    public void sendNotification(
        final PersonDocument personDocument, final PersonalDocumentRequestDocument personalDocumentRequestDocument
    ) {
        try {
            final Map<String, String> extraTemplateParams = getExtraTemplateParams(personalDocumentRequestDocument);
            elkUserNotificationService.sendPersonalDocumentRequestNotification(personDocument, extraTemplateParams);
        } catch (Exception e) {
            log.warn(
                "Unable to send notification for personal document request: {}",
                personalDocumentRequestDocument.getId(),
                e
            );
        }
    }

    private Map<String, String> getExtraTemplateParams(
        final PersonalDocumentRequestDocument personalDocumentRequestDocument
    ) {
        final Map<String, String> extraTemplateParams = new HashMap<>();
        extraTemplateParams.put("$documentTypes", retrieveDocumentTypes(personalDocumentRequestDocument));
        return extraTemplateParams;
    }

    private String retrieveDocumentTypes(final PersonalDocumentRequestDocument personalDocumentRequestDocument) {
        final PersonalDocumentRequestType personalDocumentRequest = ofNullable(personalDocumentRequestDocument)
            .map(PersonalDocumentRequestDocument::getDocument)
            .map(PersonalDocumentRequest::getPersonalDocumentRequestData)
            .orElse(null);

        final List<String> tenantDocuments = ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequestType::getOwners)
            .map(PersonalDocumentRequestType.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::retrieveRequestedTenantDocuments)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());

        final String titleDocuments = ofNullable(personalDocumentRequest)
            .map(PersonalDocumentRequestType::getDocuments)
            .map(PersonalDocumentRequestType.Documents::getDocument)
            .map(this::retrieveRequestedTitleDocuments)
            .filter(StringUtils::isNotBlank)
            .map(titleDocumentsText -> tenantDocuments.isEmpty()
                ? titleDocumentsText.concat(".")
                : titleDocumentsText.concat(";<br>")
            )
            .orElse("");

        if (!tenantDocuments.isEmpty()) {
            return titleDocuments.concat(
                "&nbsp;&bull;&nbsp;Личные документы:<br>" + String.join("<br>", tenantDocuments)
            );
        } else {
            return titleDocuments;
        }
    }

    private String retrieveRequestedTitleDocuments(final List<RequestedDocumentType> documents) {
        return documents
            .stream()
            .map(this::retrieveTitleDocumentTypeAndCommentText)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(";<br>"));
    }

    private String retrieveTitleDocumentTypeAndCommentText(final RequestedDocumentType requestedDocumentType) {
        return retrieveTypeAndCommentText("&nbsp;&bull;&nbsp;", requestedDocumentType);
    }

    private String retrieveTenantDocumentTypeAndCommentText(final RequestedDocumentType requestedDocumentType) {
        return retrieveTypeAndCommentText("&nbsp;&nbsp;&nbsp;&#8212;&nbsp;", requestedDocumentType);
    }

    private String retrieveTypeAndCommentText(final String offset, final RequestedDocumentType requestedDocumentType) {
        if (nonNull(requestedDocumentType) && StringUtils.isNotEmpty(requestedDocumentType.getTypeCode())) {
            final String typeAndCommentText = ssrPersonalDocumentTypeService
                .getNameByCode(requestedDocumentType.getTypeCode());

            final String lineText = StringUtils.isNotEmpty(requestedDocumentType.getComment())
                ? typeAndCommentText.concat(" (" + requestedDocumentType.getComment() + ")")
                : typeAndCommentText;
            return offset.concat(lineText);
        }
        return null;
    }

    private String retrieveRequestedTenantDocuments(final RequestedDocumentOwnerType requestedDocumentOwnerType) {
        final Optional<PersonDocument> optionalPersonDocument = ofNullable(requestedDocumentOwnerType)
            .map(RequestedDocumentOwnerType::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById);

        final String fio = optionalPersonDocument
            .map(PersonUtils::getFullName)
            .orElse(null);

        final String documentTypes = ofNullable(requestedDocumentOwnerType)
            .map(RequestedDocumentOwnerType::getDocuments)
            .map(RequestedDocumentOwnerType.Documents::getDocument)
            .map(this::retrieveRequestedTenantDocuments)
            .filter(StringUtils::isNotBlank)
            .orElse(null);

        if (StringUtils.isNotEmpty(fio) && StringUtils.isNotEmpty(documentTypes)) {
            final LocalDate birthDate = optionalPersonDocument
                .map(PersonDocument::getDocument)
                .map(Person::getPersonData)
                .map(PersonType::getBirthDate)
                .orElse(null);
            final String fioAndBirthDate = nonNull(birthDate)
                ? fio.concat(" (" + birthDate.format(DATE_FORMATTER) + ")") : fio;
            return "&nbsp;&nbsp;&nbsp;<b>" + fioAndBirthDate + "</b>" + ":<br>" + documentTypes + ".";
        } else {
            return null;
        }
    }

    private String retrieveRequestedTenantDocuments(final List<RequestedDocumentType> documents) {
        return documents
            .stream()
            .map(this::retrieveTenantDocumentTypeAndCommentText)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(";<br>"));
    }
}
