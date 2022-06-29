package ru.croc.ugd.ssr.solr.converter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.BuildingsInResettlementProcess;
import ru.croc.ugd.ssr.Dashboard;
import ru.croc.ugd.ssr.ResettlementBuildings;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.solr.UgdSsrDashboard;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.math.BigInteger;

/**
 * Конвертор для дашборда.
 */
@Service
@Slf4j
public class DashboardDocumentConverter extends SsrDocumentConverter<DashboardDocument, UgdSsrDashboard> {

    @Override
    public DocumentType<DashboardDocument> getDocumentType() {
        return SsrDocumentTypes.DASHBOARD;
    }

    @Override
    public UgdSsrDashboard convertDocument(@NotNull DashboardDocument dashboardDocument) {
        final UgdSsrDashboard position = createDocument(getAnyAccessType(), dashboardDocument.getId());
        final Dashboard dashboard = dashboardDocument.getDocument();
        if (dashboard.getUser() != null && dashboard.getUser().getName() != null) {
            position.setUserName(dashboard.getUser().getName());
        }

        if (dashboard.getData() != null && dashboard.getData().getInformationDate() != null) {
            position.setInformationDate(dashboard.getData().getInformationDate());
        }

        if (dashboard.getFillInDate() != null) {
            position.setFillInDate(dashboard.getFillInDate());
        }

        position.setIsArchived(dashboard.isArchived());

        if (dashboard.getData() != null && dashboard.getData().getNewBuildingsTotalAmount() != null) {
            position.setTotalNewBuildings(dashboard.getData().getNewBuildingsTotalAmount().longValue());
        }

        position.setPublished(dashboard.isPublished());
        // Вынужденное решение
        position.setNotAuto(dashboard.isAuto());

        final ResettlementBuildings resettlementBuildings = dashboardDocument
            .getDocument().getData().getResettlementBuildingsInfo();
        final BuildingsInResettlementProcess buildingsInResettlementProcess = dashboardDocument
            .getDocument().getData().getBuildingsInResettlementProcessInfo();
        if (buildingsInResettlementProcess != null &&
            resettlementBuildings != null &&
            buildingsInResettlementProcess.getPartResettlementInProcessAmount() != null
            && resettlementBuildings.getPreparingForDemolitionAmount() != null
            && resettlementBuildings.getDemolishedAmount() != null
            && buildingsInResettlementProcess.getResidentsResettledAmount() != null
            && buildingsInResettlementProcess.getOutsideResidenceResettlementAmount() != null
            && buildingsInResettlementProcess.getPaperworkCompletedAndFundIssuesAmount() != null
            && buildingsInResettlementProcess.getResettlementInProcessAmount() != null) {
            BigInteger total = resettlementBuildings.getDemolishedAmount()
                .add(resettlementBuildings.getPreparingForDemolitionAmount()
                    .add(buildingsInResettlementProcess.getPartResettlementInProcessAmount()));
            total = total.add(buildingsInResettlementProcess.getOutsideResidenceResettlementAmount()
                .add(buildingsInResettlementProcess.getPaperworkCompletedAndFundIssuesAmount()
                    .add(buildingsInResettlementProcess.getResettlementInProcessAmount()
                        .add(buildingsInResettlementProcess.getResidentsResettledAmount())
                    )));
            position.setTotalResettlementsBuildings(total.longValue());
        }
        return position;
    }
}
