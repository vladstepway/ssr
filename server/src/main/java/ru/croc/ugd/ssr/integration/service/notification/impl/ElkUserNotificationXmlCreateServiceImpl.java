package ru.croc.ugd.ssr.integration.service.notification.impl;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationXmlCreateService;
import ru.croc.ugd.ssr.integration.service.notification.NotificationDescriptor;
import ru.croc.ugd.ssr.integration.service.notification.PushAndSmsTemplates;
import ru.croc.ugd.ssr.integration.stubs.StubsService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.integration.variable.ElkMessages;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfBaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CitizenshipType;
import ru.croc.ugd.ssr.model.integration.etpmv.ContactType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.DictionaryItem;
import ru.croc.ugd.ssr.model.integration.etpmv.GenderType;
import ru.croc.ugd.ssr.model.integration.etpmv.ObjectFactory;
import ru.croc.ugd.ssr.model.integration.etpmv.OutputKindType;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestService;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestStatus;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceProperties;
import ru.croc.ugd.ssr.model.integration.etpmv.StatusType;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;

import java.util.function.Function;

/**
 * Имплиментация сервиса ElkUserNotificationXmlCreateService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElkUserNotificationXmlCreateServiceImpl
    implements ElkUserNotificationXmlCreateService {

    private final ElkMessages elkMessages;
    private final EnoCreator enoCreator;
    private final IntegrationPropertyConfig config;
    private final MessageUtils messageUtils;
    private final RealEstateDocumentService realEstateDocumentService;
    private final XmlUtils xmlUtils;
    private final StubsService stubsService;

    @Override
    public String createStartRenovationXml(PersonDocument personDocument,
                                           String cipAddress,
                                           String cipPhone) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";

        String unom = personData.getUNOM() != null ? personData.getUNOM().toString() : "";
        String address = "";
        if (unom != null && !unom.isEmpty()) {
            RealEstateDocument realEstateDocument =
                realEstateDocumentService.fetchDocumentByUnom(unom);
            address = realEstateDocument.getDocument().getRealEstateData().getAddress();
        }
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvStartRenovationNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus()
            .setReasonText(elkMessages.getNotificationStartRenovation(io,
                address,
                cipAddress,
                cipPhone));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvStartRenovationNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initPushAndSmsData(data,
            PushAndSmsTemplates.START_RENOVATION_SMS,
            PushAndSmsTemplates.START_RENOVATION_PUSH,
            PushAndSmsTemplates.START_RENOVATION_HEADER);
        initSingleTextForCustomAttributes(data,
            elkMessages.getNotificationStartRenovation(io, address, cipAddress, cipPhone));
        data.getService().setServiceNumber(eno);
        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createOfferLetterXml(PersonDocument personDocument,
                                       String cipAddress,
                                       String cipPhone,
                                       String fileUrl) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvOfferLetterNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus()
            .setReasonText(elkMessages.getNotificationOfferLetter(io,
                cipAddress,
                cipPhone,
                null));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvOfferLetterNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initPushAndSmsData(data,
            PushAndSmsTemplates.OFFER_LETTER_SMS,
            PushAndSmsTemplates.OFFER_LETTER_PUSH,
            PushAndSmsTemplates.OFFER_LETTER_HEADER);
        initSingleTextForCustomAttributes(data,
            elkMessages.getNotificationOfferLetter(io, cipAddress, cipPhone, fileUrl));
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createFlatInspectionXml(PersonDocument personDocument,
                                          String cipAddress,
                                          String cipPhone,
                                          String fileUrl) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvFlatInspectionNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus()
            .setReasonText(elkMessages.getNotificationFlatInspection(io,
                cipAddress,
                cipPhone,
                null));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvFlatInspectionNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initSingleTextForCustomAttributes(data,
            elkMessages.getNotificationFlatInspection(io, cipAddress, cipPhone, fileUrl));
        initPushAndSmsData(data,
            PushAndSmsTemplates.FLAT_INSPECTION_SMS,
            PushAndSmsTemplates.FLAT_INSPECTION_PUSH,
            PushAndSmsTemplates.FLAT_INSPECTION_HEADER);
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createFlatAgreementXml(PersonDocument personDocument) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvAgreementNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus().setReasonText(elkMessages.getNotificationFlatAgreement(io));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvAgreementNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initSingleTextForCustomAttributes(data, elkMessages.getNotificationFlatAgreement(io));
        initPushAndSmsData(data,
            PushAndSmsTemplates.FLAT_AGREEMENT_SMS,
            PushAndSmsTemplates.FLAT_AGREEMENT_PUSH,
            PushAndSmsTemplates.FLAT_AGREEMENT_HEADER);
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createFlatRefusalXml(PersonDocument personDocument,
                                       String cipAddress,
                                       String cipPhone) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvRefusalNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus()
            .setReasonText(elkMessages.getNotificationFlatRefusal(io, cipAddress, cipPhone));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvRefusalNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));

        initPushAndSmsData(data,
            PushAndSmsTemplates.FLAT_REFUSAL_SMS,
            PushAndSmsTemplates.FLAT_REFUSAL_PUSH,
            PushAndSmsTemplates.FLAT_REFUSAL_HEADER);
        initSingleTextForCustomAttributes(data,
            elkMessages.getNotificationFlatRefusal(io, cipAddress, cipPhone));
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createSignedContractXml(PersonDocument personDocument,
                                          String cipAddress,
                                          String governmentAddress,
                                          String newFlatAddress,
                                          String oldFlatAddress) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";

        String eno = enoCreator.generateEtpMvNotificationEnoNumber(config.getReleaseFlat());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus()
            .setReasonText(elkMessages.getNotificationSignedContract(
                io,
                cipAddress,
                governmentAddress,
                newFlatAddress,
                oldFlatAddress
            ));
        data.getSignService().getServiceType().setCode(config.getReleaseFlat());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initPushAndSmsData(data,
            PushAndSmsTemplates.SIGNED_CONTRACT_SMS,
            PushAndSmsTemplates.SIGNED_CONTRACT_PUSH,
            PushAndSmsTemplates.SIGNED_CONTRACT_HEADER);
        initSingleTextForCustomAttributes(data,
            elkMessages.getNotificationSignedContract(io,
                cipAddress,
                governmentAddress,
                newFlatAddress,
                oldFlatAddress));
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createContractReadyXml(PersonDocument personDocument, String cipAddress, String cipPhone) {
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEtpMvContractReadyNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification();
        CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus().setReasonText(elkMessages.getNotificationContractReady(io, cipAddress, cipPhone));
        data.getSignService()
            .getServiceType()
            .setCode(config.getEtpMvContractReadyNotificationEnoService());
        RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);
        requestContact.setSsoId(personData.getSsoID());
        requestContact.setGender(getGenderTypePerson(personData));
        initPushAndSmsData(data,
            PushAndSmsTemplates.CONTRACT_READY_SMS,
            PushAndSmsTemplates.CONTRACT_READY_PUSH,
            PushAndSmsTemplates.CONTRACT_READY_HEADER);
        initSingleTextForCustomAttributes(data, elkMessages.getNotificationContractReady(io, cipAddress, cipPhone));
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    @Override
    public String createFixDefectsXml(PersonDocument personDocument, String cipAddress, String cipPhone) {
        RequestStatus status = new RequestStatus();
        PersonType personData = personDocument.getDocument().getPersonData();
        String fio = personData.getFIO() != null ? personData.getFIO() : "";
        String io = !fio.isEmpty() ? fio.substring(fio.indexOf(" ") + 1) : "";

        final String notificationHtml = elkMessages.getNotificationFixDefects(io, cipAddress, cipPhone)
            .orElse(""); //TODO refactor

        status.setReasonText(notificationHtml);
        StatusType statusType = new StatusType();
        statusType.setStatusDate(xmlUtils.getXmlDateNow());
        statusType.setStatusTitle("");
        status.setStatus(statusType);

        RequestServiceForSign requestServiceForSign = new RequestServiceForSign();
        DictionaryItem serviceType = new DictionaryItem();
        serviceType.setCode(config.getEptFixDefectsNotificationEnoService());
        serviceType.setName("Помощь при переезде в рамках Программы реновации");
        requestServiceForSign.setServiceType(serviceType);
        requestServiceForSign.setId(""); //???
        RequestContact contact = new RequestContact();
        contact.setType(ContactType.DECLARANT);
        contact.setGender(getGenderTypePerson(personData));
        contact.setBirthDate(null);
        contact.setCitizenshipType(CitizenshipType.RF);
        contact.setSsoId(personData.getSsoID());

        ArrayOfBaseDeclarant contracts = new ArrayOfBaseDeclarant();
        contracts.getBaseDeclarant().add(contact);
        requestServiceForSign.setContacts(contracts);

        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setSimpleText(notificationHtml);
        serviceProperties.setUnsubscribeLink(
            "https://robot.mos.ru/l/unsubscribe/"
                + "?muid=164b0b6a-d6f4-4d05-8250-ee2932437e85&category=0429a93a-4609-467d-aca5-6b953a6a021d");
        serviceProperties.setEventLink("setEventLink");
        serviceProperties.setMethodInforming(stubsService.createServicePropertiesMethodInforming());
        RequestServiceForSign.CustomAttributes customAttributes =
            new RequestServiceForSign.CustomAttributes();
        customAttributes.setAny(serviceProperties);
        requestServiceForSign.setCustomAttributes(customAttributes);

        RequestService service = new RequestService();
        service.setRegDate(xmlUtils.getXmlDateNow());
        String eno =
            enoCreator.generateEtpMvNotificationEnoNumber(config.getEptFixDefectsNotificationEnoService());
        personData.setIntegrationNumberElk(eno);

        service.setServiceNumber(eno);
        service.setServicePrice(null);
        service.setDepartment(stubsService.createStubDepartment());
        service.setCreatedByDepartment(stubsService.createStubDepartment());
        service.setOutputKind(OutputKindType.PORTAL);

        CoordinateData coordinateData = new CoordinateData();
        coordinateData.setService(service);
        coordinateData.setSignService(requestServiceForSign);
        coordinateData.setStatus(status);

        initPushAndSmsData(coordinateData,
            PushAndSmsTemplates.FIX_DEFECTS_SMS,
            PushAndSmsTemplates.FIX_DEFECTS_PUSH,
            PushAndSmsTemplates.FIX_DEFECTS_HEADER);

        CoordinateMessage coordinateMessage = new CoordinateMessage();
        coordinateMessage.setCoordinateDataMessage(coordinateData);

        return transformEtpmvObjectToXmlString(coordinateMessage);
    }

    @Override
    public String createXmlByDescriptor(
        final PersonDocument personDocument,
        final NotificationDescriptor notificationDescriptor,
        final Function<String, String> paramsResolver
    ) {
        final String emailNotificationHtml = elkMessages.getNotificationHtml(
            notificationDescriptor.getEmailNotificationTemplatePath(), paramsResolver
        ).orElse("");
        final String elkNotificationHtml = elkMessages.getNotificationHtml(
            notificationDescriptor.getElkNotificationTemplatePath(), paramsResolver
        ).orElse(null);

        final CoordinateMessage message = messageUtils.createEmptyCoordinateMessageForNotification(
            notificationDescriptor.getNotificationTitle(),
            notificationDescriptor.getNotificationCode()
        );
        final CoordinateData data = message.getCoordinateDataMessage();
        data.getStatus().setReasonText(emailNotificationHtml);
        data.getSignService()
            .getServiceType()
            .setCode(notificationDescriptor.getEventCode());
        final String eno = enoCreator.generateEtpMvNotificationEnoNumber(notificationDescriptor.getEventCode());
        of(personDocument.getDocument())
            .map(Person::getPersonData)
            .ifPresent(person -> {
                this.populateWithPersonDetails(data, person);
                person.setIntegrationNumberElk(eno);
            });
        initPushAndSmsData(data,
            notificationDescriptor.getSmsNotification(),
            notificationDescriptor.getPushNotificationBody(),
            notificationDescriptor.getPushNotificationHeader());
        initSingleTextForCustomAttributes(data, elkNotificationHtml);
        log.debug("Сгенерированно ЕНО для отправки в ЛК. Код ГУ: {}. ЕНО: {}",
            notificationDescriptor.getEventCode(), eno);
        data.getService().setServiceNumber(eno);

        return transformEtpmvObjectToXmlString(message);
    }

    private void populateWithPersonDetails(final CoordinateData data, final PersonType person) {
        final RequestContact requestContact =
            (RequestContact) data.getSignService().getContacts().getBaseDeclarant().get(0);

        requestContact.setSsoId(person.getSsoID());
        requestContact.setGender(getGenderTypePerson(person));
    }

    private void initSingleTextForCustomAttributes(final CoordinateData data,
                                                   final String simpleText) {
        final Object props = data.getSignService().getCustomAttributes().getAny();
        if (props instanceof ServiceProperties) {
            ((ServiceProperties) props).setSimpleText(simpleText);
        }
    }

    private void initPushAndSmsData(final CoordinateData data,
                                    final String sms,
                                    final String pushNotification,
                                    final String pushNotificationHeader) {
        final Object props = data.getSignService().getCustomAttributes().getAny();
        if (props instanceof ServiceProperties) {
            ((ServiceProperties) props).setPushTitle(StringUtils.substring("Переезд по Программе реновации", 0, 49));
            ((ServiceProperties) props).setPushData(pushNotification);
            ((ServiceProperties) props).setSmsData(sms);
        }
    }

    private String transformEtpmvObjectToXmlString(CoordinateMessage coordinateMessage) {
        return xmlUtils.transformObjectToXmlString(coordinateMessage,
            CoordinateMessage.class,
            ServiceProperties.class,
            ObjectFactory.class);
    }

    private GenderType getGenderTypePerson(final PersonType person) {
        return ofNullable(person)
            .map(PersonType::getGender)
            .filter(gender -> gender.equalsIgnoreCase("мужской"))
            .map(gender -> GenderType.MALE)
            .orElse(GenderType.FEMALE);
    }
}
