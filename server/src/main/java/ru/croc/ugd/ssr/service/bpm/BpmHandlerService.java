package ru.croc.ugd.ssr.service.bpm;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ArmApplyRequestType;
import ru.croc.ugd.ssr.ArmIssueOfferLetterRequestType;
import ru.croc.ugd.ssr.ArmShowApartRequestType;
import ru.croc.ugd.ssr.BuildingsInResettlementProcess;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DashboardData;
import ru.croc.ugd.ssr.DashboardRequestType;
import ru.croc.ugd.ssr.DgiType;
import ru.croc.ugd.ssr.DgpType;
import ru.croc.ugd.ssr.DsType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.FondType;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.NewBuildingsFlats;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.ResettlementBuildings;
import ru.croc.ugd.ssr.ResettlementBuildingsResidents;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.SoonResettlementRequestType;
import ru.croc.ugd.ssr.Tasks;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.integration.service.flows.BeginningRessetlementFlowsService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.ResettlementInfoMfrFlowService;
import ru.croc.ugd.ssr.model.ArmApplyRequestDocument;
import ru.croc.ugd.ssr.model.ArmIssueOfferLetterRequestDocument;
import ru.croc.ugd.ssr.model.ArmShowApartRequestDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.SoonResettlementRequestDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStartRemovalType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.SoonResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.arm.ArmApplyRequestDocumentService;
import ru.croc.ugd.ssr.service.arm.ArmIssueOfferLetterRequestDocumentService;
import ru.croc.ugd.ssr.service.arm.ArmShowApartRequestDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.dashboard.DashboardDocumentService;
import ru.croc.ugd.ssr.service.dashboard.DashboardRequestDocumentService;
import ru.croc.ugd.ssr.service.egrn.EgrnBuildingRequestService;
import ru.reinform.cdp.security.security.RiAuthenticationToken;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;
import ru.reinform.cdp.utils.rest.utils.SendRestServiceAuthUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис по обработке запросов БП.
 */
