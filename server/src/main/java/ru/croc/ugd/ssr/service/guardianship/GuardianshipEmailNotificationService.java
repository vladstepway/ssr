package ru.croc.ugd.ssr.service.guardianship;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.service.AbstractEmailNotificationService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.emailnotificant.EmailNotificantService;
import ru.croc.ugd.ssr.service.emailnotificant.NotificationType;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;

import java.util.List;

@Slf4j
@Service
public class GuardianshipEmailNotificationService
    extends AbstractEmailNotificationService<GuardianshipRequestDocument> {

    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final EmailNotificantService emailNotificantService;
    private final SystemProperties systemProperties;

    @Value("${ugd.ssr.email-notification.guardianship.enabled:false}")
    private boolean guardianshipEmailNotificationEnabled;
    @Value("${ugd.ssr.email-notification.guardianship.subject:"
        + "Новое заявление в органы опеки на согласование сделки}")
    private String guardianshipEmailNotificationSubject;
    @Value("${ugd.ssr.email-notification.guardianship.body:Добрый день!<br>Поступило новое заявление в органы опеки"
        + " на согласование сделки от <a href=\"https://%s/ugd/ssr/#/app/person/%s\">%s</a>.}")
    private String guardianshipEmailNotificationBody;

    public GuardianshipEmailNotificationService(
        final MailRestApi mailRestApi,
        final PersonDocumentService personDocumentService,
        final CipService cipService,
        final EmailNotificantService emailNotificantService,
        final SystemProperties systemProperties
    ) {
        super(mailRestApi);
        this.personDocumentService = personDocumentService;
        this.cipService = cipService;
        this.emailNotificantService = emailNotificantService;
        this.systemProperties = systemProperties;
    }

    @Override
    public void sendNotificationEmail(final GuardianshipRequestDocument document) {
        if (guardianshipEmailNotificationEnabled) {
            log.info("guardianshipSendNotificationEmail is disabled");
            return;
        }

        log.debug("guardianshipSendNotificationEmail started, documentId = {}", document.getId());

        final GuardianshipRequestData guardianshipRequest = document
            .getDocument()
            .getGuardianshipRequestData();

        final PersonType person = ofNullable(guardianshipRequest.getRequesterPersonId())
            .flatMap(personDocumentService::fetchById)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);

        final String areaCode = PersonUtils
            .getLastAcceptedAgreementOfferLetter(person)
            .map(PersonType.OfferLetters.OfferLetter::getIdCIP)
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipType::getArea)
            .map(CipType.Area::getCode)
            .orElse(null);

        sendNotificationEmail(
            guardianshipEmailNotificationSubject,
            createNotificationBody(guardianshipRequest),
            getToPersonalAddressList(areaCode),
            getCcPersonalAddressList()
        );
    }

    private List<PersonalAddress> getToPersonalAddressList(final String areaCode) {
        return emailNotificantService.getToPersonalAddressList(NotificationType.GUARDIANSHIP, areaCode);
    }

    private List<PersonalAddress> getCcPersonalAddressList() {
        return emailNotificantService.getCcPersonalAddressList(NotificationType.GUARDIANSHIP);
    }

    private String createNotificationBody(final GuardianshipRequestData guardianshipRequest) {
        return String.format(
            guardianshipEmailNotificationBody,
            systemProperties.getDomain(),
            guardianshipRequest.getRequesterPersonId(),
            guardianshipRequest.getFio()
        );
    }
}
