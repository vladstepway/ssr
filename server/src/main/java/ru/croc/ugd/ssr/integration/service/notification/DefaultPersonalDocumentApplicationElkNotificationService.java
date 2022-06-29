package ru.croc.ugd.ssr.integration.service.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.personaldocument.PersonalDocumentApplicationFlowStatus;
import ru.croc.ugd.ssr.integration.command.PersonalDocumentApplicationPublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.PersonalDocumentApplicationEventPublisher;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;

import java.time.LocalDateTime;

/**
 * Сервис отправки сообщений в ЭЛК для загрузки документов.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultPersonalDocumentApplicationElkNotificationService
    implements PersonalDocumentApplicationElkNotificationService {

    private final PersonalDocumentApplicationEventPublisher personalDocumentApplicationEventPublisher;

    @Override
    public void sendStatus(
        final PersonalDocumentApplicationFlowStatus status, final PersonalDocumentApplicationDocument document
    ) {
        final PersonalDocumentApplicationPublishReasonCommand publishReasonCommand
            = createPublishReasonCommand(status, document.getDocument().getPersonalDocumentApplicationData());
        personalDocumentApplicationEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendStatus(final PersonalDocumentApplicationFlowStatus status, final String eno) {
        final PersonalDocumentApplicationPublishReasonCommand publishReasonCommand =
            createPublishReasonCommand(status, eno);
        personalDocumentApplicationEventPublisher.publishStatus(publishReasonCommand);
    }

    @Override
    public void sendToBk(final String message) {
        personalDocumentApplicationEventPublisher.publishToBk(message);
    }

    private PersonalDocumentApplicationPublishReasonCommand createPublishReasonCommand(
        final PersonalDocumentApplicationFlowStatus status, final String eno
    ) {
        final PersonalDocumentApplicationType personalDocumentApplication = new PersonalDocumentApplicationType();
        personalDocumentApplication.setEno(eno);
        return createPublishReasonCommand(status, personalDocumentApplication);
    }

    private PersonalDocumentApplicationPublishReasonCommand createPublishReasonCommand(
        final PersonalDocumentApplicationFlowStatus status,
        final PersonalDocumentApplicationType personalDocumentApplication
    ) {
        return PersonalDocumentApplicationPublishReasonCommand.builder()
            .personalDocumentApplication(personalDocumentApplication)
            .responseReasonDate(LocalDateTime.now())
            .elkStatusText(status.getStatusText())
            .status(status)
            .build();
    }
}
