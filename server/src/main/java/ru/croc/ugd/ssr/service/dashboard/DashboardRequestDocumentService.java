package ru.croc.ugd.ssr.service.dashboard;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.BuildingsInResettlementProcess;
import ru.croc.ugd.ssr.Dashboard;
import ru.croc.ugd.ssr.DashboardData;
import ru.croc.ugd.ssr.DashboardRequest;
import ru.croc.ugd.ssr.DashboardRequestType;
import ru.croc.ugd.ssr.DgiType;
import ru.croc.ugd.ssr.DgpType;
import ru.croc.ugd.ssr.DsType;
import ru.croc.ugd.ssr.FondType;
import ru.croc.ugd.ssr.NewBuildingsFlats;
import ru.croc.ugd.ssr.ResettlementBuildings;
import ru.croc.ugd.ssr.ResettlementBuildingsResidents;
import ru.croc.ugd.ssr.Tasks;
import ru.croc.ugd.ssr.db.dao.DashboardRequestDocumentDao;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.service.BpmRuntimeService;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис по работе с сущностью дашборд-request.
 */
@Service
@AllArgsConstructor
public class DashboardRequestDocumentService extends AbstractDocumentService<DashboardRequestDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardRequestDocumentService.class);

    private final DashboardDocumentService dashboardDocumentService;
    private final DashboardRequestDocumentDao dashboardRequestDocumentDao;
    private final JsonMapper jsonMapper;
    private final BpmRuntimeService bpmRuntimeService;
    private final DashboardAnalyticsService dashboardAnalyticsService;

    private DashboardRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    /**
     * Парсинг из json в DashboardRequestDocument.
     *
     * @param json json
     * @return DashboardRequestDocument
     */
    public DashboardRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, DashboardRequestDocument.class);
    }

    @Nonnull
    @Override
    public DocumentType<DashboardRequestDocument> getDocumentType() {
        return SsrDocumentTypes.DASHBOARD_REQUEST;
    }

    /**
     * Получить документ по ид дашборда.
     *
     * @param dashboardId ид дашборда
     * @return список DashboardRequestDocument
     */
    public List<DashboardRequestDocument> fetchByDashboardId(String dashboardId) {
        return dashboardRequestDocumentDao.fetchByDashboardId(dashboardId)
                .stream()
                .map(this::parseDocumentData)
                .collect(Collectors.toList());
    }

    /**
     * Создание документа по БП.
     *
     * @return DashboardRequestDocument
     */
    @Nonnull
    @Transactional
    public DashboardRequestDocument createDocumentWithProcess() {
        DashboardDocument dashboardDocument = new DashboardDocument();
        Dashboard dashboard = new Dashboard();
        dashboardDocument.setDocument(dashboard);
        DashboardData dashboardData = new DashboardData();
        dashboard.setData(dashboardData);

        dashboard.setFillInDate(LocalDate.now());
        dashboard.setArchived(false);
        dashboard.setPublished(false);

        dashboardData.setBuildingsInResettlementProcessInfo(new BuildingsInResettlementProcess());
        dashboardData.setNewBuildingsFlatsInfo(new NewBuildingsFlats());
        dashboardData.setResettlementBuildingsInfo(new ResettlementBuildings());
        dashboardData.setResettlementBuildingsResidentsInfo(new ResettlementBuildingsResidents());
        dashboardData.setTasksInfo(new Tasks());
        dashboardData.setInformationDate(LocalDate.now());

        DashboardRequestDocument document = new DashboardRequestDocument();
        DashboardRequest dashboardRequest = new DashboardRequest();
        document.setDocument(dashboardRequest);
        DashboardRequestType main = new DashboardRequestType();
        dashboardRequest.setMain(main);

        DgiType dgi = new DgiType();
        dgi.setFlatsTotalAmountAuto(BigInteger.valueOf(dashboardAnalyticsService.getFlatsTotalAmount()));
        dgi.setUseFlatsTotalAmountManualValue(false);
        dgi.setEquivalentApartmentsAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getEquivalentApartmentsAmount()));
        dgi.setUseEquivalentApartmentsAmountManualValue(false);
        dgi.setOfferedEquivalentApartmentsAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getOfferedEquivalentApartmentsAmount())
        );
        dgi.setUseOfferedEquivalentApartmentsAmountManualValue(false);
        dgi.setPaperworkCompletedAndFundIssuesAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getPaperworkCompletedAndFundIssuesAmount())
        );
        dgi.setUsePaperworkCompletedAndFundIssuesAmountManualValue(false);
        dgi.setResettlementInProcessAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getResettlementInProcessAmount())
        );
        dgi.setUseResettlementInProcessAmountManualValue(false);
        dgi.setPlannedResettlementCompletionTimeAuto(
            dashboardAnalyticsService.getPlannedResettlementCompletionTime()
        );
        dgi.setUsePlannedResettlementCompletionTimeManualValue(false);
        dgi.setEquivalentApartmentConsentedAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getEquivalentApartmentConsentedAmount())
        );
        dgi.setUseEquivalentApartmentConsentedAmountManualValue(false);
        dgi.setEquivalentApartmentProposedAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getEquivalentApartmentProposedAmount())
        );
        dgi.setUseEquivalentApartmentProposedAmountManualValue(false);
        dgi.setFamiliesAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getFamiliesAmount())
        );
        dgi.setUseFamiliesAmountManualValue(false);
        main.setDgi(dgi);


        DgpType dgp = new DgpType();
        dgp.setNewBuildingsTotalAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getNewBuildingsTotalAmount())
        );
        dgp.setUseNewBuildingsTotalAmountManualValue(false);
        dgp.setAllBuildingsAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getAllBuildingsAmount())
        );
        dgp.setUseAllBuildingsAmountManualValue(false);
        dgp.setResettledFamiliesAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getResettledFamiliesAmount())
        );
        dgp.setUseResettledFamiliesAmountManualValue(false);
        dgp.setResettledResidentsAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getResettledResidentsAmount())
        );
        dgp.setUseResettledResidentsAmountManualValue(false);
        main.setDgp(dgp);


        FondType fond = new FondType();
        fond.setBuyInApartmentsAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getBuyInApartmentsAmount())
        );
        fond.setUseBuyInApartmentsAmountManualValue(false);
        fond.setApartmentsWithCompensationAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getApartmentsWithCompensationAmount())
        );
        fond.setUseApartmentsWithCompensationAmountManualValue(false);
        fond.setApartmentWithCompensationConsentedAmountAuto(
            BigInteger.valueOf(dashboardAnalyticsService.getApartmentWithCompensationAmount())
        );
        fond.setUseApartmentWithCompensationConsentedAmountManualValue(false);
        main.setFond(fond);


        main.setDs(new DsType());

        DashboardDocument savedDashboardDocument = dashboardDocumentService.createDocument(
            dashboardDocument, true, "bpm create dashboard"
        );
        main.setDashboardId(savedDashboardDocument.getId());

        DashboardRequestDocument requestDocument = createDocument(
                document, false, "bpm create dashboard"
        );
        return startDocumentProcess(requestDocument);
    }

    @Nonnull
    private DashboardRequestDocument startDocumentProcess(
            @Nonnull DashboardRequestDocument document
    ) {
        try {
            List<FormProperty> variables = new ArrayList<>();
            variables.add(new FormProperty("EntityIdVar", document.getId()));
            ProcessInstance process = bpmRuntimeService.startProcessViaForm(
                    "ugdssr_dashboardRequest",
                    variables
            );

            return document;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(
                    e, "error on {0}(notes: {1})", RIExceptionUtils.method(), "bpm create dashboard"
            )
                    .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

    @Nonnull
    @Override
    public DashboardRequestDocument updateDocument(
            @Nonnull String id,
            @Nonnull DashboardRequestDocument newDocument,
            boolean skipUnchanged,
            boolean flagReindex,
            @Nullable String notes
    ) {
        calculateActuallyResettledBuildingsRate(newDocument);
        calculateActuallyResettledResidenceRate(newDocument);
        return super.updateDocument(id, newDocument, skipUnchanged, flagReindex, notes);
    }

    private void calculateActuallyResettledBuildingsRate(DashboardRequestDocument document) {
        DashboardRequestType main = document.getDocument().getMain();
        if (
                isNull(main)
                        || isNull(main.getDs())
                        || isNull(main.getDgp())
                        || isNull(main.getDs().getDemolishedAmount())
                        || isNull(main.getDs().getPreparingForDemolitionAmount())
                        || isNull(main.getDgp().getPlannedResettledBuildingsAmount())

        ) {
            LOG.debug("Недостаточно данных для подсчета Фактически отселено домов");
            return;
        }

        main.getDgp().setActuallyResettledBuildingsRate(
                (double) Math.round(
                        (main.getDs().getDemolishedAmount().doubleValue()
                                + main.getDs().getPreparingForDemolitionAmount().doubleValue())
                                / main.getDgp().getPlannedResettledBuildingsAmount().doubleValue() * 100
                )
        );

    }

    private void calculateActuallyResettledResidenceRate(DashboardRequestDocument document) {
        DashboardRequestType main = document.getDocument().getMain();
        if (
                isNull(main)
                        || isNull(main.getDgp())
                        || isNull(main.getDgp().getResettledResidentsAmount())
                        || isNull(main.getDgp().getPlannedResettledBuildingsAmount())
        ) {
            LOG.debug("Недостаточно данных для подсчета Фактически переселено");
            return;
        }

        main.getDgp().setActuallyResettledResidenceRate(
                (double) Math.round(
                        main.getDgp().getResettledResidentsAmount().doubleValue()
                                / main.getDgp().getPlannedResettledResidentsAmount() * 100
                )
        );
    }
}
