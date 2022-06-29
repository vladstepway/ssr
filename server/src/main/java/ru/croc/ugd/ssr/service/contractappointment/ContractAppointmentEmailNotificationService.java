package ru.croc.ugd.ssr.service.contractappointment;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.AbstractEmailNotificationService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.emailnotificant.EmailNotificantService;
import ru.croc.ugd.ssr.service.emailnotificant.NotificationType;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;

import java.util.List;

@Slf4j
@Service
public class ContractAppointmentEmailNotificationService
    extends AbstractEmailNotificationService<ContractAppointmentDocument> {

    private final CipService cipService;
    private final EmailNotificantService emailNotificantService;
    private final SystemProperties systemProperties;

    @Value("${ugd.ssr.email-notification.contract-appointment.enabled:false}")
    private boolean contractAppointmentEmailNotificationEnabled;
    @Value("${ugd.ssr.email-notification.contract-appointment.subject:"
        + "Новое заявление на запись на заключение договора}")
    private String contractAppointmentEmailNotificationSubject;
    @Value("${ugd.ssr.email-notification.contract-appointment.body:Добрый день!<br>Поступило новое заявление <a href="
        + "\"https://%s/ugd/ssr/#/app/contract-appointment/%s\">%s</a> на запись на заключение договора от жителя.}")
    private String contractAppointmentEmailNotificationBody;

    public ContractAppointmentEmailNotificationService(
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
    public void sendNotificationEmail(
        final ContractAppointmentDocument document
    ) {
        if (contractAppointmentEmailNotificationEnabled) {
            log.info("contractAppointmentSendNotificationEmail is disabled");
            return;
        }

        log.debug("contractAppointmentSendNotificationEmail started, documentId = {}", document.getId());

        final ContractAppointmentData contractAppointment = document
            .getDocument()
            .getContractAppointmentData();

        final String areaCode = ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .map(CipType::getArea)
            .map(CipType.Area::getCode)
            .orElse(null);

        sendNotificationEmail(
            contractAppointmentEmailNotificationSubject,
            createNotificationBody(document.getId(), contractAppointment),
            getToPersonalAddressList(areaCode),
            getCcPersonalAddressList()
        );
    }

    private List<PersonalAddress> getToPersonalAddressList(final String areaCode) {
        return emailNotificantService.getToPersonalAddressList(NotificationType.CONTRACT_APPOINTMENT, areaCode);
    }

    private List<PersonalAddress> getCcPersonalAddressList() {
        return emailNotificantService.getCcPersonalAddressList(NotificationType.CONTRACT_APPOINTMENT);
    }

    private String createNotificationBody(final String documentId, final ContractAppointmentData contractAppointment) {
        return String.format(
            contractAppointmentEmailNotificationBody,
            systemProperties.getDomain(),
            documentId,
            contractAppointment.getEno()
        );
    }
}
