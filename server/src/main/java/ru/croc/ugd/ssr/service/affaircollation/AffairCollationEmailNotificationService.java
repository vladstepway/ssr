package ru.croc.ugd.ssr.service.affaircollation;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.affaircollation.AffairCollationData;
import ru.croc.ugd.ssr.db.dao.ChangelogDao;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.service.AbstractEmailNotificationService;
import ru.croc.ugd.ssr.service.UserService;
import ru.reinform.cdp.mail.api.MailRestApi;

@Slf4j
@Service
public class AffairCollationEmailNotificationService
    extends AbstractEmailNotificationService<AffairCollationDocument> {

    private static final String NO_INFORMATION = "нет сведений";

    private final ChangelogDao changelogDao;
    private final UserService userService;

    @Value("${ugd.ssr.email-notification.affair-collation.subject:Перезапрос данных из ДГИ}")
    private String affairCollationEmailNotificationSubject;

    @Value("${ugd.ssr.email-notification.affair-collation.body:Из АИС РСМ получен ответ на ваш"
        + " перезапрос сведений по unom=%s, квартира=%s, affairId=%s.<br>Номера сообщений: %s.}")
    private String affairCollationEmailNotificationBody;

    public AffairCollationEmailNotificationService(
        final MailRestApi mailRestApi,
        final ChangelogDao changelogDao,
        final UserService userService
    ) {
        super(mailRestApi);
        this.changelogDao = changelogDao;
        this.userService = userService;
    }

    @Override
    public void sendNotificationEmail(final AffairCollationDocument document) {
        log.debug("affairCollationSendNotificationEmail started");

        final AffairCollationData affairCollation = document.getDocument().getAffairCollationData();

        changelogDao.findCreatedBy(document.getId())
            .map(userService::getUserBeanByLogin)
            .ifPresent(userBean -> sendNotificationEmail(
                affairCollationEmailNotificationSubject,
                createNotificationBody(affairCollation),
                userBean
            ));

        log.debug("affairCollationSendNotificationEmail finished");
    }

    private String createNotificationBody(final AffairCollationData affairCollation) {
        final String unom = ofNullable(affairCollation.getUnom()).orElse(NO_INFORMATION);
        final String flatNumber = ofNullable(affairCollation.getFlatNumber()).orElse(NO_INFORMATION);
        final String affairId = ofNullable(affairCollation.getAffairId()).orElse(NO_INFORMATION);

        final String serviceNumbers = (affairCollation.getResponseServiceNumbers().isEmpty())
            ? NO_INFORMATION
            : String.join(", ", affairCollation.getResponseServiceNumbers());

        return String.format(affairCollationEmailNotificationBody, unom, flatNumber, affairId, serviceNumbers);
    }
}
