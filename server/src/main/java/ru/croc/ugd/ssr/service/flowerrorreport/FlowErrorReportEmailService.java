package ru.croc.ugd.ssr.service.flowerrorreport;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.config.ReportProperties;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.FilestoreFile;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class FlowErrorReportEmailService {
    private static final String MESSAGE_TEMPLATE = "Добрый день!<br>"
        + "Во вложении отчеты об ошибках получения данных из ДГИ за %s.<br>"
        + "Это письмо сформировано автоматически, отвечать на него не требуется";

    private final ReportProperties reportProperties;
    private final MailRestApi mailRestApi;

    public void sendFlowErrorReportMail(final String fileStoreId, final LocalDate reportsDate) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setSubject("Отчеты об ошибках получения данных из ДГИ.");
        mailRequest.setBody(
            String.format(MESSAGE_TEMPLATE, reportsDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
        );
        mailRequest.setFilenetAttachments(Collections.singletonList(new FilestoreFile(fileStoreId)));
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        mailRequest.setTo(getRecipients());
        try {
            mailRestApi.sendMail(mailRequest);
        } catch (Exception e) {
            log.error("Couldn't send flow reports email. Error message:" + e.getMessage(), e);
        }
    }

    private List<PersonalAddress> getRecipients() {
        return reportProperties
            .getFlowErrorEmails()
            .stream()
            .map(emailToSend -> new PersonalAddress(emailToSend, StringUtils.EMPTY))
            .collect(Collectors.toList());
    }
}
