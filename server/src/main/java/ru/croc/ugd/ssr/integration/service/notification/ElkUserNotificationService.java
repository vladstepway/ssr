package ru.croc.ugd.ssr.integration.service.notification;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.MessageType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.croc.ugd.ssr.builder.ResettlementHistoryBuilder;
import ru.croc.ugd.ssr.config.agreementapplication.AgreementApplicationProperties;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.service.EtpService;
import ru.croc.ugd.ssr.integration.service.MdmIntegrationService;
import ru.croc.ugd.ssr.integration.service.flows.DgiElkDeliveryStatusSendService;
import ru.croc.ugd.ssr.integration.util.NullableUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.DictionaryItem;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionNotificationData;
import ru.croc.ugd.ssr.shipping.Apartment;
import ru.croc.ugd.ssr.shipping.ShippingApplicant;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.exception.RINotFoundException;
import ru.reinform.cdp.utils.core.RIXmlUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис по отправке нотификаций.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElkUserNotificationService {

    private static final String CHED_DEFAULT_LINK = "https://my.mos.ru/my/#/settings/info";

    private static final String OFFER_LETTER_NOTIFICATION = "Уведомление граждан о письме с предложением";

    private static final String START_RENOVATION_NOTIFICATION = "Уведомление граждан о начале переселения";

    private static final String FLAT_AGREEMENT_NOTIFICATION = "Уведомление граждан о поданном согласии";

    private static final String FLAT_REFUSAL_NOTIFICATION = "Уведомление граждан о поданном отказе";

    private static final String FLAT_INSPECTION_NOTIFICATION =
        "Уведомление о необходимости подписать заявление на согласие/отказ после просмотра квартиры";

    private static final String FIX_DEFECTS_NOTIFICATION = "Уведомление граждан об устранении дефектов";

    private static final String QUEUE_NAME = "UGD.NOTIFICATION_OUT";

    private static final String CONTRACT_READY_NOTIFICATION =
        "Уведомление о готовности договора для подписания с приглашением на подписание договора";

    private static final String SIGNED_CONTRACT_NOTIFICATION = "Уведомление о подписанном договоре";

    private static final String SEND_MESSAGE_TEXT = "Уведомление направлено";

    private final CipService cipService;

    private final IntegrationPropertyConfig config;

    private final EtpService etpService;

    private final ElkUserNotificationXmlCreateService elkUserNotificationXmlCreateService;

    private final DgiElkDeliveryStatusSendService dgiElkDeliveryStatusSendService;

    private final PersonDocumentService personDocumentService;

    private final ApartmentInspectionService apartmentInspectionService;

    private final MdmIntegrationService mdmIntegrationService;

    private final XmlUtils xmlUtils;

    private final BeanFactory beanFactory;

    private final DictionaryService dictionaryService;

    private final NullableUtils nullableUtils;

    private final ChedFileService chedFileService;

    private final FlatService flatService;

    private final AgreementApplicationProperties agreementApplicationProperties;

    /**
     * Отправка сообщения в ЕЛК (начало переселения) жителю по id.
     *
     * @param personId id жителя
     * @param cipId    id цип
     * @return статус
     */
    public ResponseEntity<String> sendRequestToElkStartRenovationByPersonId(String personId, String cipId) {
        try {
            PersonDocument personDocument = personDocumentService.fetchDocument(personId);
            sendNotificationStartRenovation(personDocument, true, cipId);

            return ResponseEntity.ok().build();
        } catch (RINotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Отправка запроса в ЕЛК о начале расселения.
     *
     * @param personDocument житель
     * @param savePerson     сохранять ли жителя
     * @param cipId          id ЦИПа
     */
    public void sendNotificationStartRenovation(PersonDocument personDocument, Boolean savePerson, String cipId) {
        if (StringUtils.isBlank(personDocument.getDocument().getPersonData().getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        if (StringUtils.isBlank(personDocument.getDocument().getPersonData().getSsoID())) {
            log.warn("Уведомление жителю с id {} не отправлено из-за отсутствия ssoId", personDocument.getId());
            return;
        }

        CipType cipData;
        try {
            CipDocument cipDocument = cipService.fetchDocument(cipId);
            cipData = cipDocument.getDocument().getCipData();
        } catch (RINotFoundException exception) {
            log.error("Не найден ЦИП с id: {}", cipId);
            return;
        }

        String xmlString = elkUserNotificationXmlCreateService
            .createStartRenovationXml(personDocument, cipData.getAddress(), cipData.getPhone());

        etpService.exportXml(xmlString, config.getElkNotificationQueueName());

        if (savePerson) {
            PersonType personData = personDocument.getDocument().getPersonData();
            createSendedMessageForPerson(
                personDocument,
                START_RENOVATION_NOTIFICATION,
                xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                "",
                null
            );

            final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
            historyBuilder
                .addEventId("1")
                .addAnnotation("Уведомление о начале переселения");
            personData.getResettlementHistory().add(historyBuilder.build());

            personData.setRelocationStatus("1");

            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }

    }

    /**
     * Отправка запроса в ЕЛК о письме с предложением.
     *
     * @param personDocument житель
     * @param savePerson     сохранять ли жителя
     * @param cipId          id ЦИПа
     * @param letterId       id письма
     * @param fileId         url file
     */
    public void sendNotificationOfferLetter(
        final PersonDocument personDocument,
        final Boolean savePerson,
        final String cipId,
        final String letterId,
        final String fileId
    ) {
        if (agreementApplicationProperties.isUpdatedOfferLetterNotificationEnabled()) {
            final PersonType.OfferLetters.OfferLetter offerLetter = PersonUtils.getOfferLetter(personDocument, letterId)
                .orElse(null);
            final String deadlineDate = ofNullable(offerLetter)
                .map(PersonType.OfferLetters.OfferLetter::getDate)
                .map(date -> date.plusDays(agreementApplicationProperties.getDaysToReceiveOffer()))
                .map(DateTimeUtils::getFormattedDate)
                .orElse(null);
            final String offerLetterLink = ofNullable(offerLetter)
                .flatMap(PersonUtils::getOfferLetterChedId)
                .map(this::getChedFileLink)
                .orElse(null);

            final Map<String, String> notificationParams = new HashMap<>();
            notificationParams.put("$deadlineDate", deadlineDate);
            notificationParams.put("$offerLetterLink", offerLetterLink);

            sendOfferLetterNotification(personDocument, notificationParams);
        } else {
            sendOfferLetterNotification(personDocument, savePerson, cipId, letterId, fileId);
        }
    }

    private void sendOfferLetterNotification(
        final PersonDocument personDocument, final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(personDocument, GeneralNotificationsDescriptor.OFFER_LETTER, extraTemplateParams);
    }

    private void sendOfferLetterNotification(PersonDocument personDocument,
                                             Boolean savePerson,
                                             String cipId,
                                             String letterId,
                                             String fileId) {
        if (StringUtils.isBlank(personDocument.getDocument().getPersonData().getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        PersonType personData = personDocument.getDocument().getPersonData();

        if (nonNull(personData.getSsoID())) {
            CipType cipData;
            try {
                CipDocument cipDocument = cipService.fetchDocument(cipId);
                cipData = cipDocument.getDocument().getCipData();
            } catch (RINotFoundException exception) {
                log.error("Не найден ЦИП с id: {}", cipId);
                return;
            }

            String xmlString = elkUserNotificationXmlCreateService.createOfferLetterXml(personDocument,
                cipData.getAddress(),
                cipData.getPhone(),
                config.getChedDownloadUrl() + fileId);

            etpService.exportXml(xmlString, config.getElkNotificationQueueName());

            if (savePerson) {
                createSendedMessageForPerson(
                    personDocument,
                    OFFER_LETTER_NOTIFICATION,
                    xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                    letterId,
                    null
                );

                final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
                historyBuilder
                    .addEventId("3")
                    .addDataId(letterId)
                    .addAnnotation(
                        "Передано уведомление с предложением равнозначной квартиры в ЛК letterId: " + letterId
                    );
                personData.getResettlementHistory().add(historyBuilder.build());

                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
            }
        } else {
            if (personData.getSNILS() == null) {
                dgiElkDeliveryStatusSendService.sendElkOfferLetterStatusToDgi(personDocument, letterId, "4");
            } else if (personData.getSsoID() == null) {
                dgiElkDeliveryStatusSendService.sendElkOfferLetterStatusToDgi(personDocument, letterId, "3");
            }
        }
    }

    /**
     * Отправка запроса в ЕЛК после осмотра квартиры.
     *
     * @param personDocument житель
     * @param savePerson     сохранять ли жителя
     * @param cipId          id ЦИПа
     * @param letterId       id письма
     * @param fileId         url file
     */
    public void sendNotificationFlatInspection(
        PersonDocument personDocument, Boolean savePerson, String cipId, String letterId, String fileId
    ) {
        PersonType personData = personDocument.getDocument().getPersonData();

        // проверим необходимость отправки уведолмения
        if (StringUtils.isNotBlank(letterId) && nonNull(personData.getSendedMessages())) {
            Optional<MessageType> typeOptional = personData.getSendedMessages().getMessage()
                .stream()
                .filter(messageType -> nonNull(messageType.getLetterId()))
                .filter(messageType -> messageType.getLetterId().equals(letterId))
                .filter(messageType -> messageType.getMessageText().equals(SEND_MESSAGE_TEXT))
                .filter(
                    messageType -> messageType.getBusinessType().equals(FLAT_AGREEMENT_NOTIFICATION)
                        || messageType.getBusinessType().equals(FLAT_REFUSAL_NOTIFICATION)
                        || messageType.getBusinessType().equals(CONTRACT_READY_NOTIFICATION)
                        || messageType.getBusinessType().equals(SIGNED_CONTRACT_NOTIFICATION)
                )
                .findFirst();
            if (typeOptional.isPresent()) {
                log.warn("Уведомление не отправлено, так как была нарушена последовательность отправки.");
                return;
            }
        }

        if (StringUtils.isBlank(personData.getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        if (StringUtils.isBlank(personData.getSsoID())) {
            log.warn("Уведомление жителю с id {} не отправлено из-за отсутствия ssoId", personDocument.getId());
            return;
        }

        String cipAddress = "";
        String cipPhone = "";
        try {
            CipDocument cipDocument = cipService.fetchDocument(cipId);
            CipType cipData = cipDocument.getDocument().getCipData();
            cipAddress = cipData.getAddress();
            cipPhone = cipData.getPhone();
        } catch (RINotFoundException exception) {
            log.error("Не найден ЦИП с id: {}", cipId);
            log.warn("Уведомление будет отправлено без информации о ЦИП");
        }

        String xmlString = elkUserNotificationXmlCreateService.createFlatInspectionXml(personDocument,
            cipAddress,
            cipPhone,
            config.getChedDownloadUrl() + fileId);

        etpService.exportXml(xmlString, config.getElkNotificationQueueName());

        if (savePerson) {
            createSendedMessageForPerson(
                personDocument,
                FLAT_INSPECTION_NOTIFICATION,
                xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                letterId,
                null
            );

            List<ResettlementHistory> historyList = personData.getResettlementHistory()
                .stream()
                .filter(rh -> rh.getEventId().equals("4"))
                .collect(Collectors.toList());
            if (historyList.size() > 0) {
                ResettlementHistory resettlementHistory = historyList.get(historyList.size() - 1);

                final ResettlementHistoryBuilder historyBuilder = beanFactory
                    .getBean(ResettlementHistoryBuilder.class);
                historyBuilder
                    .addEventId("5")
                    .addDataId(resettlementHistory.getDataId())
                    .addAnnotation(resettlementHistory.getAnnotation());
                personData.getResettlementHistory().add(historyBuilder.build());

            }
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
    }

    /**
     * Отправка запроса в ЕЛК о согласии на квартиру.
     *
     * @param ids      Ids жителей
     * @param letterId ид письма с предложением
     */
    @Async
    public void sendNotificationFlatAgreementByPersonIds(List<String> ids, String letterId) {
        List<PersonDocument> personDocuments = personDocumentService.fetchDocuments(ids);
        if (personDocuments.isEmpty()) {
            log.info("Не найдено ни одного жителя, уведомления не отправлены");
            return;
        }

        personDocuments.forEach(p -> sendNotificationFlatAgreement(p, letterId, true));
    }

    /**
     * Отправка запроса в ЕЛК о согласии на квартиру.
     *
     * @param personDocument житель
     * @param letterId       ид письма с предложением
     * @param savePerson     сохранять ли жителя
     */
    public void sendNotificationFlatAgreement(PersonDocument personDocument, String letterId, Boolean savePerson) {
        PersonType personData = personDocument.getDocument().getPersonData();

        // проверим необходимость отправки уведолмения
        if (StringUtils.isNotBlank(letterId) && nonNull(personData.getSendedMessages())) {
            Optional<MessageType> typeOptional = personData.getSendedMessages().getMessage()
                .stream()
                .filter(messageType -> nonNull(messageType.getLetterId()))
                .filter(messageType -> messageType.getLetterId().equals(letterId))
                .filter(messageType -> messageType.getMessageText().equals(SEND_MESSAGE_TEXT))
                .filter(
                    messageType -> messageType.getBusinessType().equals(CONTRACT_READY_NOTIFICATION)
                        || messageType.getBusinessType().equals(SIGNED_CONTRACT_NOTIFICATION)
                )
                .findFirst();
            if (typeOptional.isPresent()) {
                log.warn("Уведомление не отправлено, так как была нарушена последовательность отправки.");
                return;
            }
        }

        if (StringUtils.isBlank(personData.getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        if (StringUtils.isBlank(personData.getSsoID())) {
            log.warn("Уведомление жителю с id {} не отправлено из-за отсутствия ssoId", personDocument.getId());
            return;
        }

        String xmlString = elkUserNotificationXmlCreateService.createFlatAgreementXml(personDocument);

        etpService.exportXml(xmlString, config.getElkNotificationQueueName());

        if (savePerson) {
            createSendedMessageForPerson(
                personDocument,
                FLAT_AGREEMENT_NOTIFICATION,
                xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                "",
                letterId
            );

            PersonType.Agreements.Agreement agreement = personData.getAgreements().getAgreement()
                .get(personData.getAgreements().getAgreement().size() - 1);

            final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
            historyBuilder
                .addEventId("7")
                .addDataId(agreement.getDataId())
                .addAnnotation("Получено согласие");
            personData.getResettlementHistory().add(historyBuilder.build());

            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
    }

    /**
     * Отправка запроса в ЕЛК об отказе на квартиру.
     *
     * @param ids      Ids жителей
     * @param cipId    Id ЦИП
     * @param letterId ид письма с предложением
     */
    @Async
    public void sendNotificationFlatRefusalByPersonIds(List<String> ids, String cipId, String letterId) {
        List<PersonDocument> personDocuments = personDocumentService.fetchDocuments(ids);
        if (personDocuments.isEmpty()) {
            log.info("Не найдено ни одного жителя, уведомления не отправлены");
            return;
        }

        personDocuments.forEach(p -> sendNotificationFlatRefusal(p, true, cipId, letterId));
    }

    /**
     * Отправка запроса в ЕЛК после осмотра квартиры.
     *
     * @param cipId    Id ЦИП
     * @param ids      Ids жителей
     * @param letterId Id письма
     */
    @Async
    public void sendNotificationFlatInspectionByPersonIds(List<String> ids, String cipId, String letterId) {
        List<PersonType.OfferLetters.OfferLetter> offerLetters =
            personDocumentService.fetchPersonOfferLettersByLetterId(letterId);

        String fileId = "";
        if (isNull(offerLetters) || offerLetters.isEmpty()) {
            log.warn("Не найдено писем по letterId, уведомления будут направлены без данных о нем");
        } else {
            PersonType.OfferLetters.OfferLetter.Files.File totalFile = offerLetters.stream()
                .filter(ol -> nonNull(ol.getFiles())
                    && nonNull(ol.getFiles().getFile())
                    && !ol.getFiles().getFile().isEmpty())
                .filter(ol -> ol.getFiles()
                    .getFile()
                    .stream()
                    .anyMatch(file -> file.getFileType().equals("1") && nonNull(file.getChedFileId())))
                .map(ol -> ol.getFiles()
                    .getFile()
                    .stream()
                    .filter(file -> file.getFileType().equals("1") && nonNull(file.getChedFileId()))
                    .findFirst()
                    .get()

                )
                .findFirst()
                .orElse(null);

            if (isNull(totalFile)) {
                log.debug("Не найдено файлов по letterId");
                return;
            } else {
                fileId = totalFile.getChedFileId();
            }
        }

        List<PersonDocument> personDocuments = personDocumentService.fetchDocuments(ids);
        if (personDocuments.isEmpty()) {
            log.debug("Не найдено ни одного жителя, уведомления не отправлены");
            return;
        }


        String finalFileId = fileId;
        personDocuments.forEach(p -> sendNotificationFlatInspection(p, true, cipId, letterId, finalFileId));

    }

    /**
     * Отправка запроса в ЕЛК об отказе на квартиру.
     *
     * @param personDocument житель
     * @param savePerson     сохранять ли жителя
     * @param cipId          id ЦИПа
     * @param letterId       ид письма с предложением
     */
    public void sendNotificationFlatRefusal(
        PersonDocument personDocument, Boolean savePerson, String cipId, String letterId
    ) {
        PersonType personData = personDocument.getDocument().getPersonData();

        // проверим необходимость отправки уведолмения
        if (StringUtils.isNotBlank(letterId) && nonNull(personData.getSendedMessages())) {
            Optional<MessageType> typeOptional = personData.getSendedMessages().getMessage()
                .stream()
                .filter(messageType -> nonNull(messageType.getLetterId()))
                .filter(messageType -> messageType.getLetterId().equals(letterId))
                .filter(messageType -> messageType.getMessageText().equals(SEND_MESSAGE_TEXT))
                .filter(
                    messageType -> messageType.getBusinessType().equals(CONTRACT_READY_NOTIFICATION)
                        || messageType.getBusinessType().equals(SIGNED_CONTRACT_NOTIFICATION)
                )
                .findFirst();
            if (typeOptional.isPresent()) {
                log.warn("Уведомление не отправлено, так как была нарушена последовательность отправки.");
                return;
            }
        }

        if (StringUtils.isBlank(personData.getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        if (StringUtils.isBlank(personData.getSsoID())) {
            log.warn("Уведомление жителю с id {} не отправлено из-за отсутствия ssoId", personDocument.getId());
            return;
        }

        String cipAddress = "";
        String cipPhone = "";
        try {
            CipDocument cipDocument = cipService.fetchDocument(cipId);
            CipType cipData = cipDocument.getDocument().getCipData();
            cipAddress = cipData.getAddress();
            cipPhone = cipData.getPhone();
        } catch (RINotFoundException exception) {
            log.error("Не найден ЦИП с id: {}", cipId);
            log.warn("Уведомление будет отправлено без информации о ЦИП");
        }

        String xmlString = elkUserNotificationXmlCreateService
            .createFlatRefusalXml(personDocument, cipAddress, cipPhone);

        etpService.exportXml(xmlString, config.getElkNotificationQueueName());

        if (savePerson) {
            createSendedMessageForPerson(
                personDocument,
                FLAT_REFUSAL_NOTIFICATION,
                xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                "",
                letterId
            );

            PersonType.Agreements.Agreement agreement = personData.getAgreements().getAgreement()
                .get(personData.getAgreements().getAgreement().size() - 1);

            final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
            historyBuilder
                .addEventId("9")
                .addDataId(agreement.getDataId())
                .addAnnotation("Получен отказ. Причина: " + dictionaryService.getNameByCode(
                    "ugd_ssr_refuseReason", agreement.getRefuseReason()
                ));
            personData.getResettlementHistory().add(historyBuilder.build());

            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
    }

    /**
     * Отправка запроса в ЕЛК о подписанном договоре.
     *
     * @param personDocument    житель
     * @param savePerson        сохранять ли жителя
     * @param cipId             id ЦИПа
     * @param governmentAddress Адрес Управы района
     * @param newFlatAddress    Адрес новой квартиры
     * @param oldFlatAddress    Адрес старой квартиры
     * @param contractOrderId   orderId
     * @param letterId          ид письма с предложением
     */
    public void sendNotificationSignedContract(
        PersonDocument personDocument,
        Boolean savePerson,
        String cipId,
        String governmentAddress,
        String newFlatAddress,
        String oldFlatAddress,
        String contractOrderId,
        String letterId
    ) {
        if (StringUtils.isBlank(personDocument.getDocument().getPersonData().getSsoID())
        ) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
        PersonType personData = personDocument.getDocument().getPersonData();

        if (nonNull(personData.getSsoID())) {
            CipType cipData;
            try {
                CipDocument cipDocument = cipService.fetchDocument(cipId);
                cipData = cipDocument.getDocument().getCipData();
            } catch (RINotFoundException exception) {
                log.error("Не найден ЦИП с id: {}", cipId);
                return;
            }

            String xmlString = elkUserNotificationXmlCreateService.createSignedContractXml(
                personDocument, cipData.getAddress(), governmentAddress, newFlatAddress, oldFlatAddress
            );

            etpService.exportXml(xmlString, config.getElkNotificationQueueName());

            if (savePerson) {
                createSendedMessageForPerson(
                    personDocument,
                    SIGNED_CONTRACT_NOTIFICATION,
                    xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                    contractOrderId,
                    letterId
                );

                PersonType.Contracts.Contract contract = personData.getContracts().getContract()
                    .get(personData.getContracts().getContract().size() - 1);

                final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
                historyBuilder
                    .addEventId("15")
                    .addDataId(contract.getDataId())
                    .addAnnotation("Передано уведомление о подписанном договоре в ЛК");
                personData.getResettlementHistory().add(historyBuilder.build());

                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
            }
        } else {
            if (personData.getSNILS() == null) {
                dgiElkDeliveryStatusSendService.sendElkSignedContractStatusToDgi(
                    personDocument, contractOrderId, "4"
                );
            } else if (personData.getSsoID() == null) {
                dgiElkDeliveryStatusSendService.sendElkSignedContractStatusToDgi(
                    personDocument, contractOrderId, "3"
                );
            }
        }
    }

    /**
     * Отправка запроса в ЕЛК о готовности договора.
     *
     * @param personDocument  житель
     * @param savePerson      сохранять ли жителя
     * @param contractOrderId orderId
     * @param letterId        ид письма с предложением
     */
    public void sendNotificationContractReady(
        PersonDocument personDocument, Boolean savePerson, String contractOrderId, String letterId
    ) {
        PersonType personData = personDocument.getDocument().getPersonData();

        // проверим необходимость отправки уведолмения
        if (
            StringUtils.isNotBlank(letterId)
                && StringUtils.isNotBlank(contractOrderId)
                && nonNull(personData.getSendedMessages())
        ) {
            Optional<MessageType> typeOptional = personData.getSendedMessages().getMessage()
                .stream()
                .filter(messageType -> nonNull(messageType.getLetterId()))
                .filter(messageType -> nonNull(messageType.getCustomAttribute()))
                .filter(messageType -> messageType.getLetterId().equals(letterId))
                .filter(messageType -> messageType.getCustomAttribute().equals(contractOrderId))
                .filter(messageType -> messageType.getMessageText().equals(SEND_MESSAGE_TEXT))
                .filter(
                    messageType -> messageType.getBusinessType().equals(SIGNED_CONTRACT_NOTIFICATION)
                )
                .findFirst();
            if (typeOptional.isPresent()) {
                log.warn("Уведомление не отправлено, так как была нарушена последовательность отправки.");
                return;
            }
        }

        if (StringUtils.isBlank(personData.getSsoID())) {
            personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }

        if (nonNull(personData.getSsoID())) {
            CipType cipData;
            try {
                List<PersonType.OfferLetters.OfferLetter> collect = ofNullable(personData.getOfferLetters())
                    .map(PersonType.OfferLetters::getOfferLetter)
                    .map(value -> value.stream()
                        .filter(letter -> nonNull(letter.getIdCIP()))
                        .collect(Collectors.toList()))
                    .orElseThrow(() -> new RINotFoundException("Offer letters doesnt found"));
                CipDocument cipDocument = cipService.fetchDocument(collect.get(0).getIdCIP());
                cipData = cipDocument.getDocument().getCipData();
            } catch (RINotFoundException exception) {
                log.error("Не найден ЦИП у жителя с id: {}", personDocument.getId());
                return;
            }

            String xmlString = elkUserNotificationXmlCreateService.createContractReadyXml(
                personDocument, cipData.getAddress(), cipData.getPhone()
            );

            etpService.exportXml(xmlString, config.getElkNotificationQueueName());

            if (savePerson) {
                createSendedMessageForPerson(
                    personDocument,
                    CONTRACT_READY_NOTIFICATION,
                    xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                    contractOrderId,
                    letterId
                );

                PersonType.Contracts.Contract contract = personData.getContracts().getContract()
                    .get(personData.getContracts().getContract().size() - 1);

                final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
                historyBuilder
                    .addEventId("11")
                    .addDataId(contract.getDataId())
                    .addAnnotation(
                        "Передано уведомление о готовности договора с приглашением на подписание договора в ЛК"
                    );
                personData.getResettlementHistory().add(historyBuilder.build());

                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
            }
        } else {
            if (personData.getSNILS() == null) {
                dgiElkDeliveryStatusSendService.sendElkContractReadyStatusToDgi(
                    personDocument, contractOrderId, "4"
                );
            } else if (personData.getSsoID() == null) {
                dgiElkDeliveryStatusSendService.sendElkContractReadyStatusToDgi(
                    personDocument, contractOrderId, "3"
                );
            }
        }
    }

    /**
     * Формирование xml запроса в ЕЛК о начале расселения по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createStartRenovationXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService
                .createStartRenovationXml(personDocument, "Адрес ЦИП", "Телефон ЦИП");
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК о письме с предложением по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createOfferLetterXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService
                .createOfferLetterXml(personDocument, "Адрес ЦИП", "Телефон ЦИП", "url");
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК после осмотра квартиры по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createFlatInspectionXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService
                .createFlatInspectionXml(personDocument, "Адрес ЦИП", "Телефон ЦИП", "url");
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК о согласии по квартире по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createFlatAgreementXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService.createFlatAgreementXml(personDocument);
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК об отказе на квартиру по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createFlatRefusalXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService.createFlatRefusalXml(personDocument, "Адрес ЦИП", "Телефон ЦИП");
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК о подписанном договоре по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createSignedContractXmlElkRequest(String id) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(id);
        final PersonType person = personDocument.getDocument().getPersonData();
        if (nonNull(person)
            && nonNull(person.getSsoID())
            && !person.getSsoID().isEmpty()
        ) {
            final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = flatService
                .fetchRealEstateAndFlat(person.getFlatID());
            final String oldFlatAddress = RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);

            return elkUserNotificationXmlCreateService.createSignedContractXml(
                personDocument, "Адрес ЦИП", "Телефон ЦИП", "Адрес новой квартиры", oldFlatAddress
            );
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Формирование xml запроса в ЕЛК о готовности проекта договора по id жителя.
     *
     * @param id id жителя
     * @return xml
     */
    public String createContractReadyXmlElkRequest(String id) {
        PersonDocument personDocument = personDocumentService.fetchDocument(id);
        if (nonNull(personDocument.getDocument().getPersonData())
            && nonNull(personDocument.getDocument().getPersonData().getSsoID())
            && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
            return elkUserNotificationXmlCreateService
                .createContractReadyXml(personDocument, "Адрес ЦИП", "Телефон ЦИП");
        } else {
            return "Житель не найден или отсутствуют обязательные параметры";
        }
    }

    /**
     * Метод обработки входящего сообщения ЕТП МВ от ЕЛК.
     *
     * @param message входящее сообщение
     */
    public ElkUserNotificationDto parseReceiveElkMessage(String message) {
        final CoordinateStatusMessage statusMessage = RIXmlUtils.unmarshal(message, CoordinateStatusMessage.class);
        final CoordinateStatusData statusDataMessage = statusMessage.getCoordinateStatusDataMessage();

        final String eno = statusDataMessage.getServiceNumber();
        final String event = statusDataMessage.getStatus().getStatusTitle();

        if (personDocumentService.isNotificationAlreadyHandled(eno, event)) {
            log.info("Жителю пришел дубликат статуса из ЛК: eno: {}, event: {}", eno, event);
            return null;
        }
        // сохраним входящее сообщение
        etpService.saveImportXmlFile(message);

        final PersonDocument personDocument = personDocumentService.getPersonBySendMessageEno(eno);

        if (isNull(personDocument)) {
            log.warn("Отсутствует житель с ЕНО: {}", eno);
            return null;
        }
        PersonType personData = personDocument.getDocument().getPersonData();

        Optional<MessageType> first = personData.getSendedMessages()
            .getMessage()
            .stream()
            .filter(mes -> mes.getEno().equals(eno))
            .findFirst();

        if (!first.isPresent()) {
            log.warn("Не найдено отправленное сообщение у жителя с ЕНО: {}", eno);
            return null;
        }

        MessageType sendMessages = first.get();

        MessageType messageType = new MessageType();
        messageType.setMessageID(UUID.randomUUID().toString());
        messageType.setEno(eno);
        messageType.setMessageText(event);
        messageType.setEventDateTime(LocalDateTime.now());
        messageType.setApplicant(getApplicantByStatus(statusDataMessage.getStatus().getStatusCode()));
        messageType.setBusinessType(sendMessages.getBusinessType());
        messageType.setEvent(event);
        messageType.setStatusId(retrieveStatusId(statusDataMessage));

        if (personData.getSendedMessages() == null) {
            PersonType.SendedMessages sendedMessages = new PersonType.SendedMessages();
            personData.setSendedMessages(sendedMessages);
        }
        personData.getSendedMessages().getMessage().add(messageType);

        log.info("Обработан статус с ЕНО: {}", eno);

        PersonDocument updateDocument = personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "ReceiveElkMessage"
        );

        // если пришел статус по письму с предложением, то отправим его в ДГИ
        if (sendMessages.getBusinessType().equals(OFFER_LETTER_NOTIFICATION)) {
            if (statusDataMessage.getStatus().getStatusCode() == 1040) {
                dgiElkDeliveryStatusSendService.sendElkOfferLetterStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "1"
                );
            } else if (statusDataMessage.getStatus().getStatusCode() == 1075) {
                dgiElkDeliveryStatusSendService.sendElkOfferLetterStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "2"
                );
            }
        }
        // если пришел статус о готовности проекта договора, то отправим его в ДГИ
        if (sendMessages.getBusinessType().equals(CONTRACT_READY_NOTIFICATION)) {
            if (statusDataMessage.getStatus().getStatusCode() == 1040) {
                dgiElkDeliveryStatusSendService.sendElkContractReadyStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "1"
                );
            } else if (statusDataMessage.getStatus().getStatusCode() == 1075) {
                dgiElkDeliveryStatusSendService.sendElkContractReadyStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "2"
                );
            }
        }
        // если пришел статус о подписанном договоре, то отправим его в ДГИ
        if (sendMessages.getBusinessType().equals(SIGNED_CONTRACT_NOTIFICATION)) {
            if (statusDataMessage.getStatus().getStatusCode() == 1040) {
                dgiElkDeliveryStatusSendService.sendElkSignedContractStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "1"
                );
            } else if (statusDataMessage.getStatus().getStatusCode() == 1075) {
                dgiElkDeliveryStatusSendService.sendElkSignedContractStatusToDgi(
                    updateDocument, messageType.getCustomAttribute(), "2"
                );
            }
        }

        return ElkUserNotificationDto.builder()
            .eno(messageType.getEno())
            .creationDateTime(messageType.getEventDateTime())
            .personDocumentId(personDocument.getId())
            .statusId(messageType.getStatusId())
            .build();
    }

    private String retrieveStatusId(final CoordinateStatusData statusDataMessage) {
        final String code = Integer.toString(statusDataMessage.getStatus().getStatusCode());
        final String subCode = ofNullable(statusDataMessage.getReason())
            .map(DictionaryItem::getCode)
            .orElse(null);
        return Stream.of(code, subCode)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
    }

    /**
     * Создаем запись в сообщених жителя о подаче уведомления.
     *
     * @param personDocument житель
     * @param type           тип сообщения
     * @param fileId         ид файла
     * @param customAttr     кастомный атрибут
     * @param letterId       ид письма с предложением
     */
    private ElkUserNotificationDto createSendedMessageForPerson(
        PersonDocument personDocument, String type, String fileId, String customAttr, String letterId
    ) {
        MessageType messageType = new MessageType();
        messageType.setMessageID(UUID.randomUUID().toString());
        messageType.setEno(personDocument.getDocument().getPersonData().getIntegrationNumberElk());
        messageType.setMessageText(SEND_MESSAGE_TEXT);
        messageType.setEventDateTime(LocalDateTime.now());
        messageType.setApplicant("ИАС УГД");
        messageType.setBusinessType(type);
        messageType.setEvent(SEND_MESSAGE_TEXT + " в ЕЛК");
        messageType.setCustomAttribute(customAttr);
        messageType.setFileId(fileId);

        if (nonNull(letterId)) {
            messageType.setLetterId(letterId);
        }

        if (personDocument.getDocument().getPersonData().getSendedMessages() == null) {
            PersonType.SendedMessages sendedMessages = new PersonType.SendedMessages();
            personDocument.getDocument().getPersonData().setSendedMessages(sendedMessages);
        }
        personDocument.getDocument().getPersonData().getSendedMessages().getMessage().add(messageType);

        return ElkUserNotificationDto.builder()
            .eno(messageType.getEno())
            .creationDateTime(messageType.getEventDateTime())
            .personDocumentId(personDocument.getId())
            .build();
    }

    /**
     * Получить исполнителя по коду статуса.
     *
     * @param statusCode код статуса.
     * @return исполнитель.
     */
    private String getApplicantByStatus(int statusCode) {
        switch (statusCode) {
            case 1010:
                return "ЕТП";
            case 1040:
            case 1030:
            case 1075:
                return "МПГУ";
            default:
                return "";
        }
    }

    /**
     * Отправка запроса в ЕЛК об устранении дефектов.
     *
     * @param personId              id жителя
     * @param cipId                 id цип
     * @param apartmentInspectionId apartmentInspectionId
     * @return статус
     */
    public ResponseEntity<String> sendNotificationFixDefects(
        String personId, String cipId, String apartmentInspectionId
    ) {
        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        return sendNotificationFixDefects(personDocument, cipId, apartmentInspectionId);
    }

    public ResponseEntity<String> sendNotificationFixDefects(
        PersonDocument personDocument, String cipId, String apartmentInspectionId
    ) {
        try {
            if (StringUtils.isBlank(personDocument.getDocument().getPersonData().getSsoID())) {
                personDocument = mdmIntegrationService.updatePersonSsoId(personDocument);
                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
            }
            if (nonNull(personDocument.getDocument().getPersonData().getSsoID())
                && !personDocument.getDocument().getPersonData().getSsoID().isEmpty()) {
                sendNotificationFixDefects(personDocument, true, cipId);
            } else {
                log.warn(
                    "Уведомление об устранении дефектов не было отправлено: отсутствует ssoID у жителя {}",
                    personDocument.getId()
                );
            }
        } catch (Exception e) {
            log.warn("Уведомление об устранении дефектов не было отправлено: {}", e.getMessage());
        }

        if (apartmentInspectionId != null) {
            apartmentInspectionService.setCurrentDefectsEliminatedDate(apartmentInspectionId);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Отправка запроса в ЕЛК об устранении дефектов.
     *
     * @param personDocument житель
     * @param savePerson     сохранять ли жителя
     * @param cipId          id ЦИПа
     */
    public void sendNotificationFixDefects(PersonDocument personDocument, Boolean savePerson, String cipId) {
        final Optional<CipType> optionalCipType = cipService.fetchCipTypeById(cipId);
        final String cipAddress = optionalCipType.map(CipType::getAddress).orElse("-");
        final String cipPhone = optionalCipType.map(CipType::getPhone).orElse("-");

        final String xmlString = elkUserNotificationXmlCreateService
            .createFixDefectsXml(personDocument, cipAddress, cipPhone);

        etpService.exportXml(xmlString, QUEUE_NAME);

        if (savePerson) {
            createSendedMessageForPerson(
                personDocument,
                FIX_DEFECTS_NOTIFICATION,
                xmlUtils.saveXmlToAlfresco(xmlString, personDocument.getFolderId()),
                "",
                null
            );
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }
    }

    /**
     * Sends shipping notification.
     *
     * @param shippingApplicationDocument shippingApplicationDocument
     * @param shippingFlowStatus          shippingFlowStatus
     */
    public void sendShippingNotification(
        final ShippingApplicationDocument shippingApplicationDocument,
        final ShippingFlowStatus shippingFlowStatus,
        final PersonDocument outterPersonDocument
    ) {
        final ShippingApplicationType shippingApplication = Optional.of(shippingApplicationDocument.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .orElseThrow(() -> new SendNotificationException(
                "Некорректная структура заявления с id: " + shippingApplicationDocument.getId()
            ));

        final PersonDocument personDocument = getPersonDocument(
            outterPersonDocument, shippingApplication, shippingApplicationDocument.getId()
        );

        final CipType cip = PersonUtils.getCipId(personDocument)
            .flatMap(cipService::fetchCipById)
            .orElse(null);

        final Function<String, String> shippingTemplateProcessor = createShippingTemplateProcessor(
            shippingFlowStatus, shippingApplication, personDocument, cip
        );

        sendNotification(
            shippingFlowStatus.getNotificationTitle(),
            elkUserNotificationXmlCreateService
                .createXmlByDescriptor(
                    personDocument,
                    shippingFlowStatus,
                    shippingTemplateProcessor
                ),
            personDocument
        );
    }

    private PersonDocument getPersonDocument(
        final PersonDocument outterPersonDocument,
        final ShippingApplicationType shippingApplication,
        final String id
    ) {
        if (outterPersonDocument == null) {
            return ofNullable(shippingApplication)
                .map(ShippingApplicationType::getApplicant)
                .map(ShippingApplicant::getPersonUid)
                .map(personDocumentService::fetchDocument)
                .orElseThrow(() -> new SendNotificationException(
                    "Не найден житель относящися к заявлению с id: " + id
                ));
        }
        return outterPersonDocument;
    }

    /**
     * sendTradeAdditionNotification.
     *
     * @param tradeAdditionNotificationStatus tradeAdditionNotificationStatus
     * @param fileForDeployedTrade            fileForDeployedTradeDocument
     * @param personDocument                  personDocument
     */
    public void sendTradeAdditionNotification(
        final TradeAdditionNotificationData tradeAdditionNotificationStatus,
        final TradeApplicationFileType fileForDeployedTrade,
        final PersonDocument personDocument
    ) {
        final Map<String, String> generalParams = getGeneralTemplateMappings(personDocument);
        generalParams.put("$districtCouncilAddress", "-");
        ofNullable(fileForDeployedTrade).ifPresent(tradeApplicationFileDocument -> {
            if (StringUtils.isNotEmpty(tradeApplicationFileDocument.getChedId())) {
                generalParams.put("$offerLetterLink", getChedFileLink(tradeApplicationFileDocument.getChedId()));
            }
        });
        final Function<String, String> tradeTemplateProcessor = getProcessorFromMappings(generalParams);

        sendNotification(
            tradeAdditionNotificationStatus.getNotificationTitle(),
            elkUserNotificationXmlCreateService
                .createXmlByDescriptor(
                    personDocument,
                    tradeAdditionNotificationStatus,
                    tradeTemplateProcessor
                ),
            personDocument
        );
    }

    public void sendContractReadyForAcquaintingNotification(
        final PersonDocument personDocument
    ) {
        sendGeneralNotification(personDocument, GeneralNotificationsDescriptor.CONTRACT_READY_FOR_ACQUAINTING);
    }

    public void sendGuardianshipAgreementNotification(
        final PersonDocument personDocument,
        final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            GuardianshipNotificationsDescriptor.GUARDIANSHIP_ACCEPT,
            extraTemplateParams
        );
    }

    public void sendGuardianshipDeclineNotification(
        final PersonDocument personDocument, final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            GuardianshipNotificationsDescriptor.GUARDIANSHIP_DECLINE,
            extraTemplateParams
        );
    }

    public void sendNotaryNotification(
        final PersonDocument personDocument,
        final NotaryApplicationFlowStatus notaryApplicationFlowStatus,
        final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            notaryApplicationFlowStatus,
            extraTemplateParams
        );
    }

    public void sendNotaryCompensationNotification(
        final PersonDocument personDocument,
        final NotaryCompensationFlowStatus notaryCompensationFlowStatus,
        final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            notaryCompensationFlowStatus,
            extraTemplateParams
        );
    }

    public void sendContractAppointmentNotification(
        final PersonDocument personDocument,
        final ContractAppointmentFlowStatus contractAppointmentFlowStatus,
        final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            contractAppointmentFlowStatus,
            extraTemplateParams
        );
    }

    public ElkUserNotificationDto sendContractDigitalSignNotification(
        final PersonDocument personDocument,
        final ContractDigitalSignFlowStatus status,
        final Map<String, String> extraTemplateParams
    ) {
        return sendGeneralNotification(
            personDocument,
            status,
            extraTemplateParams,
            config.getElkMsNotificationQueueName()
        );
    }

    public void sendFlatAppointmentNotification(
        final PersonDocument personDocument,
        final FlatAppointmentFlowStatus flatAppointmentFlowStatus,
        final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            flatAppointmentFlowStatus,
            extraTemplateParams
        );
    }

    public void sendPersonalDocumentRequestNotification(
        final PersonDocument personDocument, final Map<String, String> extraTemplateParams
    ) {
        sendGeneralNotification(
            personDocument,
            PersonalDocumentRequestNotificationsDescriptor.REQUEST_CREATED,
            extraTemplateParams
        );
    }

    public void sendResidencePlaceRegistrationNotification(final PersonDocument personDocument) {
        sendGeneralNotification(personDocument, ResidencePlaceRegistrationNotificationsDescriptor.OPTION_AVAILABLE);
    }

    private void sendGeneralNotification(
        final PersonDocument personDocument, final NotificationDescriptor notificationDescriptor
    ) {
        sendGeneralNotification(personDocument, notificationDescriptor, null);
    }

    private ElkUserNotificationDto sendGeneralNotification(
        final PersonDocument personDocument,
        final NotificationDescriptor notificationDescriptor,
        final Map<String, String> extraParameters
    ) {
        return sendGeneralNotification(personDocument, notificationDescriptor, extraParameters, QUEUE_NAME);
    }

    private ElkUserNotificationDto sendGeneralNotification(
        final PersonDocument personDocument,
        final NotificationDescriptor notificationDescriptor,
        final Map<String, String> extraParameters,
        final String queueName
    ) {
        final Map<String, String> generalParams = getGeneralTemplateMappings(personDocument);
        if (extraParameters != null && extraParameters.size() > 0) {
            generalParams.putAll(extraParameters);
        }
        final Function<String, String> templateProcessor = getProcessorFromMappings(generalParams);

        return sendNotification(
            notificationDescriptor.getNotificationTitle(),
            elkUserNotificationXmlCreateService
                .createXmlByDescriptor(
                    personDocument,
                    notificationDescriptor,
                    templateProcessor
                ),
            personDocument,
            queueName
        );
    }

    private Function<String, String> getProcessorFromMappings(final Map<String, String> valueMapping) {
        final AtomicReference<String> updatedHttpTemplate = new AtomicReference<>();
        return httpTemplate -> {
            updatedHttpTemplate.set(httpTemplate);
            MapUtils.emptyIfNull(valueMapping)
                .forEach((key, value) -> updatedHttpTemplate
                    .set(updatedHttpTemplate.get()
                        .replace(key, StringUtils.defaultIfEmpty(value, StringUtils.EMPTY))));
            return updatedHttpTemplate.get();
        };
    }

    private Map<String, String> getGeneralTemplateMappings(final PersonDocument personDocument) {
        final Map<String, String> templateMappings = new HashMap<>();
        PersonUtils.getOfferLetterChedId(personDocument)
            .ifPresent(
                chedId -> templateMappings.put("$offerLetterLink", getChedFileLink(chedId))
            );
        PersonUtils.getLastContractChedId(personDocument)
            .ifPresent(
                chedId -> templateMappings.put("$contractLink", getChedFileLink(chedId))
            );
        final CipType cip = cipService.getPersonCip(personDocument).orElse(null);
        populateCipData(templateMappings, cip);
        templateMappings.put("$io", retrieveFirstAndMiddleName(personDocument));
        return templateMappings;
    }

    private void populateCipData(final Map<String, String> templateMappings, final CipType cip) {
        templateMappings.put("$cipAddress", ofNullable(cip).map(CipType::getAddress).orElse("-"));
        templateMappings.put("$cipPhone", ofNullable(cip).map(CipType::getPhone).orElse("-"));
    }

    private Function<String, String> createShippingTemplateProcessor(
        final ShippingFlowStatus shippingFlowStatus,
        final ShippingApplicationType shippingApplication,
        final PersonDocument personDocument,
        final CipType cip
    ) {
        if (nonNull(shippingFlowStatus.getNotificationTitle())
            && nonNull(shippingFlowStatus.getElkNotificationTemplatePath())
            && nonNull(shippingFlowStatus.getEmailNotificationTemplatePath())
        ) {
            return createShippingTemplateProcessor(shippingApplication, personDocument, cip);
        }

        return Function.identity();
    }

    private Function<String, String> createShippingTemplateProcessor(
        final ShippingApplicationType shippingApplication, final PersonDocument personDocument, final CipType cip
    ) {
        final LocalDateTime moveDateTime = shippingApplication.getShippingDateStart();
        final String moveDate = moveDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final String moveTime = moveDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        final LocalDateTime cancelLocalDate = moveDateTime.minusDays(1);
        final String cancelDate = cancelLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        final String cancelTime = cancelLocalDate.format(DateTimeFormatter.ofPattern("HH:mm"));

        final String oldAddress = retrieveAddress(shippingApplication.getApartmentFrom());
        final String newAddress = retrieveAddress(shippingApplication.getApartmentTo());

        final String firstAndMiddleName = retrieveFirstAndMiddleName(personDocument);

        final String cipAddress = ofNullable(cip).map(CipType::getAddress).orElse(null);
        final String cipPhone = ofNullable(cip).map(CipType::getPhone).orElse(null);

        return htmlTemplate -> htmlTemplate
            .replace("$io", nullableUtils.retrieveNullable(firstAndMiddleName))
            .replace("$oldAddress", nullableUtils.retrieveNullable(oldAddress))
            .replace("$newAddress", nullableUtils.retrieveNullable(newAddress))
            .replace("$moveDate", nullableUtils.retrieveNullable(moveDate))
            .replace("$moveTime", nullableUtils.retrieveNullable(moveTime))
            .replace("$newMoveDate", nullableUtils.retrieveNullable(moveDate))
            .replace("$newMoveTime", nullableUtils.retrieveNullable(moveTime))
            .replace("$cancelDate", nullableUtils.retrieveNullable(cancelDate))
            .replace("$cancelTime", nullableUtils.retrieveNullable(cancelTime))
            .replace("$cipAddress", nullableUtils.retrieveNullable(cipAddress))
            .replace("$cipPhone", nullableUtils.retrieveNullable(cipPhone));
    }

    private String retrieveFirstAndMiddleName(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFIO)
            .map(String::trim)
            .filter(fio -> fio.length() != 0)
            .map(fio -> fio.substring(fio.indexOf(" ") + 1))
            .orElse("");
    }

    private String retrieveAddress(final Apartment apartment) {
        return ofNullable(apartment)
            .map(Apartment::getStringAddress)
            .orElse("-");
    }

    private void sendNotification(
        final String notificationName,
        final String notificationXml,
        final PersonDocument personDocument
    ) {
        sendNotification(notificationName, notificationXml, personDocument, QUEUE_NAME);
    }

    private ElkUserNotificationDto sendNotification(
        final String notificationName,
        final String notificationXml,
        final PersonDocument personDocument,
        final String queueName
    ) {
        log.debug(
            "Start notification sending: personDocumentId = {}, notificationName = {}, notificationXml = {}",
            personDocument.getId(), notificationName, notificationXml
        );
        etpService.exportXml(notificationXml, queueName);

        final ElkUserNotificationDto elkUserNotificationDto = createSendedMessageForPerson(
            personDocument,
            notificationName,
            xmlUtils.saveXmlToAlfresco(notificationXml, personDocument.getFolderId()),
            "",
            null
        );
        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "sendNotification");

        log.debug(
            "Finish notification sending: personDocumentId = {}, notificationName = {}, notificationXml = {}",
            personDocument.getId(), notificationName, notificationXml
        );

        return elkUserNotificationDto;
    }

    private String getChedFileLink(final String chedId) {
        return chedFileService.getChedFileLink(chedId).orElse(CHED_DEFAULT_LINK);
    }
}
