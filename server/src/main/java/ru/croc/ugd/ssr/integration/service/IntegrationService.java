package ru.croc.ugd.ssr.integration.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SendEmailService;
import ru.croc.ugd.ssr.service.egrn.EgrnBuildingRequestService;
import ru.croc.ugd.ssr.service.mdm.MdmExternalPersonInfoService;
import ru.croc.ugd.ssr.service.pfr.PfrSnilsRequestService;
import ru.reinform.cdp.bus.rest.api.BusRestApi;
import ru.reinform.cdp.bus.rest.model.BusRequest;
import ru.reinform.cdp.bus.rest.model.SyncResponse;
import ru.reinform.cdp.exception.RINotFoundException;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Интеграционный.
 */
@Service
@RequiredArgsConstructor
public class IntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationService.class);

    private final AsurIntegrationService asurIntegrationService;
    private final BusRestApi busRestApi;
    private final MdmIntegrationService mdmIntegrationService;
    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final ElkUserNotificationService elkUserNotificationService;
    private final SendEmailService sendEmailService;
    private final EgrnBuildingRequestService egrnBuildingRequestService;
    private final MdmExternalPersonInfoService mdmExternalPersonInfoService;
    private final PfrSnilsRequestService pfrSnilsRequestService;

    @Value("${ugd.ssr.new-update-snils.enabled:false}")
    private boolean newUpdateSnilsEnabled;
    @Value("${ugd.ssr.new-update-snils.mdm-request.enabled:false}")
    private boolean newUpdateSnilsMdmRequestEnabled;

    /**
     * Обновление домов из ЕЖД.
     *
     * @param realEstateIds ид домов
     * @param username      ФИО пользователя
     */
    @Async
    public void updateRealEstatesFromEzd(List<String> realEstateIds, String username) {
        for (String realEstateId : realEstateIds) {
            updateRealEstateFromEzd(realEstateId, username);
        }
    }

    /**
     * Обновление дома из ЕЖД.
     *
     * @param realEstateId ид дома
     * @param username     ФИО пользователя
     */
    public void updateRealEstateFromEzd(String realEstateId, String username) {
        RealEstateDocument realEstateDocument;
        try {
            realEstateDocument = realEstateDocumentService.fetchDocument(realEstateId);
        } catch (RINotFoundException ex) {
            LOG.error(ex.toString());
            return;
        }
        egrnBuildingRequestService.requestByRealEstate(realEstateDocument);
        mdmExternalPersonInfoService.requestByRealEstate(realEstateDocument);
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        String unom = realEstateData.getUNOM() != null ? realEstateData.getUNOM().toString() : null;
        if (unom != null) {
            sendEmailService.sendEmailStartResettlement(realEstateData.getUNOM().toString(), username);
            if (isNull(realEstateData.getFlats())) {
                LOG.error("У REAL-ESTATE id: {} отсутствуют квартиры!", realEstateId);
                return;
            }
            List<FlatType> flats = realEstateData.getFlats().getFlat();
            flats.forEach(flat -> updateFlatFromEzdByUnom(flat, unom));

            realEstateData.setEnrichingFlag(true);
            realEstateData.setUpdatedDate(LocalDate.now());
            realEstateData.setUpdatedStatus("в процессе обогащения");
        } else {
            realEstateData.setUpdatedDate(LocalDate.now());
            realEstateData.setUpdatedStatus("UNOM отсутствует - обогащение невозможно");
        }
        realEstateDocumentService.updateDocument(
            realEstateId, realEstateDocument, true, true, ""
        );
    }

    /**
     * Обновление квартир из ЕЖД.
     *
     * @param flatIds  ид квартир
     * @param username ФИО пользователя
     */
    @Async
    public void updateFlatsFromEzd(List<String> flatIds, String username) {
        HashSet<String> unoms = new HashSet<>();
        for (String flatId : flatIds) {
            updateFlatFromEzd(flatId);
            unoms.add(realEstateDocumentService.getUnomByFlatId(flatId));
        }

        for (String unom : unoms) {
            egrnBuildingRequestService.requestByRealEstateUnom(unom);
            sendEmailService.sendEmailStartResettlement(unom, username);
        }
    }

    /**
     * Обновление квартиры из ЕЖД.
     *
     * @param flatId ид квартиры
     */
    public void updateFlatFromEzd(String flatId) {
        RealEstateDocument realEstateDocument;
        try {
            realEstateDocument = realEstateDocumentService.fetchByFlatId(flatId);
        } catch (RINotFoundException ex) {
            LOG.error(ex.toString());
            return;
        }

        FlatType flatType = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat()
            .stream().filter(flat -> flat.getFlatID().equals(flatId)).findFirst().get();

        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        String unom = realEstateData.getUNOM() != null ? realEstateData.getUNOM().toString() : null;
        if (unom != null) {
            updateFlatFromEzdByUnom(flatType, unom);
        } else {
            realEstateData.setUpdatedDate(LocalDate.now());
            realEstateData.setUpdatedStatus("UNOM отсутствует - обогащение невозможно");
        }
        realEstateData.setEnrichingFlag(true);
        realEstateData.setUpdatedDate(LocalDate.now());
        realEstateData.setUpdatedStatus("в процессе обогащения");
        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, ""
        );
    }

    /**
     * Обновление квартиры по уному.
     *
     * @param flat квартира
     * @param unom уном
     */
    public void updateFlatFromEzdByUnom(FlatType flat, String unom) {
        BusRequest busRequest = new BusRequest();
        busRequest.setDocument(asurIntegrationService.createGetApartmentDataRequestXml(flat, unom));
        busRequest.setDocumentType("getApartmentData");
        busRequest.setSystemID("ugd_ssr");
        busRestApi.sync(busRequest);
    }

    /**
     * Получить СНИЛС жителя по ид.
     *
     * @param personId ид жителя
     */
    public void updateSnilsFromPfr(final String personId) {
        try {
            PersonDocument personDocument = personDocumentService.fetchDocument(personId);
            updateSnilsFromPfr(personDocument);
        } catch (RINotFoundException ex) {
            LOG.error(ex.getMessage());
        }
    }

    /**
     * Получить СНИЛС жителя.
     *
     * @param document житель
     */
    public void updateSnilsFromPfr(final PersonDocument document) {
        if (newUpdateSnilsEnabled) {
            updateSnilsFromPfrNew(document);
        } else {
            updateSnilsFromPfrLegacy(document);
        }
    }

    /**
     * Получить СНИЛС жителя.
     *
     * @param document житель
     */
    public void updateSnilsFromPfrNew(final PersonDocument document) {
        PersonDocument updatedDocument = document;
        if (newUpdateSnilsMdmRequestEnabled) {
            updatedDocument = mdmExternalPersonInfoService.updatePersonFromMdmExternal(document);
        } else {
            LOG.info("updateSnilsFromPfr: mdm request disabled");
        }
        pfrSnilsRequestService.createByPersonDocumentList(Collections.singletonList(updatedDocument));
    }

    /**
     * Получить СНИЛС жителя.
     *
     * @param document житель
     */
    public void updateSnilsFromPfrLegacy(final PersonDocument document) {
        final PersonType personData = document.getDocument().getPersonData();
        //сначала отправим по мужчине
        final BusRequest busRequestM = new BusRequest();
        busRequestM.setDocument(asurIntegrationService.createGetSnilsRequestXml(personData, "1"));
        busRequestM.setDocumentType("getSNILS");
        busRequestM.setSystemID("ugd_ssr");
        //теперь отправим по женщине
        final BusRequest busRequestF = new BusRequest();
        busRequestF.setDocument(asurIntegrationService.createGetSnilsRequestXml(personData, "2"));
        busRequestF.setDocumentType("getSNILS");
        busRequestF.setSystemID("ugd_ssr");
        busRestApi.sync(busRequestM);
        busRestApi.sync(busRequestF);
        personDocumentService.updateDocument(document.getId(), document, true, true, "");
    }

    /**
     * Тестовый запрос на обогащение дома данными из ЕЖД.
     *
     * @param createTestEstate С созданием тестового дома
     * @param flatNumber       Номер квартиры, по умолчанию 23
     */
    public void testUpdateRealEstateFromEzd(Boolean createTestEstate, String flatNumber) {
        RealEstateDataType realEstateDataType = new RealEstateDataType();
        realEstateDataType.setUNOM(BigInteger.valueOf(6489));
        realEstateDataType.setAddress("Тестовая улица");
        realEstateDataType.setUpdatedStatus("в процессе обогащения");
        realEstateDataType.setUpdatedDate(LocalDate.now());
        FlatType flatType = new FlatType();
        flatType.setFlatID("testValue23");
        flatType.setGlobalID(BigInteger.valueOf(2311111));
        flatType.setApartmentL4VALUE(flatNumber);
        RealEstateDataType.Flats flats = new RealEstateDataType.Flats();
        flats.getFlat().add(flatType);
        realEstateDataType.setFlats(flats);
        RealEstate realEstate = new RealEstate();
        realEstate.setRealEstateData(realEstateDataType);
        RealEstateDocument realEstateDocument = new RealEstateDocument();
        realEstateDocument.setDocument(realEstate);

        BusRequest busRequest = new BusRequest();
        busRequest.setDocument(asurIntegrationService.createGetApartmentDataRequestXml(flatType, "6489"));
        busRequest.setDocumentType("getApartmentData");
        busRequest.setSystemID("ugd_ssr");
        busRestApi.sync(busRequest);
        if (createTestEstate) {
            realEstateDocumentService.createDocument(realEstateDocument, true, "");
        }
    }

    /**
     * Отправка сообщения в ЕЛК (начало переселения) по всем жителям дома.
     *
     * @param realEstateId Id дома
     * @param cipId        id ЦИП
     * @return статус
     */
    public ResponseEntity<String> sendRequestToElkByRealEstateId(String realEstateId, String cipId) {
        List<PersonDocument> documents = personDocumentService.fetchByRealEstateId(realEstateId);
        return sendRequestToElkByDocuments(documents, cipId);
    }

    /**
     * Отправка сообщения в ЕЛК (начало переселения) по всем жителям квартиры.
     *
     * @param flatId Id квартиры
     * @param cipId  id ЦИП
     * @return статус
     */
    public ResponseEntity<String> sendRequestToElkByFlatId(String flatId, String cipId) {
        List<PersonDocument> documents = personDocumentService.fetchByFlatId(flatId);
        return sendRequestToElkByDocuments(documents, cipId);
    }

    private ResponseEntity<String> sendRequestToElkByDocuments(List<PersonDocument> documents, String cipId) {
        if (documents.size() > 0) {
            documents.forEach(personDocument -> {
                elkUserNotificationService.sendNotificationStartRenovation(personDocument, true, cipId);
            });
            return ResponseEntity.ok("ok");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Тестовый запрос на получение СНИЛС.
     *
     * @return busMessageId
     */
    public String testGetSnilsFromPfr() {
        BusRequest busRequest = new BusRequest();
        PersonType personType = new PersonType();
        personType.setFIO("Сапожникова Нина Георгиевна");
        personType.setBirthDate(LocalDate.of(1935, 12, 11));
        busRequest.setDocument(asurIntegrationService.createGetSnilsRequestXml(personType, "2"));
        busRequest.setDocumentType("getSNILS");
        busRequest.setSystemID("ugd_ssr");
        SyncResponse response = busRestApi.sync(busRequest);
        return response.getMessageID().toString();
    }

    /**
     * Запрос на парсинг сообщения от ЕЛК (ЕТП МВ).
     *
     * @param message message
     */
    public void parseMessageFromElk(String message) {
        elkUserNotificationService.parseReceiveElkMessage(message);
    }

    /**
     * Обновление ssoId жителей по ид домов.
     *
     * @param ids      ид домов
     * @param username ФИО пользователя
     */
    @Async
    public void updatePersonsSsoIdByRealEstateIds(List<String> ids, String username) {
        for (String id : ids) {
            String unom = realEstateDocumentService.getUnomByRealEstateId(id);
            sendEmailService.sendEmailStartResettlement(unom, username);
            try {
                updatePersonsSsoIdByRealEstateId(id);
            } catch (Exception e) {
                LOG.error("При обогащении жителя из мос.ру произошла ошибка: {}", e.getMessage());
                sendEmailService.sendEmailErrorResettlement(unom, e.getMessage());
            }
        }
    }

    /**
     * Обновить данные по ЛК(SsoId) для всех жителей дома.
     *
     * @param id Id дома
     */
    public void updatePersonsSsoIdByRealEstateId(String id) {
        List<PersonDocument> personDocuments = personDocumentService.fetchByRealEstateId(id);
        for (PersonDocument personDocument : personDocuments) {
            PersonType personData = personDocument.getDocument().getPersonData();
            String snils = personData.getSNILS();
            if (nonNull(snils) && !snils.isEmpty()) {
                personData.setSsoID(mdmIntegrationService.getSsoId(snils));
                personData.setUpdatedFromELKdate(LocalDate.now());
                personData.setUpdatedFromELKstatus("обогащено");
                personDocumentService.updateDocument(
                    personDocument.getId(), personDocument, true, true, ""
                );
            }

        }
    }

    /**
     * Обновление статусов интеграции.
     */
    public void updateIntegrationStatuses() {
        List<RealEstateDocument> realEstateDocuments;
        int pageNum = 0;
        do {
            realEstateDocuments = realEstateDocumentService.fetchNotUpdated(pageNum++, 10);
            realEstateDocuments.forEach(this::updateStatusesByUnom);
            LOG.debug("Обработано " + realEstateDocuments.size() + " записей");
        }
        while (realEstateDocuments.size() == 10);
    }

    private void updateStatusesByUnom(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        if (realEstateData == null || realEstateData.getFlats() == null) {
            return;
        }

        List<FlatType> flats = realEstateData.getFlats().getFlat();
        for (FlatType flat : flats) {
            if (nonNull(flat.getUpdatedFromEZDstatus())) {
                if ("Запрашиваемые сведения не найдены".equals(flat.getUpdatedFromEZDstatus())) {
                    flat.setUpdatedFullStatus("обогащено");
                    if (nonNull(flat.getUpdatedFromEZDdate())) {
                        flat.setUpdatedFullDate(flat.getUpdatedFromEZDdate());
                    }
                } else if ("обогащено".equals(flat.getUpdatedFromEZDstatus())) {
                    List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(flat.getFlatID());
                    for (PersonDocument personDocument : personDocuments) {
                        PersonType personData = personDocument.getDocument().getPersonData();
                        if (nonNull(personData.getUpdatedFromPFRstatus())
                            && "Запрашиваемые сведения не найдены".equals(personData.getUpdatedFromPFRstatus())) {
                            personData.setUpdatedFromELKstatus("обогащено");
                            if (nonNull(personData.getUpdatedFromPFRdate())) {
                                personData.setUpdatedFromELKdate(personData.getUpdatedFromPFRdate());
                            }
                            personDocumentService.updateDocument(
                                personDocument.getId(), personDocument, true, true, ""
                            );
                        }
                    }
                }

            }
        }
        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, "updateStatuses"
        );
    }
}
