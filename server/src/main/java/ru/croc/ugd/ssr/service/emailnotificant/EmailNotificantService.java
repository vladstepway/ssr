package ru.croc.ugd.ssr.service.emailnotificant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.reinform.cdp.mail.model.data.PersonalAddress;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Service
public class EmailNotificantService {
    private final String dictionaryName;
    private final DictionaryService dictionaryService;
    private final EmailNotificantConverter emailNotificantConverter;

    private Set<EmailNotificant> emailNotificants;

    public EmailNotificantService(
        final DictionaryService dictionaryService,
        final EmailNotificantConverter emailNotificantConverter,
        @Value("${ugd.ssr.email-notification.dictionary-name:ugd_ssr_notificantEmailsTest}") final String dictName
    ) {
        this.dictionaryService = dictionaryService;
        this.emailNotificantConverter = emailNotificantConverter;
        this.dictionaryName = dictName;
    }

    /**
     * Заполняет типов документов данными из справочника.
     */
    @PostConstruct
    public void afterInit() {
        emailNotificants = emailNotificantConverter.convertElements(
            dictionaryService.getDictionaryElementsAsServiceUser(dictionaryName)
        );
    }

    public List<PersonalAddress> getToPersonalAddressList(
        final NotificationType notificationType, final String areaCode
    ) {
        return getEmailNotificantsByNotificationTypeAndAreaCode(notificationType, areaCode)
            .stream()
            .map(this::toPersonalAddress)
            .collect(Collectors.toList());
    }

    public List<PersonalAddress> getCcPersonalAddressList(
        final NotificationType notificationType
    ) {
        return getCcEmailNotificantsByNotificationType(notificationType)
            .stream()
            .map(this::toPersonalAddress)
            .collect(Collectors.toList());
    }

    private List<EmailNotificant> getEmailNotificantsByNotificationTypeAndAreaCode(
        final NotificationType notificationType, final String areaCode
    ) {
        return emailNotificants.stream()
            .filter(type -> Objects.equals(areaCode, type.getAreaCode())
                && notificationType == type.getNotificationType())
            .collect(Collectors.toList());
    }

    private List<EmailNotificant> getCcEmailNotificantsByNotificationType(
        final NotificationType notificationType
    ) {
        return emailNotificants.stream()
            .filter(type -> type.isCc() && notificationType == type.getNotificationType())
            .collect(Collectors.toList());
    }

    private PersonalAddress toPersonalAddress(final EmailNotificant emailNotificant) {
        final PersonalAddress personalAddress = new PersonalAddress();
        personalAddress.setFio(emailNotificant.getFullName());
        personalAddress.setEmail(emailNotificant.getEmail());
        return personalAddress;
    }

}
