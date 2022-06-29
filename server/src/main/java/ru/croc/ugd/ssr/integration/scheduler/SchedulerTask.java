package ru.croc.ugd.ssr.integration.scheduler;

import static java.util.Objects.nonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SendEmailService;
import ru.croc.ugd.ssr.service.SuccessfullUpdatedStatusStatisticsService;
import ru.croc.ugd.ssr.service.pfr.PfrSnilsRequestService;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс с задачами для планировщика интеграции.
 */
public class SchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerTask.class);

    @Value("${integration.updateLimitedRequestsScheduler}")
    private Boolean updateLimitedRequestsScheduler;

    @Value("${integration.updateStatusesScheduler}")
    private Boolean updateStatusesScheduler;

    @Value("${integration.batchSize:10}")
    private Integer batchSize;

    @Value("${schedulers.personsPrfUpdate.enable:false}")
    private Boolean personsPrfUpdateEnable;

    @Autowired
    private Environment environment;

    @Autowired
    private PersonDocumentService personDocumentService;

    @Autowired
    private RealEstateDocumentService realEstateDocumentService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private RiAuthenticationUtils riAuthenticationUtils;

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private SuccessfullUpdatedStatusStatisticsService successfullUpdatedStatusStatisticsService;

    @Autowired
    private PfrSnilsRequestService pfrSnilsRequestService;

    /**
     * Таск на интеграцию по объектам со статусом "Превышен суточный лимит запросов на документ".
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateLimitedRequests() {
        if (updateLimitedRequestsScheduler) {
            LOG.info(
                "SchedulerTask: Началась запланированная задача на интеграцию по объектам со статусом "
                    + "'Превышен суточный лимит запросов на документ'"
            );
            riAuthenticationUtils.setSecurityContextByServiceuser();
            LOG.info("updateSnilsFromPfr: updateLimitedRequests");
            pfrSnilsRequestService.processLimitedRequests();
            updateLimitedRequestsPersonPfr();
            updateLimitedRequestsRealEstateEzd();
            LOG.info(
                "SchedulerTask: Завершилась запланированная задача на интеграцию по объектам со статусом "
                    + "'Превышен суточный лимит запросов на документ'"
            );

            LOG.info("Началась запланированная задача на подсчет кол-ва ssoId");
            updateSsoIdCount();
            LOG.info("Завершилась запланированная задача на подсчет кол-ва ssoId");
        }
    }

    /**
     * Метод для тестирования - запуск таска на обновление только для дома по UNOM.
     *
     * @param unom UNOM дома
     */
    public void updateLimitedRequestsWithUnom(String unom) {
        riAuthenticationUtils.setSecurityContextByServiceuser();
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (realEstateDocument == null) {
            LOG.info("RealEstateDocument с UNOM=" + unom + " не найден.");
            return;
        }
        updateLimitedRequestsRealEstateEzdByUnom(realEstateDocument);
    }

    /**
     * Таск на обновление статусов интеграции домов и квартир (каждые 3 часа).
     */
    @Scheduled(fixedDelay = 3 * 60 * 60 * 1000)
    public void updateStatuses() {
        if (updateStatusesScheduler) {
            LOG.info(
                "SchedulerTask: Началась запланированная задача на обновление статусов интеграции домов и квартир"
            );
            riAuthenticationUtils.setSecurityContextByServiceuser();

            realEstateDocumentService.fetchNotUpdatedIds().forEach(this::updateStatusesRealEstateById);

            LOG.info(
                "SchedulerTask: Завершилась запланированная задача на обновление статусов интеграции домов и квартир"
            );
        }
    }

    /**
     * Метод для тестирования - запуск таска на обновление только для дома по UNOM.
     *
     * @param unom UNOM дома
     */
    public void updateStatusesByUnom(String unom) {
        riAuthenticationUtils.setSecurityContextByServiceuser();
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (realEstateDocument == null) {
            LOG.info("RealEstateDocument с UNOM=" + unom + " не найден.");
            return;
        }
        updateStatusesRealEstate(realEstateDocument);
    }

    private void updateStatusesRealEstateById(String id) {
        LOG.debug("Обработка дома с id: {}", id);
        updateStatusesRealEstate(realEstateDocumentService.fetchDocument(id));
        LOG.debug("Дом с id: {} обработан", id);
    }

    private void updateStatusesRealEstate(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        if (realEstateData == null || realEstateData.getFlats() == null) {
            return;
        }
        LocalDate maxRealEstateDate = null;
        LocalDate maxRealEstateInitDate = null;

        List<FlatType> flats = realEstateData.getFlats().getFlat();
        for (FlatType flat : flats) {
            String updatedFromEzdStatus = flat.getUpdatedFromEZDstatus();
            List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(flat.getFlatID());
            LocalDate maxFlatDate = getMaxDateOfFlatPersonsUpdated(personDocuments);
            if (updatedFromEzdStatus == null || updatedFromEzdStatus.equals("не обогащалось")) {
                flat.setUpdatedFullStatus("не обогащалось");
            } else if ("обогащено".equals(updatedFromEzdStatus) && maxFlatDate != null) {
                flat.setUpdatedFullStatus("обогащено");
                flat.setUpdatedFullDate(maxFlatDate);

                if (maxRealEstateDate == null || maxFlatDate.isAfter(maxRealEstateDate)) {
                    maxRealEstateDate = maxFlatDate;
                }
            } else if ("обогащено".equals(updatedFromEzdStatus) && personDocuments.isEmpty()) {
                flat.setUpdatedFullStatus("обогащено");
                flat.setUpdatedFullDate(flat.getUpdatedFromEZDdate());
            } else {
                LocalDate minFlatDate = getMinDateOfFlatPersonsUpdated(personDocuments, flat.getUpdatedFromEZDdate());
                if (minFlatDate != null) {
                    flat.setUpdatedFullDate(minFlatDate);
                    if (maxRealEstateInitDate == null || minFlatDate.isAfter(maxRealEstateInitDate)) {
                        maxRealEstateInitDate = minFlatDate;
                    }
                }
            }
        }

        if (flats.stream().allMatch(flat -> "обогащено".equals(flat.getUpdatedFullStatus()))) {
            realEstateData.setUpdatedStatus("обогащено");
            realEstateData.setUpdatedDate(maxRealEstateDate);
            // отправим письмо об окончании обогащения, если включена отправка
            if (Boolean.parseBoolean(environment.getProperty("integration.notification.enable"))) {
                sendSuccessEmail(realEstateDocument);
            }
        } else if (
            flats.stream().noneMatch(flat -> "обогащено".equals(flat.getUpdatedFromEZDstatus())
                || "в процессе обогащения".equals(flat.getUpdatedFromEZDstatus()))
        ) {
            realEstateData.setUpdatedStatus("не обогащалось");
        } else {
            realEstateData.setUpdatedStatus("в процессе обогащения");
            realEstateData.setUpdatedDate(maxRealEstateInitDate);
        }

        realEstateData.setFlats(
            mergeFlats(
                flats,
                realEstateDocumentService.fetchDocument(realEstateDocument.getId())
                    .getDocument().getRealEstateData().getFlats()
            )
        );

        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, "updateStatuses"
        );
    }

    /**
     * Определяет последнюю дату обновления жителя по ПФР и ЕЛК.
     * Если не обогащенный житель возвращает null.
     *
     * @param personDocuments список жителей
     * @return макс. дата или null, если не все жители обогащены
     */
    private LocalDate getMaxDateOfFlatPersonsUpdated(List<PersonDocument> personDocuments) {
        LocalDate maxDate = null;
        for (PersonDocument personDocument : personDocuments) {
            PersonType personData = personDocument.getDocument().getPersonData();
            if (personData == null || !"обогащено".equals(personData.getUpdatedFromELKstatus())) {
                return null;
            }

            LocalDate updatedFromElkDate = personData.getUpdatedFromELKdate();
            if (maxDate == null) {
                maxDate = updatedFromElkDate;
            }
            if (updatedFromElkDate != null && updatedFromElkDate.isAfter(maxDate)) {
                maxDate = updatedFromElkDate;
            }
            LocalDate updatedFromPfrDate = personData.getUpdatedFromPFRdate();
            if (maxDate == null) {
                maxDate = updatedFromPfrDate;
            }
            if (updatedFromPfrDate != null && updatedFromPfrDate.isAfter(maxDate)) {
                maxDate = updatedFromPfrDate;
            }
        }
        return maxDate;
    }

    /**
     * Возвращается наименьшая из дат отправки запросов по квартире или жителям, по которым не было получено ответа.
     *
     * @param personDocuments    жители
     * @param updatedFromEzdDate дата обогащения квартиры ЕЖД
     * @return минималльная дата
     */
    private LocalDate getMinDateOfFlatPersonsUpdated(
        List<PersonDocument> personDocuments, LocalDate updatedFromEzdDate
    ) {
        LocalDate result = updatedFromEzdDate;
        for (PersonDocument personDocument : personDocuments) {
            PersonType personData = personDocument.getDocument().getPersonData();
            if (personData == null) {
                continue;
            }

            String updatedFromElkStatus = personData.getUpdatedFromELKstatus();
            if ("в процессе обогащения".equals(updatedFromElkStatus)
                || "Превышен суточный лимит запросов на документ".equals(updatedFromElkStatus)) {
                LocalDate updatedFromElkDate = personData.getUpdatedFromELKdate();
                if (updatedFromElkDate != null && result.isAfter(updatedFromElkDate)) {
                    result = updatedFromElkDate;
                }
            }
            String updatedFromPfrStatus = personData.getUpdatedFromPFRstatus();
            if ("в процессе обогащения".equals(updatedFromPfrStatus)
                || "Превышен суточный лимит запросов на документ".equals(updatedFromPfrStatus)) {
                LocalDate updatedFromPfrDate = personData.getUpdatedFromPFRdate();
                if (updatedFromPfrDate != null && result.isAfter(updatedFromPfrDate)) {
                    result = updatedFromPfrDate;
                }
            }
        }
        return result;
    }

    private void updateLimitedRequestsPersonPfr() {
        List<String> personDocumentIds = personDocumentService.fetchIdsWithLimitedRequests();
        LOG.info("updateSnilsFromPfr: updateLimitedRequestsPersonPfr");
        personDocumentIds.forEach(personDocumentId -> integrationService.updateSnilsFromPfr(personDocumentId));
    }

    private void updateLimitedRequestsRealEstateEzd() {
        List<RealEstateDocument> realEstateDocuments;
        int pageNum = 0;
        do {
            realEstateDocuments = realEstateDocumentService.fetchNotUpdated(pageNum++, batchSize);
            realEstateDocuments.forEach(this::updateLimitedRequestsRealEstateEzdByUnom);
            LOG.debug("Обработано " + realEstateDocuments.size() + " записей");
        }
        while (realEstateDocuments.size() == batchSize);
    }

    private void updateLimitedRequestsRealEstateEzdByUnom(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        if (realEstateData == null) {
            return;
        }
        if (realEstateData.getFlats() == null) {
            return;
        }

        String unom = realEstateData.getUNOM().toString();
        List<FlatType> flats = realEstateData.getFlats().getFlat();
        flats
            .stream()
            .filter(flat -> nonNull(flat.getUpdatedFromEZDstatus()))
            .filter(flat -> flat.getUpdatedFromEZDstatus().equals("Превышен суточный лимит запросов на документ"))
            .forEach(flat -> integrationService.updateFlatFromEzdByUnom(flat, unom));
        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, "updateLimitedRequests Task"
        );
    }

    /**
     * Обновить количество ssoId.
     */
    public void updateSsoIdCount() {
        List<RealEstateDocument> realEstateDocuments;
        int pageNum = 0;
        do {
            realEstateDocuments = realEstateDocumentService.fetchUpdatedAndProgress(pageNum++, batchSize);
            realEstateDocuments.forEach(this::updateSsoIdCountForRealEstate);
            LOG.debug("Обработано " + realEstateDocuments.size() + " записей");
        }
        while (realEstateDocuments.size() == batchSize);
    }

    private void updateSsoIdCountForRealEstate(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        if (realEstateData == null) {
            return;
        }

        int ssoIdCount;
        List<PersonDocument> personDocuments = personDocumentService
                .fetchByRealEstateId(realEstateDocument.getId())
                .stream()
                .filter(pd -> !pd.getDocument().getPersonData().isIsArchive())
                .collect(Collectors.toList());
        ssoIdCount = (int) personDocuments
                .stream()
                .filter(pd -> nonNull(pd.getDocument().getPersonData().getSsoID()))
                .count();

        realEstateData.setSsoIdCount(ssoIdCount + "/" + personDocuments.size());
        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, "updateSsoIdCount"
        );
    }

    private void sendSuccessEmail(RealEstateDocument realEstateDocument) {
        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
        String body = successfullUpdatedStatusStatisticsService.buildSuccessEmailBody(realEstateData);
        sendEmailService.sendSuccessUpdateRealEstateEmail(
            body,
            "Обогащение дома с UNOM " + realEstateData.getUNOM()
        );
    }

    /**
     * Таск на обновление жителей из ПФР.
     */
    @Scheduled(cron = "${schedulers.personsPrfUpdate.period:0 0 23 * * FRI}")
    public void updatePersonsFromPfr() {
        if (!personsPrfUpdateEnable) {
            return;
        }

        LOG.info("Началась запланированная задача на обновление жителей из ПФР");
        
        riAuthenticationUtils.setSecurityContextByServiceuser();

        List<String> personsId = personDocumentService.fetchAllPersonIdsForPfrUpdate();
        LOG.info("updateSnilsFromPfr: updatePersonsFromPfr");
        for (String personId : personsId) {
            integrationService.updateSnilsFromPfr(personId);
        }

        LOG.info("Задача на обновление жителей из ПФР завершена");
    }

    private RealEstateDataType.Flats mergeFlats(List<FlatType> localFlats, RealEstateDataType.Flats dbFlats) {
        for (FlatType flatType : dbFlats.getFlat()) {
            localFlats
                .stream()
                .filter(flat -> flat.getFlatID().equals(flatType.getFlatID()))
                .findFirst()
                .ifPresent(flat -> {
                    if (nonNull(flat.getUpdatedFullStatus())) {
                        flatType.setUpdatedFullStatus(flat.getUpdatedFullStatus());
                    }
                    if (nonNull(flat.getUpdatedFullDate())) {
                        flatType.setUpdatedFullDate(flat.getUpdatedFullDate());
                    }
                });
        }

        return dbFlats;
    }
}
