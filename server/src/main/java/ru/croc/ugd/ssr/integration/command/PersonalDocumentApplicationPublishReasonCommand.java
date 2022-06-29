package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.personaldocument.PersonalDocumentApplicationFlowStatus;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;

import java.time.LocalDateTime;

/**
 * Команда для отправки статусов в очередь для заявления на предоставление документов.
 */
@Data
@Builder(toBuilder = true)
public class PersonalDocumentApplicationPublishReasonCommand {

    private PersonalDocumentApplicationType personalDocumentApplication;

    private PersonalDocumentApplicationFlowStatus status;

    private String elkStatusText;

    private LocalDateTime responseReasonDate;
}
