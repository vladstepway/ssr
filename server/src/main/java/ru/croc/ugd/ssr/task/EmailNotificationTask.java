package ru.croc.ugd.ssr.task;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.reinform.cdp.ldap.model.LogicOperation;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapService;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


/**
 * Компонент отвечающий за отправку уведомлений пользователям.
 */
@Component
@AllArgsConstructor
public class EmailNotificationTask {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationTask.class);
    private static final String MESSAGE_TEMPLATE = "Уважаемый (-ая), %s!<br><br>"
        + "Напоминаем Вам о необходимости внести на портале https://smart.mos.ru/ статистическую информацию о переселении сегодня до 23:59."
        + "<br><br><br>"
        + "--<br>"
        + "С уважением,<br>"
        + "Департамент градостроительной политики и <br>"
        + "строительства города Москвы <br>";
    private final LdapService ldapService;
    private final MailRestApi mailRestApi;

    /**
     * Уведомление пользователей о новой записи дашборда.
     *
     * @param groups список групп для уведомлений
     */
    public void reportNewDashboardItem(List<String> groups) {
        if (isNull(groups)) {
            return;
        }

        List<UserBean> users = ldapService.findUsersArray(groups, null, LogicOperation.OR);

        for (UserBean user : users) {
            if (nonNull(user.getMail())) {
                sendMail(user);
            }
        }
    }

    /**
     * Отправка email сообщения.
     *
     * @param user кому.
     */
    private void sendMail(UserBean user) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setSubject("Статистическая информация о переселении");
        mailRequest.setBody(
            String.format(MESSAGE_TEMPLATE, user.getDisplayName())
        );
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        PersonalAddress to = new PersonalAddress(user.getMail(), user.getFioShort());
        mailRequest.setTo(Collections.singletonList(to));
        try {
            mailRestApi.sendMail(mailRequest);
            LOG.debug("Message was send to: {} {}; Time: {}", to.getEmail(), to.getFio(), LocalDateTime.now());
        } catch (Exception e) {
            LOG.error("Message was send to: {} {}; Time: {}", to.getEmail(), to.getFio(), LocalDateTime.now());
            LOG.error("Error message:" + e.getMessage());
            LOG.error(e.toString());
        }
    }
}
