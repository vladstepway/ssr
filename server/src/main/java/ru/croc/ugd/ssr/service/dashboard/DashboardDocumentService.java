package ru.croc.ugd.ssr.service.dashboard;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.BuildingsInResettlementProcess;
import ru.croc.ugd.ssr.Dashboard;
import ru.croc.ugd.ssr.DashboardData;
import ru.croc.ugd.ssr.NewBuildingsFlats;
import ru.croc.ugd.ssr.ResettlementBuildings;
import ru.croc.ugd.ssr.ResettlementBuildingsResidents;
import ru.croc.ugd.ssr.db.dao.DashboardDocumentDao;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.math.BigInteger;
import java.time.LocalDate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Сервис по работе с дашбордом.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DashboardDocumentService extends AbstractDocumentService<DashboardDocument> {

    private final DashboardAnalyticsService analyticsService;
    private final DashboardDocumentDao dashboardDocumentDao;
    private final JsonMapper jsonMapper;

    private DashboardDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    /**
     * Парсинг из json в DashboardDocument.
     *
     * @param json json
     * @return DashboardDocument
     */
    public DashboardDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, DashboardDocument.class);
    }

    @Nonnull
    @Override
    public DocumentType<DashboardDocument> getDocumentType() {
        return SsrDocumentTypes.DASHBOARD;
    }

    /**
     * Получить последнюю активную запись по дашборду.
     *
     * @param auto признак автоматически сформированной записи
     * @return дашборд документ
     */
    public DashboardDocument getLastActiveDocument(boolean auto) {
        if (auto) {
            return this.dashboardDocumentDao.getLastActiveAutoDocument()
                .map(this::parseDocumentData)
                .orElse(null);
        } else {
            return getLastActiveDashboard();
        }
    }

    /**
     * Сформировать автоматическую запись по дашборду.
     */
    public String createAutoDashboardDocument() {
        DashboardDocument document = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        document.setDocument(dashboard);
        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);
        dashboard.setData(getLastActiveDashboard().getDocument().getData());
        DashboardData data = dashboard.getData();
        data.setInformationDate(LocalDate.now());
        data.setNewBuildingsTotalAmount(BigInteger.valueOf(analyticsService.getNewBuildingsTotalAmount()));

        NewBuildingsFlats newBuildingsFlats = data.getNewBuildingsFlatsInfo();
        newBuildingsFlats.setFlatsTotalAmount(BigInteger.valueOf(analyticsService.getFlatsTotalAmount()));
        newBuildingsFlats.setEquivalentApartmentsAmount(
            BigInteger.valueOf(analyticsService.getEquivalentApartmentsAmount())
        );
        newBuildingsFlats.setOfferedEquivalentApartmentsAmount(
            BigInteger.valueOf(analyticsService.getOfferedEquivalentApartmentsAmount())
        );
        newBuildingsFlats.setBuyInApartmentsAmount(
            BigInteger.valueOf(analyticsService.getBuyInApartmentsAmount())
        );
        newBuildingsFlats.setApartmentsWithCompensationAmount(
            BigInteger.valueOf(analyticsService.getApartmentsWithCompensationAmount())
        );
        newBuildingsFlats.setOfferedApartmentsWithCompensationAmount(
            BigInteger.valueOf(analyticsService.getOfferedApartmentsWithCompensationAmount())
        );

        ResettlementBuildings resettlementBuildings = data.getResettlementBuildingsInfo();
        resettlementBuildings.setAllBuildingsAmount(
            BigInteger.valueOf(analyticsService.getAllBuildingsAmount())
        );
        resettlementBuildings.setPreparingForDemolitionAmount(
            BigInteger.valueOf(analyticsService.getPreparingForDemolitionAmount())
        );
        resettlementBuildings.setDemolishedAmount(
            BigInteger.valueOf(analyticsService.getDemolishedAmount())
        );
        resettlementBuildings.setPreservedAmount(
            BigInteger.valueOf(analyticsService.getPreservedAmount())
        );
        resettlementBuildings.setPreservedAmount(BigInteger.valueOf(analyticsService.getPreservedAmount()));

        BuildingsInResettlementProcess buildingsInResettlementProcess = data.getBuildingsInResettlementProcessInfo();
        buildingsInResettlementProcess.setPaperworkCompletedAndFundIssuesAmount(
            BigInteger.valueOf(analyticsService.getPaperworkCompletedAndFundIssuesAmount())
        );
        buildingsInResettlementProcess.setResettlementInProcessAmount(
            BigInteger.valueOf(analyticsService.getResettlementInProcessAmount())
        );
        buildingsInResettlementProcess.setPlannedResettlementCompletionTime(
            analyticsService.getPlannedResettlementCompletionTime()
        );
        buildingsInResettlementProcess.setResidentsResettledAmount(
            BigInteger.valueOf(analyticsService.getResidentsResettledAmount())
        );
        buildingsInResettlementProcess.setPartResettlementInProcessAmount(
            BigInteger.valueOf(analyticsService.getPartResettlementInProcessAmount())
        );
        buildingsInResettlementProcess.setOutsideResidenceResettlementAmount(
            BigInteger.valueOf(analyticsService.getOutsideResidenceResettlementAmount())
        );

        ResettlementBuildingsResidents resettlementBuildingsResidents = data.getResettlementBuildingsResidentsInfo();
        resettlementBuildingsResidents.setEquivalentApartmentConsentedAmount(
            BigInteger.valueOf(analyticsService.getEquivalentApartmentConsentedAmount())
        );
        resettlementBuildingsResidents.setEquivalentApartmentProposedAmount(
            BigInteger.valueOf(analyticsService.getEquivalentApartmentProposedAmount())
        );
        resettlementBuildingsResidents.setFamiliesAmount(
            BigInteger.valueOf(analyticsService.getFamiliesAmount())
        );
        resettlementBuildingsResidents.setResettledFamiliesAmount(
            BigInteger.valueOf(analyticsService.getResettledFamiliesAmount())
        );
        resettlementBuildingsResidents.setResettledResidentsAmount(
            BigInteger.valueOf(analyticsService.getResettledResidentsAmount())
        );
        resettlementBuildingsResidents.setApartmentWithCompensationConsentedAmount(
            BigInteger.valueOf(analyticsService.getApartmentWithCompensationAmount())
        );
        resettlementBuildingsResidents.setApartmentWithCompensationProposedAmount(
            BigInteger.valueOf(analyticsService.getApartmentWithCompensationProposedAmount())
        );
        resettlementBuildingsResidents.setRefusedAmount(BigInteger.valueOf(analyticsService.getRefusedAmount()));

        DashboardDocument auto = this.createDocument(document, true, "auto");
        return auto.getId();
    }

    /**
     * Сформировать автоматическую запись по дашборду c показателями по заселяемым домам.
     */
    public DashboardDocument createSettlementObjectsIndicatorsGroupDashboardDocument() {
        DashboardDocument lastAutoDashboard = getLastActiveDocument(true);

        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);

        dashboardDocument.assignId(lastAutoDashboard.getId());
        dashboardDocument.assignFolderId(lastAutoDashboard.getFolderId());
        dashboard.setDocumentID(lastAutoDashboard.getId());
        dashboard.setFolderGUID(lastAutoDashboard.getFolderId());

        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);

        DashboardData data = new DashboardData();
        dashboard.setData(data);
        data.setInformationDate(LocalDate.now());

        DashboardData lastData = lastAutoDashboard.getDocument().getData();
        data.setNewBuildingsTotalAmount(lastData.getNewBuildingsTotalAmount());

        NewBuildingsFlats newBuildingsFlats = new NewBuildingsFlats();
        data.setNewBuildingsFlatsInfo(newBuildingsFlats);
        newBuildingsFlats.setFlatsTotalAmount(lastData.getNewBuildingsFlatsInfo().getFlatsTotalAmount());
        newBuildingsFlats.setEquivalentApartmentsAmount(
            lastData.getNewBuildingsFlatsInfo().getEquivalentApartmentsAmount()
        );
        newBuildingsFlats.setBuyInApartmentsAmount(lastData.getNewBuildingsFlatsInfo().getBuyInApartmentsAmount());
        newBuildingsFlats.setApartmentsWithCompensationAmount(
            lastData.getNewBuildingsFlatsInfo().getApartmentsWithCompensationAmount()
        );
        newBuildingsFlats.setOfferedEquivalentApartmentsAmount(
            lastData.getNewBuildingsFlatsInfo().getOfferedEquivalentApartmentsAmount()
        );

        return dashboardDocument;
    }

    /**
     * Сформировать автоматическую запись по дашборду c показателями по отселяемым домам.
     */
    public DashboardDocument createResettlementObjectsIndicatorsGroupDashboardDocument() {
        DashboardDocument lastAutoDashboard = getLastActiveDocument(true);

        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);

        dashboardDocument.assignId(lastAutoDashboard.getId());
        dashboardDocument.assignFolderId(lastAutoDashboard.getFolderId());
        dashboard.setDocumentID(lastAutoDashboard.getId());
        dashboard.setFolderGUID(lastAutoDashboard.getFolderId());

        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);

        DashboardData data = new DashboardData();
        dashboard.setData(data);
        data.setInformationDate(LocalDate.now());

        DashboardData lastData = lastAutoDashboard.getDocument().getData();
        ResettlementBuildings resettlementBuildings = new ResettlementBuildings();
        data.setResettlementBuildingsInfo(resettlementBuildings);
        resettlementBuildings.setAllBuildingsAmount(lastData.getResettlementBuildingsInfo().getAllBuildingsAmount());
        resettlementBuildings.setPreservedAmount(lastData.getResettlementBuildingsInfo().getPreservedAmount());
        resettlementBuildings.setDemolishedAmount(lastData.getResettlementBuildingsInfo().getDemolishedAmount());
        resettlementBuildings.setPreparingForDemolitionAmount(
            lastData.getResettlementBuildingsInfo().getPreparingForDemolitionAmount()
        );

        return dashboardDocument;
    }

    /**
     * Сформировать автоматическую запись по дашборду c показателями по домам в процессе переселения.
     */
    public DashboardDocument createResettlementInProcessObjectsIndicatorsGroupDashboardDocument() {
        DashboardDocument lastAutoDashboard = getLastActiveDocument(true);

        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);

        dashboardDocument.assignId(lastAutoDashboard.getId());
        dashboardDocument.assignFolderId(lastAutoDashboard.getFolderId());
        dashboard.setDocumentID(lastAutoDashboard.getId());
        dashboard.setFolderGUID(lastAutoDashboard.getFolderId());

        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);

        DashboardData data = new DashboardData();
        dashboard.setData(data);
        data.setInformationDate(LocalDate.now());

        DashboardData lastData = lastAutoDashboard.getDocument().getData();
        BuildingsInResettlementProcess buildingsInResettlementProcess = new BuildingsInResettlementProcess();
        data.setBuildingsInResettlementProcessInfo(buildingsInResettlementProcess);
        buildingsInResettlementProcess.setPaperworkCompletedAndFundIssuesAmount(
            lastData.getBuildingsInResettlementProcessInfo().getPaperworkCompletedAndFundIssuesAmount()
        );
        buildingsInResettlementProcess.setResettlementInProcessAmount(
            lastData.getBuildingsInResettlementProcessInfo().getResettlementInProcessAmount()
        );
        buildingsInResettlementProcess.setPlannedResettlementCompletionTime(
            lastData.getBuildingsInResettlementProcessInfo().getPlannedResettlementCompletionTime()
        );
        buildingsInResettlementProcess.setPartResettlementInProcessAmount(
            lastData.getBuildingsInResettlementProcessInfo().getPartResettlementInProcessAmount()
        );
        buildingsInResettlementProcess.setOutsideResidenceResettlementAmount(
            lastData.getBuildingsInResettlementProcessInfo().getPartResettlementInProcessAmount()
        );

        return dashboardDocument;
    }

    /**
     * Сформировать автоматическую запись по дашборду c показателями по переселению.
     */
    public DashboardDocument createResettlementIndicatorsGroupDashboardDocument() {
        DashboardDocument lastAutoDashboard = getLastActiveDocument(true);

        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);

        dashboardDocument.assignId(lastAutoDashboard.getId());
        dashboardDocument.assignFolderId(lastAutoDashboard.getFolderId());
        dashboard.setDocumentID(lastAutoDashboard.getId());
        dashboard.setFolderGUID(lastAutoDashboard.getFolderId());

        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);

        DashboardData data = new DashboardData();
        dashboard.setData(data);
        data.setInformationDate(LocalDate.now());

        DashboardData lastData = lastAutoDashboard.getDocument().getData();

        NewBuildingsFlats newBuildingsFlats = new NewBuildingsFlats();
        data.setNewBuildingsFlatsInfo(newBuildingsFlats);
        newBuildingsFlats.setOfferedApartmentsWithCompensationAmount(
            lastData.getNewBuildingsFlatsInfo().getOfferedApartmentsWithCompensationAmount()
        );

        BuildingsInResettlementProcess buildingsInResettlementProcess = new BuildingsInResettlementProcess();
        data.setBuildingsInResettlementProcessInfo(buildingsInResettlementProcess);
        buildingsInResettlementProcess.setResidentsResettledAmount(
            lastData.getBuildingsInResettlementProcessInfo().getResidentsResettledAmount()
        );

        ResettlementBuildingsResidents resettlementBuildingsResidents = new ResettlementBuildingsResidents();
        data.setResettlementBuildingsResidentsInfo(resettlementBuildingsResidents);
        resettlementBuildingsResidents.setRefusedAmount(
            lastData.getResettlementBuildingsResidentsInfo().getRefusedAmount()
        );
        resettlementBuildingsResidents.setApartmentWithCompensationProposedAmount(
            lastData.getResettlementBuildingsResidentsInfo().getApartmentWithCompensationProposedAmount()
        );

        return dashboardDocument;
    }

    /**
     * Сформировать автоматическую запись по дашборду c 4-й группой показателей.
     */
    public DashboardDocument createPersonsAndFamiliesIndicatorsGroupDashboardDocument() {
        DashboardDocument lastAutoDashboard = getLastActiveDocument(true);

        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);

        dashboardDocument.assignId(lastAutoDashboard.getId());
        dashboardDocument.assignFolderId(lastAutoDashboard.getFolderId());
        dashboard.setDocumentID(lastAutoDashboard.getId());
        dashboard.setFolderGUID(lastAutoDashboard.getFolderId());

        dashboard.setAuto(true);
        dashboard.setArchived(false);
        dashboard.setFillInDate(LocalDate.now());
        dashboard.setPublished(true);

        DashboardData data = new DashboardData();
        dashboard.setData(data);
        data.setInformationDate(LocalDate.now());

        DashboardData lastData = lastAutoDashboard.getDocument().getData();
        ResettlementBuildingsResidents resettlementBuildingsResidents = new ResettlementBuildingsResidents();
        data.setResettlementBuildingsResidentsInfo(resettlementBuildingsResidents);
        resettlementBuildingsResidents.setEquivalentApartmentConsentedAmount(
            lastData.getResettlementBuildingsResidentsInfo().getEquivalentApartmentConsentedAmount()
        );
        resettlementBuildingsResidents.setEquivalentApartmentProposedAmount(
            lastData.getResettlementBuildingsResidentsInfo().getEquivalentApartmentProposedAmount()
        );
        resettlementBuildingsResidents.setFamiliesAmount(
            lastData.getResettlementBuildingsResidentsInfo().getFamiliesAmount()
        );
        resettlementBuildingsResidents.setResettledFamiliesAmount(
            lastData.getResettlementBuildingsResidentsInfo().getResettledFamiliesAmount()
        );
        resettlementBuildingsResidents.setResettledResidentsAmount(
            lastData.getResettlementBuildingsResidentsInfo().getResettledResidentsAmount()
        );
        resettlementBuildingsResidents.setApartmentWithCompensationConsentedAmount(
            lastData.getResettlementBuildingsResidentsInfo().getApartmentWithCompensationConsentedAmount()
        );

        return dashboardDocument;
    }

    @Nonnull
    @Override
    public DashboardDocument createDocument(
        @Nonnull DashboardDocument document,
        boolean flagReindex,
        @Nullable String notes
    ) {
        calculateActuallyResettledBuildingsRate(document);
        calculateActuallyResettledResidenceRate(document);
        return super.createDocument(document, flagReindex, notes);
    }

    @Nonnull
    @Override
    public DashboardDocument updateDocument(
        @Nonnull String id,
        @Nonnull DashboardDocument newDocument,
        boolean skipUnchanged,
        boolean flagReindex,
        @Nullable String notes
    ) {
        calculateActuallyResettledBuildingsRate(newDocument);
        calculateActuallyResettledResidenceRate(newDocument);
        return super.updateDocument(id, newDocument, skipUnchanged, flagReindex, notes);
    }

    /**
     * Получить сегодняшнюю активную автоматическую запись по дашборду.
     *
     * @return дашборд документ
     */
    public DashboardDocument getTodayActiveAutoDocument() {
        return this.dashboardDocumentDao.getActiveAutoDocumentByDate(LocalDate.now())
            .map(this::parseDocumentData)
            .orElse(null);

    }

    /**
     * Получить сегодняшнюю активную запись по дашборду.
     *
     * @return дашборд документ
     */
    public DashboardDocument getTodayActiveDocument() {
        return this.dashboardDocumentDao.getActiveDocumentByDate(LocalDate.now())
            .map(this::parseDocumentData)
            .orElse(null);

    }

    private void calculateActuallyResettledBuildingsRate(DashboardDocument document) {
        DashboardData data = document.getDocument().getData();
        if (
            isNull(data)
                || isNull(data.getResettlementBuildingsInfo())
                || isNull(data.getTasksInfo())
                || isNull(data.getTasksInfo().getPlannedResettledBuildingsAmount())
                || isNull(data.getResettlementBuildingsInfo().getDemolishedAmount())
                || isNull(data.getResettlementBuildingsInfo().getPreparingForDemolitionAmount())
        ) {
            log.warn("Недостаточно данных для подсчета Фактически отселено домов");
            return;
        }

        data.getTasksInfo().setActuallyResettledBuildingsRate(
            (double) Math.round(
                (data.getResettlementBuildingsInfo().getDemolishedAmount().doubleValue()
                    + data.getResettlementBuildingsInfo().getPreparingForDemolitionAmount().doubleValue())
                    / data.getTasksInfo().getPlannedResettledBuildingsAmount().doubleValue() * 100
            )
        );

    }

    private void calculateActuallyResettledResidenceRate(DashboardDocument document) {
        DashboardData data = document.getDocument().getData();
        if (
            isNull(data)
                || isNull(data.getResettlementBuildingsResidentsInfo())
                || isNull(data.getResettlementBuildingsResidentsInfo().getResettledResidentsAmount())
                || isNull(data.getTasksInfo())
                || isNull(data.getTasksInfo().getPlannedResettledBuildingsAmount())
        ) {
            log.warn("Недостаточно данных для подсчета Фактически переселено");
            return;
        }

        data.getTasksInfo().setActuallyResettledResidenceRate(
            (double) Math.round(
                data.getResettlementBuildingsResidentsInfo().getResettledResidentsAmount().doubleValue()
                    / data.getTasksInfo().getPlannedResettledResidentsAmount() * 100
            )
        );
    }

    private DashboardDocument getLastActiveDashboard() {
        return this.dashboardDocumentDao.getLastActiveDocument()
            .map(this::parseDocumentData)
            .orElse(null);
    }

}
