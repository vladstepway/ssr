package ru.croc.ugd.ssr.integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Проперти для интеграции.
 */
@ConfigurationProperties(prefix = IntegrationProperties.PREFIX)
@Getter
@Setter
public class IntegrationProperties {

    /**
     * Префикс свойств интеграции.
     */
    public static final String PREFIX = "ugd.ssr.integration";

    private String xmlImportFlowFirst;

    private String xmlExportFlowFirst;

    private String xmlImportFlowSecond;

    private String xmlExportFlowSecond;

    private String xmlExportFlowThird;

    private String xmlImportFlowFourth;

    private String xmlExportFlowFifth;

    private String xmlExportFlowNinth;

    private String xmlExportFlowTenth;

    private String xmlExportFlowSixth;

    private String xmlExportFlowEighth;

    private String xmlImportFlowEighth;

    private String xmlImportFlowSeventh;

    private String xmlExportFlowSeventh;

    private String xmlImportFlowTwentieth;

    private String xmlExportFlowTwentieth;

    private String xmlImportFlowTradeAddition;

    private String xmlExportFlowTradeAddition;

    private String xmlExportMfrResettlementInfo;

    private String xmlExportMfrAdministrativeDocuments;

    private String xmlExportMfrContract;

    private String xmlExportMfrContractProject;

    private String xmlExportMfrPersonInfo;

    private String xmlExportMfrReleaseFlat;

    private String xmlImportMfrMessage;

    private String xmlImportMfrStatus;

    private String xmlExportMfrStatus;

    private String xmlImportShippingFlow;

    private String psUpdateFlatController =
        "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/updateFlatRemovableStatus?unom={0}&cadastr={1}";

    private String psCreateIfNotExistFlatController =
        "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/createFlatIfNotExist?unom={0}&flatNum={1}&removalStatus={2}";

    private String psCcoAddressByUnom = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/ccoAddressByUnom?unom={0}";

    private String psCcoFindCco = "/app/ugd/ps/crud/find/CAPITAL-CONSTRUCTION-OBJECT";

    private String psCcoFetchCco = "/app/ugd/ps/crud/fetch/CAPITAL-CONSTRUCTION-OBJECT/";

    private String xmlImportCommissionInspectionFlow;

    private String xmlImportNotaryApplicationFlow;

    private String xmlImportNotaryCompensationFlow;

    private String psCcoChessFlatsByUnom = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getCcoChessFlatsByUnom?unom={0}";

    private String psGetParticipantsFromCco = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getParticipantsFromCco";

    private String updateCcoFlats = "/app/ugd/ps/updateCcoFlats/{0}";

    private String updateCcoFlat = "/app/ugd/ps/updateCcoFlat/{0}";

    private String psCcoChessStatsByUnom = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getCcoChessStats?unom={0}";

    private String psFullCcoChessInfoByUnom = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getFullCcoChessInfo?unom={0}";

    private String psCcoChessEntranceFlatsByUnom
        = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getCcoChessEntranceFlatsByUnom?unom={0}&entrance={1}";

    private String psCcoChessFlatInfoByUnom
        = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getCcoChessFlatInfoByUnom?unom={0}&flatNumber={1}";

    private String psCcoChessFlatInfoByUnomFlatnumberEntranceAndFloor
        = "/app/ugd/ps/CAPITAL-CONSTRUCTION-OBJECT/getCcoChessFlatInfoByUnom?unom={0}&flatNumber={1}"
        + "&entrance={2}&floor={3}";

    private String psFindObjectsUsingXmlFilter = "/app/ugd/ps/openApi/findObjectsUsingXmlFilter?skip={0}&top={1}";

    private String orrRegistryEvents = "/app/ugdnpc/orr/registry-events/{0}";

    private String xmlExportFlowEleventh;

    private String xmlImportFlowTwelfth;

    private String xmlExportFlowTwelfth;

    private String xmlImportAdministrativeDocuments;

    private String xmlExportAdministrativeDocuments;

    private String ehdCatalogPushUpdatesImport;

    private String ehdCatalogPushUpdatesFailed;

    private String xmlImportContractAppointmentFlow;

    private String xmlImportFlatAppointmentFlow;

    private String xmlImportPersonalDocumentApplicationFlow;

    private String xmlExportDefectEliminationAct;

    private String xmlExportDefectEliminationTransfer;

    private String xmlExportDefectEliminationAgreement;

    private String xmlImportRsmObject;

    private String xmlExportRsmObject;

    private String xmlImportCourtInfo;

    private String xmlExportCourtInfo;

    private String xmlExportDisabledPerson;

    private String xmlImportDisabledPerson;

    private String xmlExportAffairCollation;

    private String xmlImportAffairCollation;
}
