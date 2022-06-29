package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.ldap.model.LogicOperation;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapService;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.FilestoreFile;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.util.Collections;
import java.util.List;

/**
 * Сервис отправки email.
 */
@Slf4j
@AllArgsConstructor
@Service
public class MailService {

    private final LdapService ldapService;

    private final MailRestApi mailRestApi;

    /**
     * Отправка email сообщения.
     *
     * @param groups Группы в LDAP.
     * @param subject Тема сообщения.
     * @param message Сообщение.
     */
    @Async
    public void sendMail(
        final List<String> groups,
        final String subject,
        final String message
    ) {
        ldapService.findUsersArray(groups, null, LogicOperation.OR)
            .stream()
            .filter(user -> nonNull(user.getMail()))
            .forEach(user -> sendMail(user, subject, message, Collections.emptyList()));
    }

    /**
     * Отправка email сообщения без тела.
     *
     * @param user Кому.
     * @param subject Тема сообщения.
     * @param attachments Вложения.
     */
    @Async
    public void sendMail(
        final UserBean user,
        final String subject,
        final List<FilestoreFile> attachments
    ) {
        sendMail(user, subject, "", attachments);
    }

    /**
     * Отправка email сообщения без тела.
     *
     * @param emailTo Кому.
     * @param subject Тема сообщения.
     * @param attachments Вложения.
     */
    @Async
    public void sendMail(
        final String emailTo,
        final String subject,
        final List<FilestoreFile> attachments
    ) {
        sendMail(new PersonalAddress(emailTo, null), subject, "", attachments);
    }

    private void sendMail(
        final UserBean user,
        final String subject,
        final String message,
        final List<FilestoreFile> attachments
    ) {
        sendMail(new PersonalAddress(user.getMail(), user.getFioShort()), subject, message, attachments);
    }

    private void sendMail(
        final PersonalAddress to,
        final String subject,
        final String message,
        final List<FilestoreFile> attachments
    ) {
        final MailRequest mailRequest = new MailRequest();
        mailRequest.setSubject(subject);
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        mailRequest.setTo(Collections.singletonList(to));
        mailRequest.setBody(message);
        mailRequest.setFilenetAttachments(attachments);
        try {
            mailRestApi.sendMail(mailRequest);
            log.debug("Mail send to: {}, {}", to.getEmail(), to.getFio());
        } catch (Exception e) {
            log.error("Failed to send mail to: {} {}", to.getEmail(), to.getFio(), e);
        }
    }
}
