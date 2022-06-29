package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public abstract class AbstractEmailNotificationService<T extends DocumentAbstract>
    implements EmailNotificationService<T> {

    private final MailRestApi mailRestApi;

    protected void sendNotificationEmail(final String subject, final String body, final UserBean userBean) {
        if (isNull(userBean.getMail())) {
            log.info("sendNotificationEmail: unable to send email, notificant email is empty");
            return;
        }
        sendNotificationEmail(subject, body, new PersonalAddress(userBean.getMail(), userBean.getFioShort()));
    }

    protected void sendNotificationEmail(final String subject, final String body, final PersonalAddress to) {
        sendNotificationEmail(subject, body, Collections.singletonList(to));
    }

    protected void sendNotificationEmail(final String subject, final String body, final List<PersonalAddress> to) {
        sendNotificationEmail(subject, body, to, null);
    }

    protected void sendNotificationEmail(
        final String subject, final String body, final List<PersonalAddress> to, final List<PersonalAddress> cc
    ) {
        final MailRequest mailRequest = new MailRequest();
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        if (nonNull(cc)) {
            to.addAll(cc);
        }
        if (to.isEmpty()) {
            log.info("sendNotificationEmail: unable to send email, notificant list is empty");
            return;
        }
        mailRequest.setTo(to);
        final String mailList = of(to)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(PersonalAddress::getEmail)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(","));
        try {
            mailRestApi.sendMail(mailRequest);
            log.info("sendNotificationEmail: was sent to {}, subject = {}, body = {}", mailList, subject, body);
        } catch (Exception e) {
            log.info("sendNotificationEmail: unable to send", e);
        }
    }

}
