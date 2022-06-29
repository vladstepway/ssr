package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FirstFlowErrorAnalyticsData;
import ru.croc.ugd.ssr.integration.variable.ElkMessages;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.report.FirstFlowErrorAnalyticsReport;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.FilestoreFile;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Сервис по отправке email.
 */
@Service
public class SendEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailService.class);

    private final ElkMessages elkMessages;
    private final MailRestApi mailRestApi;
    private final UserService userService;
    private final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService;
    private final FirstFlowErrorAnalyticsReport firstFlowErrorAnalyticsReport;

    private final String username = "shtopor52@gmail.com";
    private final String password = "sznkkcueykhnciwa";

    @Value("${integration.notification.resettlementRole:UGD_SSR_RESETTLEMENT_AUTHORITY}")
    private String resettlementRole;

    public SendEmailService(
        final ElkMessages elkMessages,
        final MailRestApi mailRestApi,
        final UserService userService,
        @Lazy final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService,
        @Lazy final FirstFlowErrorAnalyticsReport firstFlowErrorAnalyticsReport
    ) {
        this.elkMessages = elkMessages;
        this.mailRestApi = mailRestApi;
        this.userService = userService;
        this.firstFlowErrorAnalyticsService = firstFlowErrorAnalyticsService;
        this.firstFlowErrorAnalyticsReport = firstFlowErrorAnalyticsReport;
    }

    /**
     * Отправить email с сообщением по скорому переезду.
     *
     * @param emailTo Адресат
     */
    public void sendEmailStartRenovation(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationStartRenovation(
                "Тест Тестович",
                "Москва, улица Тестовая-Жилая, дом №5",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "8(499)499-99-94"
            )
        );
    }

    /**
     * Отправить email с сообщением о получении письма с предложением.
     *
     * @param emailTo Адресат
     */
    public void sendEmailOfferLetter(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationOfferLetter(
                "Тест Тестович",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "8(499)499-99-94",
                "https://doc-upload.mos.ru/uform3.0/service/getcontent?os=GU_DOCS&id=8fbc8b87-1c8a-4a8c-b7aa-13681f08db4c"
            )
        );
    }

    /**
     * Отправить email с сообщением о согласии на квартиру.
     *
     * @param emailTo Адресат
     */
    public void sendEmailFlatAgreement(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationFlatAgreement(
                "Тест Тестович"
            )
        );
    }

    /**
     * Отправить email с сообщением об отказе на квартиру.
     *
     * @param emailTo Адресат
     */
    public void sendEmailFlatRefusal(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationFlatRefusal(
                "Тест Тестович",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "8(499)499-99-94"
            )
        );
    }

    /**
     * Отправить email с сообщением после осмотра квартиры.
     *
     * @param emailTo Адресат
     * @param fileUrl Ссылка на файл в ЦХЭД
     */
    public void sendEmailFlatInspection(String emailTo, String fileUrl) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationFlatInspection(
                "Тест Тестович",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "8(499)499-99-94",
                fileUrl
            )
        );
    }

    /**
     * Отправить email с сообщением о подписанном договоре.
     *
     * @param emailTo Адресат
     */
    public void sendEmailSignedContract(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationSignedContract(
                "Тест Тестович",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "Москва, улица Тестовая-Уголовная, дом №7",
                "Москва, улица Тестовая-Новая, дом №6",
                "Москва, улица Тестовая-Старая, дом №5"
            )
        );
    }

    /**
     * Отправить email с сообщением о готовности договора.
     *
     * @param emailTo Адресат
     */
    public void sendEmailContractReady(String emailTo) {
        sendNotificationEmail(
            emailTo,
            elkMessages.getNotificationContractReady(
                "Тест Тестович",
                "Москва, улица Тестовая-Регистрационная, дом №2",
                "8(499)499-99-94"
            )
        );
    }

    private Session getGmailSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return Session.getInstance(
            prop,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }
        );
    }

    private void sendNotificationEmail(String emailTo, String content) {
        try {
            Message message = new MimeMessage(getGmailSession());
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(emailTo)
            );
            message.setSubject("Testing Gmail");
            message.setContent(content, "text/html; charset=UTF-8");

            sendEmail(message);
        } catch (MessagingException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEmail(Message message) throws MessagingException {
        Transport.send(message);
        LOG.info("Email message send");
    }

    /**
     * Отправить сообщение об успешном обогащении.
     *
     * @param body    тело письма
     * @param subject тема письма
     */
    public void sendSuccessUpdateRealEstateEmail(String body, String subject) {
        List<UserBean> users = userService.getUsersByRole(resettlementRole);

        for (UserBean user : users) {
            if (nonNull(user.getMail())) {
                MailRequest mailRequest = new MailRequest();
                mailRequest.setSubject(subject);
                mailRequest.setBody(body);
                sendMail(mailRequest, user.getMail(), user.getFioShort());
            }
        }
    }

    /**
     * Отправка отчета с обработкой ошибок от ДГИ.
     *
     * @param id     ID задачи-документа на обработку ошибок
     * @param fileId ID отчета в альфреске (если он уже сформирован)
     */
    public void sendEmailFirstFlowErrorAnalyticsReport(String id, String fileId) {
        FirstFlowErrorAnalyticsDocument firstFlowErrorAnalyticsDocument
            = firstFlowErrorAnalyticsService.fetchDocument(id);
        FirstFlowErrorAnalyticsData data = firstFlowErrorAnalyticsDocument.getDocument().getData();

        if (StringUtils.isBlank(fileId)) {
            fileId = firstFlowErrorAnalyticsReport.createReport(id);
        }
        LOG.info("fileId of report = " + fileId);

        List<UserBean> users = userService.getUsersByRole("UGD_SSR_FIRST_FLOW_ERROR_REPORT");

        for (UserBean user : users) {
            if (nonNull(user.getMail())) {
                MailRequest mailRequest = new MailRequest();
                mailRequest.setSubject("Результат разбора ошибок обогащения дома с UNOM " + data.getUnom());
                mailRequest.setFilenetAttachments(Collections.singletonList(new FilestoreFile(fileId)));
                sendMail(mailRequest, user.getMail(), user.getFioShort());
            }
        }
    }

    /**
     * Отправить сообщение о начале обогащения.
     *
     * @param unom     УНОМ дома
     * @param username ФИО пользователя
     */
    public void sendEmailStartResettlement(String unom, String username) {
        List<UserBean> users = userService.getUsersByRole(resettlementRole);

        for (UserBean user : users) {
            if (nonNull(user.getMail())) {
                MailRequest mailRequest = new MailRequest();
                mailRequest.setSubject("Запуск обогащения дома с UNOM " + unom);
                mailRequest.setBody(
                    LocalDateTime.now()
                        + " пользователем "
                        + username
                        + " было инициировано обогащение данных по дому с УНОМ = " + unom
                );
                sendMail(mailRequest, user.getMail(), user.getFioShort());
            }
        }
    }

    /**
     * Отправить сообщение об ошибке обогащения из МОС.РУ.
     *
     * @param unom  УНОМ дома
     * @param error ошибка
     */
    public void sendEmailErrorResettlement(String unom, String error) {
        List<UserBean> users = userService.getUsersByRole(resettlementRole);

        for (UserBean user : users) {
            if (nonNull(user.getMail())) {
                MailRequest mailRequest = new MailRequest();
                mailRequest.setSubject("Ошибка обогащения дома с UNOM " + unom);
                mailRequest.setBody(
                    LocalDateTime.now()
                        + " при обогащении данных по дому с УНОМ = "
                        + unom + " из mos.ru возникла ошибка "
                        + error
                );
                sendMail(mailRequest, user.getMail(), user.getFioShort());
            }
        }
    }

    private void sendMail(MailRequest mailRequest, String emailTo, String fio) {
        PersonalAddress to = new PersonalAddress(emailTo, fio);
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        mailRequest.setTo(Collections.singletonList(to));
        try {
            mailRestApi.sendMail(mailRequest);
            LOG.debug("Message was send to: " + to + "; Time:" + LocalDateTime.now());
        } catch (Exception e) {
            LOG.error("Messages wasn't send to: " + to + "; Time:" + LocalDateTime.now());
            LOG.error("Error message:" + e.getMessage());
            LOG.error(e.toString());
        }
    }
}
