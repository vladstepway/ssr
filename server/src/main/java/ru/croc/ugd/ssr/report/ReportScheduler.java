package ru.croc.ugd.ssr.report;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.config.ReportProperties;
import ru.croc.ugd.ssr.config.SentMessagesStatisticsReportProperties;
import ru.croc.ugd.ssr.service.MailService;
import ru.croc.ugd.ssr.service.UserService;
import ru.reinform.cdp.ldap.model.LogicOperation;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapService;
import ru.reinform.cdp.mail.model.data.FilestoreFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Сервис запуска формирования отчета по расписанию.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(ReportScheduler.class);

    private final FlowsReport flowsReport;

    private final LdapService ldapService;

    private final SentMessageStatisticsReport sentMessageStatisticsReport;

    private final MailService mailService;

    private final ReportProperties reportProperties;

    private final SentMessagesStatisticsReportProperties sentMessagesStatisticsReportProperties;

    private final UserService userService;

    /**
     * Отправка отчета.
     */
    //@Scheduled(cron = "${ugd.ssr.report.schedule}")
    public void sendReport() {

        final LocalDate today = LocalDate.now();
        log.info("Start create daily report ssr {}", today);

        final String fileId = flowsReport.createReport(today);

        List<UserBean> users = ldapService
            .findUsersArray(Collections.singletonList(reportProperties.getReportRole()), null, LogicOperation.OR);

        users.forEach(user -> {
            if (nonNull(user.getMail())) {
                sendMail(user, fileId, today);
            }
        });

    }

    /**
     * Отправка отчета со статистикой по уведомлениям жителей.
     */
    @Scheduled(cron = "${ugd.ssr.sent-messages-statistics-report.schedule}")
    public void sentMessagesStatisticsReport() {
        if (!sentMessagesStatisticsReportProperties.isEnabled()) {
            LOG.info("Отправка отчета со статистикой по уведомлениям жителей отключена");
            return;
        }

        LOG.info("Начало формирования отчета со статистикой по уведомлениям жителей");

        final String fileId = sentMessageStatisticsReport.createReport();
        LOG.info("Отчет сформирован, fileId = " + fileId);

        LOG.info("Начало отправки сформированного отчета "
            + String.join(", ", sentMessagesStatisticsReportProperties.getEmails()));

        sentMessagesStatisticsReportProperties.getEmails().forEach(emailTo -> sendMail(emailTo, fileId));
    }

    public void sendDefectReport(final List<String> logins, final String fileId) {
        final LocalDate today = LocalDate.now();
        final List<UserBean> users = userService.getAllLdapUsers(logins);

        users.forEach(user -> {
            if (nonNull(user.getMail())) {
                sendMail(user, fileId, today);
            }
        });
    }

    /**
     * Отправка email сообщения.
     *
     * @param user
     *            кому.
     * @param fileId
     *            ид файла.
     */
    private void sendMail(UserBean user, String fileId, LocalDate date) {
        final String subject = "Отчет за " + date;
        final List<FilestoreFile> attachments = Collections.singletonList(new FilestoreFile(fileId));
        mailService.sendMail(user, subject, attachments);
    }

    private void sendMail(String emailTo, String fileId) {
        String subject = "Отчет со статистикой по уведомлениям";
        final List<FilestoreFile> attachments = Collections.singletonList(new FilestoreFile(fileId));
        mailService.sendMail(emailTo, subject, attachments);
    }

}