@Service
@AllArgsConstructor
public class BpmHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(BpmHandlerService.class);

    private final UserService userService;
    private final PersonDocumentService personDocumentService;
    private final ArmApplyRequestDocumentService armApplyRequestDocumentService;
    private final ArmIssueOfferLetterRequestDocumentService armIssueOfferLetterRequestDocumentService;
    private final ArmShowApartRequestDocumentService armShowApartRequestDocumentService;
    private final SoonResettlementRequestDocumentService soonResettlementRequestDocumentService;
    private final DashboardRequestDocumentService dashboardRequestDocumentService;
    private final DashboardDocumentService dashboardDocumentService;
    private final IntegrationService integrationService;
    private final RiAuthenticationUtils riAuthenticationUtils;
    private final SendRestServiceAuthUtils sendRestServiceAuthUtils;
    private final BeginningRessetlementFlowsService beginningRessetlementFlowsService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final CipService cipService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final EgrnBuildingRequestService egrnBuildingRequestService;
    private final ResettlementInfoMfrFlowService resettlementInfoMfrFlowService;

    /**
     * Обогащает дома из SoonResettlementRequest данными.
     *
     * @param id    идентификатор SoonResettlementRequest
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    public void soonResettle(String id, String login) {
        LOG.debug("BpmHandlerController: updateRealEstateFromEzd запущен");

        SoonResettlementRequestDocument soonResettlementRequestDocument
            = soonResettlementRequestDocumentService.fetchDocument(id);

        SoonResettlementRequestType main = soonResettlementRequestDocument.getDocument().getMain();
        if (main == null) {
            return;
        }

        String type = main.getType();
        if (type == null || type.isEmpty()) {
            return;
        }

        String userName = userService.getUserFioByLogin(login);

        switch (type) {
            case "full":
                LOG.info("BpmHandlerController: Запущен процесс полного обогащения данными");
                integrationService.updateRealEstatesFromEzd(main.getRealEstates(), userName);
                break;
            case "elk":
                LOG.info(
                    "BpmHandlerController: Запущен процесс обогащения данными из личного кабинета жителя на mos.ru"
                );
                integrationService.updatePersonsSsoIdByRealEstateIds(main.getRealEstates(), userName);
                break;
            case "flats":
                LOG.info("BpmHandlerController: Запущен процесс обогащения данными из ЕЖД поквартирно");
                integrationService.updateFlatsFromEzd(main.getFlats(), userName);
                break;
            default:
                break;
        }

        LOG.debug("BpmHandlerController: updateRealEstateFromEzd завершен");
    }

    /**
     * Переселяет дома/квартиры из ResettlementRequest.
     *
     * @param id идентификатор ResettlementRequest
     */
    public void resettle(String id) {
        LOG.info("BpmHandlerController: updateRealEstateFromEtp запущен");

        LOG.info("BpmHandlerController: запускаем 3 поток");
        SuperServiceDGPStartRemovalType superServiceDgpStartRemoval = new SuperServiceDGPStartRemovalType();
        SuperServiceDGPStartRemovalType.Letter letter = new SuperServiceDGPStartRemovalType.Letter();
        superServiceDgpStartRemoval.setLetter(letter);
        SuperServiceDGPStartRemovalType.SettlementHouses settlementHouses =
            new SuperServiceDGPStartRemovalType.SettlementHouses();
        superServiceDgpStartRemoval.setSettlementHouses(settlementHouses);

        ResettlementRequestDocument resettlementRequestDocument = resettlementRequestDocumentService.fetchDocument(id);

        egrnBuildingRequestService.requestByResettlementRequest(resettlementRequestDocument);

        ResettlementRequestType main = resettlementRequestDocument.getDocument().getMain();
        if (main == null) {
            LOG.error("BpmHandlerController: main отсутствует");
            return;
        }

        superServiceDgpStartRemoval.setStartDate(main.getStartResettlementDate());

        letter.setLetterDate(main.getSendEmailDate());
        letter.setLetterNum(main.getEmailNumberDgp());

        List<SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse> settlementHouse
            = settlementHouses.getSettlementHouse();
        for (HouseToSettle houseToSettle : main.getHousesToSettle()) {
            SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse newSettlementHouse
                = new SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse();
            settlementHouse.add(newSettlementHouse);

            newSettlementHouse.setNewUnom(houseToSettle.getCapitalConstructionObjectUnom());

            String informationCenterCode = houseToSettle.getInformationCenterCode();
            if (StringUtils.isNotBlank(informationCenterCode)) {
                SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.Cip newCip
                    = new SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.Cip();
                newSettlementHouse.setCip(newCip);

                CipDocument cipDocument = cipService.fetchDocument(informationCenterCode);
                CipType cipData = cipDocument.getDocument().getCipData();

                newCip.setIdCIP(cipDocument.getId());
                newCip.setAddressCIP(cipData.getAddress());
                newCip.setPhoneCIP(cipData.getPhone());
                newCip.setWorkCIP(cipData.getWorkTime());
                newCip.setDateCIP(cipData.getCipDateStart());
            }

            SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.ResettlementHouses resettlementHouses
                = new SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.ResettlementHouses();
            newSettlementHouse.setResettlementHouses(resettlementHouses);

            List<SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.ResettlementHouses
                .ResettlementHouse> resettlementHouse = resettlementHouses.getResettlementHouse();
            for (HouseToResettle houseToResettle : houseToSettle.getHousesToResettle()) {
                RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocument(
                    houseToResettle.getRealEstateId()
                );
                RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
                realEstateData.setResettlementBy(houseToResettle.getResettlementBy());
                if (houseToResettle.getResettlementBy().equals("full")) {
                    if (realEstateData.getFlats() != null) {
                        for (FlatType flatType : realEstateData.getFlats().getFlat()) {
                            flatType.setCcoId(houseToSettle.getCapitalConstructionObjectId());
                        }
                    }
                } else {
                    if (realEstateData.getFlats() != null) {
                        List<FlatType> flats = realEstateData.getFlats().getFlat()
                            .stream()
                            .filter(f -> houseToResettle.getFlats().contains(f.getFlatID()))
                            .collect(Collectors.toList());
                        for (FlatType flatType : flats) {
                            flatType.setCcoId(houseToSettle.getCapitalConstructionObjectId());
                        }
                    }
                }

                realEstateDocumentService.fillResettlementStatus(realEstateDocument);

                realEstateDocumentService.updateDocument(
                    realEstateDocument.getId(),
                    realEstateDocument,
                    true,
                    false,
                    "updated link to settlementHouse (ccoId)"
                );

                SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.ResettlementHouses
                    .ResettlementHouse newResettlementHouse
                    = new SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse.ResettlementHouses
                    .ResettlementHouse();
                resettlementHouse.add(newResettlementHouse);

                newResettlementHouse.setResType(houseToResettle.getResettlementBy().equals("full") ? "1" : "2");
                newResettlementHouse.setSnosUnom(houseToResettle.getRealEstateUnom());

                if (newResettlementHouse.getResType().equals("2")) {
                    SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse
                        .ResettlementHouses.ResettlementHouse.Flats flats
                        = new SuperServiceDGPStartRemovalType.SettlementHouses
                        .SettlementHouse.ResettlementHouses.ResettlementHouse.Flats();
                    newResettlementHouse.setFlats(flats);

                    List<String> flatNumber = flats.getFlatNumber();
                    flatNumber.addAll(houseToResettle.getFlatNumbers());
                }
            }
        }

        beginningRessetlementFlowsService.sendInfoBeginningResettlement(superServiceDgpStartRemoval);
        LOG.info("BpmHandlerController: 3 поток запущен");
        resettlementInfoMfrFlowService.sendResettlementInfo(main);
        LOG.info("BpmHandlerController: updateRealEstateFromEtp завершен");
    }

    /**
     * Отправялет уведомления жителям.
     *
     * @param id objectResettlementId
     */
    @Async
    public void sendNotificationsAfterResettlement(String id) {
        LOG.info("BpmHandlerController: sendNotificationsAfterResettlement запущен");
        ResettlementRequestDocument resettlementRequestDocument
            = resettlementRequestDocumentService.fetchDocument(id);

        ResettlementRequestType main = resettlementRequestDocument.getDocument().getMain();
        if (main == null) {
            LOG.warn("BpmHandlerController: main отсутствует");
            return;
        }

        for (HouseToSettle houseToSettle : main.getHousesToSettle()) {
            String cipId = houseToSettle.getInformationCenterCode();

            for (HouseToResettle houseToResettle : houseToSettle.getHousesToResettle()) {
                final RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocument(
                    houseToResettle.getRealEstateId()
                );
                final RealEstateDataType realEstate = realEstateDocument.getDocument().getRealEstateData();
                if (realEstate.isNotSendResettlementNotification()) {
                    LOG.info("BpmHandlerController: не отправляем уведомление жителю по"
                        + " причине отношения дома к историчным, realEstateId = {}", realEstateDocument.getId());
                    continue;
                }
                String resettlementBy = houseToResettle.getResettlementBy();
                if (resettlementBy == null) {
                    LOG.warn("BpmHandlerController: resettlementBy отсутствует");
                    continue;
                }
                if (resettlementBy.equals("full")) {
                    LOG.info("BpmHandlerController: расселение по домам");
                    integrationService.sendRequestToElkByRealEstateId(houseToResettle.getRealEstateId(), cipId);
                } else if (resettlementBy.equals("part")) {
                    LOG.info("BpmHandlerController: расселение по квартирам");
                    for (String flatId : houseToResettle.getFlats()) {
                        integrationService.sendRequestToElkByFlatId(flatId, cipId);
                    }
                } else {
                    LOG.warn("BpmHandlerController: неверно указан resettlementBy");
                }
            }
        }
        LOG.info("BpmHandlerController: sendNotificationsAfterResettlement завершен");
    }

    /**
     * Сохраняет информацию из запроса ДГИ в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    public void saveDashboardDgi(String id, String login) {
        if ("null".equals(login)) {
            return;
        }
        RiAuthenticationToken auth = riAuthenticationUtils.createAuthentication(this.sendRestServiceAuthUtils, login);
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(auth);

        LOG.info("saveDashboardDgi запущен");
        DashboardRequestDocument dashboardRequestDocument
            = dashboardRequestDocumentService.fetchDocument(id);

        DashboardRequestType main = dashboardRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        DgiType dgi = main.getDgi();
        if (isNull(dgi)) {
            LOG.warn("dgi отсутствует");
            return;
        }

        String dashboardId = main.getDashboardId();
        if (isNull(dashboardId) || dashboardId.isEmpty()) {
            LOG.warn("dashboardId не заполнен");
            return;
        }

        DashboardDocument dashboardDocument = dashboardDocumentService.fetchDocument(dashboardId);

        DashboardData data = dashboardDocument.getDocument().getData();
        if (isNull(data)) {
            LOG.warn("data у dashboardDocument отсутствует");
            return;
        }

        NewBuildingsFlats newBuildingsFlatsInfo = data.getNewBuildingsFlatsInfo();
        newBuildingsFlatsInfo.setFlatsTotalAmount(
            dgi.isUseFlatsTotalAmountManualValue()
                ? dgi.getFlatsTotalAmount() : dgi.getFlatsTotalAmountAuto()
        );
        newBuildingsFlatsInfo.setEquivalentApartmentsAmount(
            dgi.isUseEquivalentApartmentsAmountManualValue()
                ? dgi.getEquivalentApartmentsAmount() : dgi.getEquivalentApartmentsAmountAuto()
        );
        newBuildingsFlatsInfo.setOfferedEquivalentApartmentsAmount(
            dgi.isUseOfferedEquivalentApartmentsAmountManualValue()
                ? dgi.getOfferedEquivalentApartmentsAmount() : dgi.getOfferedEquivalentApartmentsAmountAuto()
        );

        BuildingsInResettlementProcess buildingsInResettlementProcessInfo
            = data.getBuildingsInResettlementProcessInfo();
        buildingsInResettlementProcessInfo.setResidentsResettledAmount(dgi.getResidentsResettledAmount());
        buildingsInResettlementProcessInfo.setPaperworkCompletedAndFundIssuesAmount(
            dgi.isUsePaperworkCompletedAndFundIssuesAmountManualValue()
                ? dgi.getPaperworkCompletedAndFundIssuesAmount() : dgi.getPaperworkCompletedAndFundIssuesAmountAuto()
        );
        buildingsInResettlementProcessInfo.setResettlementInProcessAmount(
            dgi.isUseResettlementInProcessAmountManualValue()
                ? dgi.getResettlementInProcessAmount() : dgi.getResettlementInProcessAmountAuto()
        );
        buildingsInResettlementProcessInfo.setPlannedResettlementCompletionTime(
            dgi.isUsePlannedResettlementCompletionTimeManualValue()
                ? dgi.getPlannedResettlementCompletionTime() : dgi.getPlannedResettlementCompletionTimeAuto()
        );
        buildingsInResettlementProcessInfo.setOutsideResidenceResettlementAmount(
            dgi.getOutsideResidenceResettlementAmount()
        );

        ResettlementBuildingsResidents resettlementBuildingsResidentsInfo
            = data.getResettlementBuildingsResidentsInfo();
        resettlementBuildingsResidentsInfo.setEquivalentApartmentConsentedAmount(
            dgi.isUseEquivalentApartmentConsentedAmountManualValue()
                ? dgi.getEquivalentApartmentConsentedAmount() : dgi.getEquivalentApartmentConsentedAmountAuto()
        );
        resettlementBuildingsResidentsInfo.setEquivalentApartmentProposedAmount(
            dgi.isUseEquivalentApartmentProposedAmountManualValue()
                ? dgi.getEquivalentApartmentProposedAmount() : dgi.getEquivalentApartmentProposedAmountAuto()
        );
        resettlementBuildingsResidentsInfo.setRefusedAmount(dgi.getRefusedAmount());
        resettlementBuildingsResidentsInfo.setFamiliesAmount(
            dgi.isUseFamiliesAmountManualValue()
                ? dgi.getFamiliesAmount() : dgi.getFamiliesAmountAuto()
        );

        dashboardDocumentService.updateDocument(
            dashboardDocument.getId(), dashboardDocument, true, true, "bpm dgi update"
        );
        LOG.info("saveDashboardDgi завершен");
    }

    /**
     * Сохраняет информацию из запроса ДГП в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    public void saveDashboardDgp(String id, String login) {
        if ("null".equals(login)) {
            return;
        }
        RiAuthenticationToken auth = riAuthenticationUtils.createAuthentication(this.sendRestServiceAuthUtils, login);
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(auth);

        LOG.info("saveDashboardDgp запущен");
        DashboardRequestDocument dashboardRequestDocument
            = dashboardRequestDocumentService.fetchDocument(id);

        DashboardRequestType main = dashboardRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        DgpType dgp = main.getDgp();
        if (isNull(dgp)) {
            LOG.warn("dgp отсутствует");
            return;
        }

        String dashboardId = main.getDashboardId();
        if (isNull(dashboardId) || dashboardId.isEmpty()) {
            LOG.warn("dashboardId не заполнен");
            return;
        }

        DashboardDocument dashboardDocument = dashboardDocumentService.fetchDocument(dashboardId);

        DashboardData data = dashboardDocument.getDocument().getData();
        if (isNull(data)) {
            LOG.warn("data у dashboardDocument отсутствует");
            return;
        }

        data.setNewBuildingsTotalAmount(
            dgp.isUseNewBuildingsTotalAmountManualValue()
                ? dgp.getNewBuildingsTotalAmount() : dgp.getNewBuildingsTotalAmountAuto()
        );

        ResettlementBuildings resettlementBuildingsInfo = data.getResettlementBuildingsInfo();
        resettlementBuildingsInfo.setAllBuildingsAmount(
            dgp.isUseAllBuildingsAmountManualValue()
                ? dgp.getAllBuildingsAmount() : dgp.getAllBuildingsAmountAuto()
        );

        BuildingsInResettlementProcess buildingsInResettlementProcessInfo
            = data.getBuildingsInResettlementProcessInfo();
        buildingsInResettlementProcessInfo.setPartResettlementInProcessAmount(
            dgp.getPartResettlementInProcessAmount()
        );

        ResettlementBuildingsResidents resettlementBuildingsResidentsInfo
            = data.getResettlementBuildingsResidentsInfo();
        resettlementBuildingsResidentsInfo.setResettledFamiliesAmount(
            dgp.isUseResettledFamiliesAmountManualValue()
                ? dgp.getResettledFamiliesAmount() : dgp.getResettledFamiliesAmountAuto()
        );
        resettlementBuildingsResidentsInfo.setResettledResidentsAmount(
            dgp.isUseResettledResidentsAmountManualValue()
                ? dgp.getResettledResidentsAmount() : dgp.getResettledResidentsAmountAuto()
        );

        Tasks tasksInfo = data.getTasksInfo();
        tasksInfo.setYear(dgp.getYear());
        tasksInfo.setPlannedResettledBuildingsAmount(dgp.getPlannedResettledBuildingsAmount());
        tasksInfo.setActuallyResettledBuildingsRate(dgp.getActuallyResettledBuildingsRate());
        tasksInfo.setPlannedResettledResidentsAmount(dgp.getPlannedResettledResidentsAmount());
        tasksInfo.setActuallyResettledResidenceRate(dgp.getActuallyResettledResidenceRate());

        dashboardDocumentService.updateDocument(
            dashboardDocument.getId(), dashboardDocument, true, true, "bpm dgp update"
        );
        LOG.info("saveDashboardDgp завершен");
    }

    /**
     * Сохраняет информацию из запроса Фонда в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    public void saveDashboardFond(String id, String login) {
        if ("null".equals(login)) {
            return;
        }
        RiAuthenticationToken auth = riAuthenticationUtils.createAuthentication(this.sendRestServiceAuthUtils, login);
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(auth);

        LOG.info("saveDashboardFond запущен");
        DashboardRequestDocument dashboardRequestDocument
            = dashboardRequestDocumentService.fetchDocument(id);

        DashboardRequestType main = dashboardRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        FondType fond = main.getFond();
        if (isNull(fond)) {
            LOG.warn("fond отсутствует");
            return;
        }

        String dashboardId = main.getDashboardId();
        if (isNull(dashboardId) || dashboardId.isEmpty()) {
            LOG.warn("dashboardId не заполнен");
            return;
        }

        DashboardDocument dashboardDocument = dashboardDocumentService.fetchDocument(dashboardId);

        DashboardData data = dashboardDocument.getDocument().getData();
        if (isNull(data)) {
            LOG.warn("data у dashboardDocument отсутствует");
            return;
        }

        NewBuildingsFlats newBuildingsFlatsInfo = data.getNewBuildingsFlatsInfo();
        newBuildingsFlatsInfo.setBuyInApartmentsAmount(
            fond.isUseBuyInApartmentsAmountManualValue()
                ? fond.getBuyInApartmentsAmount() : fond.getBuyInApartmentsAmountAuto()
        );
        newBuildingsFlatsInfo.setApartmentsWithCompensationAmount(
            fond.isUseApartmentsWithCompensationAmountManualValue()
                ? fond.getApartmentsWithCompensationAmount() : fond.getApartmentsWithCompensationAmountAuto()
        );
        newBuildingsFlatsInfo.setOfferedApartmentsWithCompensationAmount(
            fond.getOfferedApartmentsWithCompensationAmount()
        );

        ResettlementBuildingsResidents resettlementBuildingsResidentsInfo
            = data.getResettlementBuildingsResidentsInfo();
        resettlementBuildingsResidentsInfo.setApartmentWithCompensationConsentedAmount(
            fond.isUseApartmentWithCompensationConsentedAmountManualValue()
                ? fond.getApartmentWithCompensationConsentedAmount()
                : fond.getApartmentWithCompensationConsentedAmountAuto()
        );
        resettlementBuildingsResidentsInfo.setApartmentWithCompensationProposedAmount(
            fond.getApartmentWithCompensationProposedAmount()
        );

        dashboardDocumentService.updateDocument(
            dashboardDocument.getId(), dashboardDocument, true, true, "bpm fond update"
        );
        LOG.info("saveDashboardFond завершен");
    }

    /**
     * Сохраняет информацию из запроса ДС в сам дашборд.
     *
     * @param id    идентификатор DashboardRequestDocument
     * @param login логин пользователя, от имени которого вносятся изменения
     */
    public void saveDashboardDs(String id, String login) {
        if ("null".equals(login)) {
            return;
        }
        RiAuthenticationToken auth = riAuthenticationUtils.createAuthentication(this.sendRestServiceAuthUtils, login);
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(auth);

        LOG.info("saveDashboardDs запущен");
        DashboardRequestDocument dashboardRequestDocument
            = dashboardRequestDocumentService.fetchDocument(id);

        DashboardRequestType main = dashboardRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        DsType ds = main.getDs();
        if (isNull(ds)) {
            LOG.warn("ds отсутствует");
            return;
        }

        String dashboardId = main.getDashboardId();
        if (isNull(dashboardId) || dashboardId.isEmpty()) {
            LOG.warn("dashboardId не заполнен");
            return;
        }

        DashboardDocument dashboardDocument = dashboardDocumentService.fetchDocument(dashboardId);

        DashboardData data = dashboardDocument.getDocument().getData();
        if (isNull(data)) {
            LOG.warn("data у dashboardDocument отсутствует");
            return;
        }

        ResettlementBuildings resettlementBuildingsInfo = data.getResettlementBuildingsInfo();
        resettlementBuildingsInfo.setDemolishedAmount(ds.getDemolishedAmount());
        resettlementBuildingsInfo.setPreparingForDemolitionAmount(ds.getPreparingForDemolitionAmount());

        dashboardDocumentService.updateDocument(
            dashboardDocument.getId(), dashboardDocument, true, true, "bpm ds update"
        );
        LOG.info("saveDashboardDs завершен");
    }

    /**
     * Публикует запись дашборда.
     *
     * @param id идентификатор DashboardRequestDocument
     */
    public void publishDashboard(String id) {
        LOG.info("publishDashboard запущен");
        DashboardRequestDocument dashboardRequestDocument
            = dashboardRequestDocumentService.fetchDocument(id);

        DashboardRequestType main = dashboardRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        String dashboardId = main.getDashboardId();
        if (isNull(dashboardId) || dashboardId.isEmpty()) {
            LOG.warn("dashboardId не заполнен");
            return;
        }

        DashboardDocument dashboardDocument = dashboardDocumentService.fetchDocument(dashboardId);
        dashboardDocument.getDocument().setPublished(true);
        dashboardDocumentService.updateDocument(
            dashboardDocument.getId(), dashboardDocument, true, true, "bpm publish dashboard"
        );
        LOG.info("publishDashboard завершен");
    }

    /**
     * Сохраняет в истории выдачу письма с предложением.
     *
     * @param id       идентификатор ArmIssueOfferLetterRequest
     * @param assignee assignee
     */
    public void saveArmIssueOfferRequest(String id, String assignee) {
        LOG.info("saveArmIssueOfferRequest запущен");
        ArmIssueOfferLetterRequestDocument armIssueOfferLetterRequestDocument
            = armIssueOfferLetterRequestDocumentService.fetchDocument(id);

        ArmIssueOfferLetterRequestType main = armIssueOfferLetterRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        String personId = main.getPersonId();
        if (isNull(personId) || personId.isEmpty()) {
            LOG.warn("personId не заполнен");
            return;
        }

        ResettlementHistory tillResettlementHistory = new ResettlementHistory();

        String event = main.getEvent();
        if (main.getDate() != null) {
            tillResettlementHistory.setDate(main.getDate().toLocalDate());
        }
        if (event.equals("Письмо с предложением не выдано")) {
            tillResettlementHistory.setAnnotation(main.getRefusalReason());
        }
        tillResettlementHistory.setStatus(event);
        tillResettlementHistory.setUserFio(userService.getUserFioByLogin(assignee));

        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        PersonType personData = personDocument.getDocument().getPersonData();

        List<ResettlementHistory> resettlementHistory = personData.getResettlementHistory();
        resettlementHistory.add(tillResettlementHistory);

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "bpm save issueOfferLetter"
        );
        LOG.info("saveArmIssueOfferRequest завершен");
    }

    /**
     * Сохраняет в истории показ квартиры.
     *
     * @param id       идентификатор armShowApartRequest
     * @param assignee assignee
     */
    public void saveArmShowApartRequest(String id, String assignee) {
        LOG.info("saveArmShowApartRequest запущен");
        ArmShowApartRequestDocument armShowApartRequestDocument
            = armShowApartRequestDocumentService.fetchDocument(id);

        ArmShowApartRequestType main = armShowApartRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        String personId = main.getPersonId();
        if (isNull(personId) || personId.isEmpty()) {
            LOG.warn("personId не заполнен");
            return;
        }


        ResettlementHistory tillResettlementHistory = new ResettlementHistory();

        String event = main.getEvent();
        if (main.getDate() != null) {
            tillResettlementHistory.setDate(main.getDate().toLocalDate());
        }
        if (event.equals("Показ квартиры не выполнен")) {
            tillResettlementHistory.setAnnotation(main.getRefusalReason());
        }
        tillResettlementHistory.setStatus(event);
        String annotationShowApart = main.getRemarks().size() > 0 ? "Выявлены замечания, " : "";
        String annotation = main.getAnnotation();
        if (annotation != null && !annotation.isEmpty()) {
            if (annotationShowApart.length() > 0) {
                annotation = annotation.substring(0, 1).toLowerCase() + annotation.substring(1);
            } else {
                annotation = annotation.substring(0, 1).toUpperCase() + annotation.substring(1);
            }
            annotationShowApart += annotation;
        }
        tillResettlementHistory.setAnnotationShowApart(annotationShowApart);
        tillResettlementHistory.setUserFio(userService.getUserFioByLogin(assignee));

        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        PersonType personData = personDocument.getDocument().getPersonData();

        List<ResettlementHistory> resettlementHistory = personData.getResettlementHistory();
        resettlementHistory.add(tillResettlementHistory);

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "bpm save showApart"
        );
        LOG.info("saveArmShowApartRequest завершен");
    }

    /**
     * Сохраняет в истории принятие/отказ.
     *
     * @param id       идентификатор armApplyRequest
     * @param assignee assignee
     */
    public void saveArmApplyRequest(String id, String assignee) {
        LOG.info("saveArmApplyRequest запущен");
        ArmApplyRequestDocument armApplyRequestDocument
            = armApplyRequestDocumentService.fetchDocument(id);

        ArmApplyRequestType main = armApplyRequestDocument.getDocument().getMain();
        if (isNull(main)) {
            LOG.warn("main отсутствует");
            return;
        }

        String personId = main.getPersonId();
        if (isNull(personId) || personId.isEmpty()) {
            LOG.warn("personId не заполнен");
            return;
        }


        ResettlementHistory tillResettlementHistory = new ResettlementHistory();

        String event = main.getEvent();
        if (main.getDate() != null) {
            tillResettlementHistory.setDate(main.getDate().toLocalDate());
        }
        if (event.equals("Заявление не принято")) {
            tillResettlementHistory.setAnnotation(main.getRefusalReason());
        }
        tillResettlementHistory.setStatus(event);
        tillResettlementHistory.setUserFio(userService.getUserFioByLogin(assignee));

        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        PersonType personData = personDocument.getDocument().getPersonData();

        List<ResettlementHistory> resettlementHistory = personData.getResettlementHistory();
        resettlementHistory.add(tillResettlementHistory);


        if (event.equals("Принято заявление о согласии") || event.equals("Принято заявление об отказе")) {
            List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(personData.getFlatID());
            List<PersonDocument> familyPersons = personDocuments.stream()
                .filter(person -> person.getDocument().getPersonData().getAffairId() != null)
                .filter(person -> person.getDocument().getPersonData().getAffairId().equals(personData.getAffairId()))
                .collect(Collectors.toList());
            for (PersonDocument familyPerson : familyPersons) {
                PersonType personMain = familyPerson.getDocument().getPersonData();
                if (event.equals("Принято заявление о согласии")) {
                    personMain.setRelocationStatus("Получено согласие");
                } else {
                    personMain.setRelocationStatus("Получен отказ");
                }
                personDocumentService.updateDocument(
                    familyPerson.getId(), familyPerson, true, true, "bpm arm update status"
                );
            }
            if (event.equals("Принято заявление о согласии")) {
                personData.setRelocationStatus("Получено согласие");
            } else {
                personData.setRelocationStatus("Получен отказ");
            }
            personDocumentService.updateDocument(personId, personDocument, true, true, "bpm arm update status");
        }

        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "bpm save apply");
        LOG.info("saveArmApplyRequest завершен");
    }

}
