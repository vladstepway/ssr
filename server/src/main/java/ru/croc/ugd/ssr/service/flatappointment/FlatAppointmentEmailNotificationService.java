package ru.croc.ugd.ssr.service.flatappointment;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.AbstractEmailNotificationService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.emailnotificant.EmailNotificantService;
import ru.croc.ugd.ssr.service.emailnotificant.NotificationType;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;

import java.util.List;

@Slf4j
@Service
public class FlatAppointmentEmailNotificationService extends AbstractEmailNotificationService<FlatAppointmentDocument> {

    private final CipService cipService;
    private final EmailNotificantService emailNotificantService;
    private final SystemProperties systemProperties;

    @Value("${ugd.ssr.email-notification.flat-appointment.enabled:false}")
    private boolean flatAppointmentEmailNotificationEnabled;
    @Value("${ugd.ssr.email-notification.flat-appointment.subject:"
        + "Новое заявление на осмотр квартиры}")
    private String flatAppointmentEmailNotificationSubject;
    @Value("${ugd.ssr.email-notification.flat-appointment.body:Добрый день!<br>Поступило новое заявление "
        + "<a href=\"https://%s/ugd/ssr/#/app/flat-appointment/%s\">%s</a> на осмотр предложенной равнозначной квартиры от жителя.}")
    private String flatAppointmentEmailNotificationBody;

    public FlatAppointmentEmailNotificationService(
        final MailRestApi mailRestApi,
        final CipService cipService,
        final EmailNotificantService emailNotificantService,
        final SystemProperties systemProperties
    ) {
        super(mailRestApi);
        this.cipService = cipService;
        this.emailNotificantService = emailNotificantService;
        this.systemProperties = systemProperties;
    }

    @Override
    public void sendNotificationEmail(final FlatAppointmentDocument document) {
        if (flatAppointmentEmailNotificationEnabled) {
            log.info("flatAppointmentSendNotificationEmail is disabled");
            return;
        }

        log.debug("flatAppointmentSendNotificationEmail started, documentId = {}", document.getId());

        final FlatAppointmentData flatAppointment = document.getDocument().getFlatAppointmentData();

        final String areaCode = ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipType::getArea)
            .map(CipType.Area::getCode)
            .orElse(null);

        sendNotificationEmail(
            flatAppointmentEmailNotificationSubject,
            createNotificationBody(document.getId(), flatAppointment),
            getToPersonalAddressList(areaCode),
            getCcPersonalAddressList()
        );
    }

    private List<PersonalAddress> getToPersonalAddressList(final String areaCode) {
        return emailNotificantService.getToPersonalAddressList(NotificationType.FLAT_APPOINTMENT, areaCode);
    }

    private List<PersonalAddress> getCcPersonalAddressList() {
        return emailNotificantService.getCcPersonalAddressList(NotificationType.FLAT_APPOINTMENT);
    }

    private String createNotificationBody(final String documentId, final FlatAppointmentData flatAppointment) {
        return String.format(
            flatAppointmentEmailNotificationBody,
            systemProperties.getDomain(),
            documentId,
            flatAppointment.getEno()
        );
    }
}
