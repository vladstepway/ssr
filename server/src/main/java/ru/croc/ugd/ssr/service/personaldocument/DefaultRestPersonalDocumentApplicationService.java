package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.PersonUtils.getFullName;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentApplicationDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.PersonalDocumentApplicationMapper;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.service.MailService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentDocumentService;
import ru.reinform.cdp.bpm.model.Task;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestPersonalDocumentApplicationService implements RestPersonalDocumentApplicationService {

    private static final String ACCEPT_DOCUMENTS_TASK_DEFINITION_KEY = "ugdssrDocument_acceptDocuments_task";
    private static final String UGD_SSR_PERSONAL_DOCUMENT_APPLICATION_READ =
        "UGD_SSR_PERSONAL_DOCUMENT_APPLICATION_READ";
    private static final String ACCEPT_DOCUMENTS = "Принять документы от жителя";
    private static final String ACCEPT_DOCUMENTS_MSG_TEMPLATE = "Добрый день!<br/>"
        + "Поступил пакет документов, необходимых для организации переселения по Программе реновации "
        + "от %s по заявлению %s.<br/>Вам необходимо проверить поступившие документы и взять в работу."
        + " Для этого перейдите по ссылке: %s.";
    private static final String LINK_TEMPLATE = "<a href=\"%s\" target=\"_blank\">%s</a>";
    private static final String UGDSSR_DOCUMENTS_PREFIX = "/ugdssrDocument";

    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonalDocumentDocumentService personalDocumentDocumentService;
    private final PersonalDocumentApplicationMapper personalDocumentApplicationMapper;
    private final BpmService bpmService;
    private final SystemProperties systemProperties;
    private final MailService mailService;

    @Override
    public RestPersonalDocumentApplicationDto fetchById(final String id) {
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument =
            personalDocumentApplicationDocumentService.fetchDocument(id);
        final PersonalDocumentDocument personalDocumentDocument = personalDocumentDocumentService
            .findByApplicationDocumentId(id)
            .orElseThrow(() -> new SsrException("Не найдены документы для заявления " + id));

        return personalDocumentApplicationMapper.toRestPersonalDocumentApplicationDto(
            personalDocumentApplicationDocument, personalDocumentDocument
        );
    }

    @Override
    public void acceptById(final String id) {
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument =
            personalDocumentApplicationDocumentService.fetchDocument(id);

        final PersonalDocumentDocument personalDocumentDocument = personalDocumentDocumentService
            .findByApplicationDocumentId(id)
            .orElseThrow(() -> new SsrException("Не найдены документы для заявления " + id));

        addAcceptanceDateTime(personalDocumentDocument);

        personalDocumentDocumentService.updateDocument(
            personalDocumentDocument.getId(), personalDocumentDocument, true, true, null
        );

        final PersonalDocumentApplicationType personalDocumentApplication = personalDocumentApplicationDocument
            .getDocument()
            .getPersonalDocumentApplicationData();
        finishBpmProcess(personalDocumentApplication.getProcessInstanceId());
    }

    @Override
    public List<RestPersonalDocumentApplicationDto> findAll(
        final String personDocumentId, final boolean includeRequestedApplications
    ) {
        final List<PersonalDocumentApplicationDocument> personalDocumentApplicationDocuments =
            personalDocumentApplicationDocumentService.findAll(personDocumentId, includeRequestedApplications);
        return personalDocumentApplicationMapper.toRestPersonalDocumentApplicationDtos(
            personalDocumentApplicationDocuments, this::retrievePersonalDocument
        );
    }

    @Override
    public void sendEmail(final String id) {
        final PersonalDocumentApplicationType personalDocumentApplicationData =
            personalDocumentApplicationDocumentService.fetchDocument(id)
                .getDocument()
                .getPersonalDocumentApplicationData();

        final String personalDocumentApplicationEno = personalDocumentApplicationData.getEno();
        final String personDocumentId = personalDocumentApplicationData.getPersonDocumentId();
        final String fullName = getFullName(
            personalDocumentApplicationData.getLastName(),
            personalDocumentApplicationData.getFirstName(),
            personalDocumentApplicationData.getMiddleName()
        );
        final String processInstanceId = personalDocumentApplicationData.getProcessInstanceId();

        final Task task = bpmService.getTasksByProcessId(processInstanceId)
            .stream()
            .filter(activeTask -> ACCEPT_DOCUMENTS_TASK_DEFINITION_KEY.equals(activeTask.getTaskDefinitionKey()))
            .findFirst()
            .orElseThrow(() -> new SsrException("Не найдена задача по идентификатору процесса: " + processInstanceId));

        final String applicationLink = buildSsrLink("personal-document-applications", id);
        final String personCardLink = buildSsrLink("person", personDocumentId);
        final String taskLink = buildSsrLink("execution", task.getId(), task);

        final String message = String.format(
            ACCEPT_DOCUMENTS_MSG_TEMPLATE,
            String.format(LINK_TEMPLATE, personCardLink, fullName),
            String.format(LINK_TEMPLATE, applicationLink, personalDocumentApplicationEno),
            String.format(LINK_TEMPLATE, taskLink, ACCEPT_DOCUMENTS)
        );

        mailService.sendMail(
            Collections.singletonList(UGD_SSR_PERSONAL_DOCUMENT_APPLICATION_READ), ACCEPT_DOCUMENTS, message
        );
    }

    private String buildSsrLink(final String type, final String id) {
        return buildSsrLink(type, id, null);
    }

    private String buildSsrLink(final String type, final String id, final Task task) {
        final String baseUri = systemProperties.getDomain() + "/ugd/ssr/#/app/";
        switch (type) {
            case "person":
            case "personal-document-applications":
                return baseUri + type + "/" + id;
            case "execution":
                return task != null
                    ? baseUri + type + UGDSSR_DOCUMENTS_PREFIX + "/" + task.getId() + UGDSSR_DOCUMENTS_PREFIX
                    + task.getFormKey() + "?systemCode=UGD"
                    : "-";
            default:
                return "-";
        }
    }

    private void addAcceptanceDateTime(final PersonalDocumentDocument personalDocumentDocument) {
        ofNullable(personalDocumentDocument)
            .map(PersonalDocumentDocument::getDocument)
            .map(PersonalDocument::getPersonalDocumentData)
            .ifPresent(personalDocument -> personalDocument.setAcceptanceDateTime(LocalDateTime.now()));
    }

    private PersonalDocumentDocument retrievePersonalDocument(
        final PersonalDocumentApplicationDocument personalDocumentApplicationDocument
    ) {
        return ofNullable(personalDocumentApplicationDocument)
            .map(PersonalDocumentApplicationDocument::getId)
            .flatMap(personalDocumentDocumentService::findByApplicationDocumentId)
            .orElse(null);
    }

    private void finishBpmProcess(final String processInstanceId) {
        if (StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn(
                    "Unable to finish bpm process for personal document application due to: {}", e.getMessage(), e
                );
            }
        }
    }
}
