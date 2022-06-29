package ru.croc.ugd.ssr.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentDefect;
import ru.croc.ugd.ssr.ApartmentDefectConfirmation;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ArmApplyRequest;
import ru.croc.ugd.ssr.ArmIssueOfferLetterRequest;
import ru.croc.ugd.ssr.ArmShowApartRequest;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.Compensation;
import ru.croc.ugd.ssr.Dashboard;
import ru.croc.ugd.ssr.DashboardRequest;
import ru.croc.ugd.ssr.FirstFlowErrorAnalytics;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.SoonResettlementRequest;
import ru.croc.ugd.ssr.affaircollation.AffairCollation;
import ru.croc.ugd.ssr.bus.BusRequest;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisation;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.courtinfo.CourtInfo;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDaySchedule;
import ru.croc.ugd.ssr.disabledperson.DisabledPerson;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonImport;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.feedback.Survey;
import ru.croc.ugd.ssr.flatappointment.FlatAppointment;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedError;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequest;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlow;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowQueue;
import ru.croc.ugd.ssr.ipev.IpevLog;
import ru.croc.ugd.ssr.mdm.MdmExternalPersonInfo;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.ApartmentDefectDocument;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.ArmApplyRequestDocument;
import ru.croc.ugd.ssr.model.ArmIssueOfferLetterRequestDocument;
import ru.croc.ugd.ssr.model.ArmShowApartRequestDocument;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.DashboardDocument;
import ru.croc.ugd.ssr.model.DashboardRequestDocument;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.FlowReportedErrorDocument;
import ru.croc.ugd.ssr.model.MdmExternalPersonInfoDocument;
import ru.croc.ugd.ssr.model.NotaryDayScheduleDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.PfrSnilsRequestDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.ShippingDayScheduleDocument;
import ru.croc.ugd.ssr.model.SoonResettlementRequestDocument;
import ru.croc.ugd.ssr.model.SsrSmevResponseDocument;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.model.compensation.CompensationDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.model.courtinfo.CourtInfoDocument;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.model.feedback.SurveyDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowQueueDocument;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.model.personuploadlog.PersonUploadLogDocument;
import ru.croc.ugd.ssr.model.rsm.RsmObjectRequestLogDocument;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.model.trade.TradeDataBatchStatusDocument;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensation;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsing;
import ru.croc.ugd.ssr.personaldocument.PersonalDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplication;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentRequest;
import ru.croc.ugd.ssr.personuploadlog.PersonUploadLog;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequest;
import ru.croc.ugd.ssr.rsm.RsmObjectRequestLog;
import ru.croc.ugd.ssr.shipping.ShippingApplicant;
import ru.croc.ugd.ssr.shipping.ShippingDaySchedule;
import ru.croc.ugd.ssr.smevresponse.SsrSmevResponse;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionHistory;
import ru.croc.ugd.ssr.trade.TradeApplicationFile;
import ru.croc.ugd.ssr.trade.TradeDataBatchStatus;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentTypesRegistry;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Getter
public class SsrDocumentTypes {

    public static final DocumentType<RealEstateDocument> REAL_ESTATE = new DocumentType<>(
        "REAL-ESTATE",
        "Документ - объект недвижимости",
        RealEstateDocument.class,
        RealEstate.class,
        "RealEstate"
    );

    public static final DocumentType<PersonDocument> PERSON = new DocumentType<>(
        "PERSON",
        "Документ - житель дома",
        PersonDocument.class,
        Person.class,
        "Person"
    );

    public static final DocumentType<CipDocument> CIP = new DocumentType<>(
        "CIP",
        "Документ - Центр информирования по переселению жителей",
        CipDocument.class,
        Cip.class,
        "Cip"
    );

    public static final DocumentType<SoonResettlementRequestDocument> SOON_RESETTLEMENT_REQUEST = new DocumentType<>(
        "SOON-RESETTLEMENT-REQUEST",
        "Документ - запрос на скорое переселение домов",
        SoonResettlementRequestDocument.class,
        SoonResettlementRequest.class,
        "SoonResettlementRequest"
    );

    public static final DocumentType<ResettlementRequestDocument> RESETTLEMENT_REQUEST = new DocumentType<>(
        "RESETTLEMENT-REQUEST",
        "Документ - запрос на переселение домов/квартир",
        ResettlementRequestDocument.class,
        ResettlementRequest.class,
        "ResettlementRequest"
    );

    public static final DocumentType<DashboardDocument> DASHBOARD = new DocumentType<>(
        "DASHBOARD",
        "Дашборд ССР",
        DashboardDocument.class,
        Dashboard.class,
        "Dashboard"
    );

    public static final DocumentType<DashboardRequestDocument> DASHBOARD_REQUEST = new DocumentType<>(
        "DASHBOARD-REQUEST",
        "Запрос на создание дашборда ССР",
        DashboardRequestDocument.class,
        DashboardRequest.class,
        "DashboardRequest"
    );

    public static final DocumentType<ArmIssueOfferLetterRequestDocument> ARM_ISSUE_OFFER_LETTER_REQUEST = new DocumentType<>(
        "ARM-ISSUE-OFFER-LETTER-REQUEST",
        "Запрос на выдачу письма с предложением",
        ArmIssueOfferLetterRequestDocument.class,
        ArmIssueOfferLetterRequest.class,
        "ArmIssueOfferLetterRequest"
    );

    public static final DocumentType<ArmShowApartRequestDocument> ARM_SHOW_APART_REQUEST = new DocumentType<>(
        "ARM-SHOW-APART-REQUEST",
        "Запрос на показ квартиры",
        ArmShowApartRequestDocument.class,
        ArmShowApartRequest.class,
        "ArmShowApartRequest"
    );

    public static final DocumentType<ArmApplyRequestDocument> ARM_APPLY_REQUEST = new DocumentType<>(
        "ARM-APPLY-REQUEST",
        "Запрос на принятие/отказ",
        ArmApplyRequestDocument.class,
        ArmApplyRequest.class,
        "ArmApplyRequest"
    );

    public static final DocumentType<FirstFlowErrorAnalyticsDocument> FIRST_FLOW_ERROR_ANALYTICS = new DocumentType<>(
        "FIRST-FLOW-ERROR-ANALYTICS",
        "Разбор ошибок обновления данных первого потока",
        FirstFlowErrorAnalyticsDocument.class,
        FirstFlowErrorAnalytics.class,
        "FirstFlowErrorAnalytics"
    );

    public static final DocumentType<ApartmentInspectionDocument> APARTMENT_INSPECTION = new DocumentType<>(
        "APARTMENT-INSPECTION",
        "Осмотр квартиры на предмет дефектов",
        ApartmentInspectionDocument.class,
        ApartmentInspection.class,
        "ApartmentInspection"
    );

    public static final DocumentType<ApartmentDefectDocument> APARTMENT_DEFECT = new DocumentType<>(
        "DEFECT",
        "Дефекты квартиры",
        ApartmentDefectDocument.class,
        ApartmentDefect.class,
        "ApartmentDefect"
    );

    public static final DocumentType<ShippingDayScheduleDocument> SHIPPING_DAY_SCHEDULE = new DocumentType<>(
        "SHIPPING-DAY-SCHEDULE",
        "Рабочий день помощи в переезде",
        ShippingDayScheduleDocument.class,
        ShippingDaySchedule.class,
        "ShippingDaySchedule"
    );

    public static final DocumentType<ShippingApplicationDocument> SHIPPING_APPLICATION = new DocumentType<>(
        "SHIPPING-APPLICATION",
        "Заявление на помощь в переезде",
        ShippingApplicationDocument.class,
        ShippingApplicant.class,
        "ShippingApplication"
    );

    public static final DocumentType<TradeAdditionDocument> TRADE_ADDITION = new DocumentType<>(
        "TRADE-ADDITION",
        "Данные по типу сделки",
        TradeAdditionDocument.class,
        TradeAddition.class,
        "TradeAddition"
    );

    public static final DocumentType<TradeAdditionHistoryDocument> TRADE_ADDITION_HISTORY = new DocumentType<>(
        "TRADE-ADDITION-HISTORY",
        "Данные об изменениях типа сделки",
        TradeAdditionHistoryDocument.class,
        TradeAdditionHistory.class,
        "TradeAdditionHistory"
    );

    public static final DocumentType<TradeApplicationFileDocument> TRADE_APPLICATION_FILE = new DocumentType<>(
        "TRADE-APPLICATION-FILE",
        "Данные о файле приложения о сделке",
        TradeApplicationFileDocument.class,
        TradeApplicationFile.class,
        "TradeApplicationFile"
    );

    public static final DocumentType<TradeDataBatchStatusDocument> TRADE_DATA_BATCH_STATUS = new DocumentType<>(
        "TRADE-DATA-BATCH-STATUS",
        "Данные по типу сделки",
        TradeDataBatchStatusDocument.class,
        TradeDataBatchStatus.class,
        "TradeDataBatchStatus"
    );

    public static final DocumentType<CommissionInspectionDocument> COMMISSION_INSPECTION = new DocumentType<>(
        "COMMISSION-INSPECTION",
        "Заявление на комиссионный осмотр",
        CommissionInspectionDocument.class,
        CommissionInspection.class,
        "CommissionInspection"
    );

    public static final DocumentType<CommissionInspectionOrganisationDocument> COMMISSION_INSPECTION_ORGANISATION
        = new DocumentType<>(
        "COMMISSION-INSPECTION-ORGANISATION",
        "Организация, осуществляющая запись на комиссионный осмотр в зависимости от вида переселения",
        CommissionInspectionOrganisationDocument.class,
        CommissionInspectionOrganisation.class,
        "CommissionInspectionOrganisation"
    );

    public static final DocumentType<PersonUploadLogDocument> PERSON_UPLOAD_LOG = new DocumentType<>(
        "PERSON-UPLOAD-LOG",
        "Журнал загрузки жителей",
        PersonUploadLogDocument.class,
        PersonUploadLog.class,
        "PersonUploadLog"
    );

    public static final DocumentType<NotaryDocument> NOTARY = new DocumentType<>(
        "NOTARY",
        "Карточка нотариуса",
        NotaryDocument.class,
        Notary.class,
        "Notary"
    );
    public static final DocumentType<NotaryApplicationDocument> NOTARY_APPLICATION = new DocumentType<>(
        "NOTARY-APPLICATION",
        "Заявление на посещение нотариуса",
        NotaryApplicationDocument.class,
        NotaryApplication.class,
        "NotaryApplication"
    );

    public static final DocumentType<NotaryDayScheduleDocument> NOTARY_DAY_SCHEDULE = new DocumentType<>(
        "NOTARY-DAY-SCHEDULE",
        "Рабочий день нотариуса",
        NotaryDayScheduleDocument.class,
        NotaryDaySchedule.class,
        "NotaryDaySchedule"
    );

    public static final DocumentType<GuardianshipRequestDocument> GUARDIANSHIP_REQUEST = new DocumentType<>(
        "GUARDIANSHIP",
        "Сделка с органами опеки",
        GuardianshipRequestDocument.class,
        GuardianshipRequest.class,
        "GuardianshipRequest"
    );

    public static final DocumentType<ContractAppointmentDocument> CONTRACT_APPOINTMENT = new DocumentType<>(
        "CONTRACT-APPOINTMENT",
        "Запись на заключение договора",
        ContractAppointmentDocument.class,
        ContractAppointment.class,
        "ContractAppointment"
    );

    public static final DocumentType<ContractAppointmentDayScheduleDocument> CONTRACT_APPOINTMENT_DAY_SCHEDULE
        = new DocumentType<>(
        "CONTRACT-APPOINTMENT-DAY-SCHEDULE",
        "Расписание записей на заключение договора",
        ContractAppointmentDayScheduleDocument.class,
        ContractAppointmentDaySchedule.class,
        "ContractAppointmentDaySchedule"
    );

    public static final DocumentType<FlatAppointmentDocument> FLAT_APPOINTMENT = new DocumentType<>(
        "FLAT-APPOINTMENT",
        "Запись на осмотр квартиры",
        FlatAppointmentDocument.class,
        FlatAppointment.class,
        "FlatAppointment"
    );

    public static final DocumentType<FlatAppointmentDayScheduleDocument> FLAT_APPOINTMENT_DAY_SCHEDULE
        = new DocumentType<>(
        "FLAT-APPOINTMENT-DAY-SCHEDULE",
        "Расписание записей на осмотр квартиры",
        FlatAppointmentDayScheduleDocument.class,
        FlatAppointmentDaySchedule.class,
        "FlatAppointmentDaySchedule"
    );

    public static final DocumentType<PersonalDocumentDocument> PERSONAL_DOCUMENT
        = new DocumentType<>(
        "PERSONAL-DOCUMENT",
        "Сведения о документах",
        PersonalDocumentDocument.class,
        PersonalDocument.class,
        "PersonalDocument"
    );

    public static final DocumentType<PersonalDocumentApplicationDocument> PERSONAL_DOCUMENT_APPLICATION
        = new DocumentType<>(
        "PERSONAL-DOCUMENT-APPLICATION",
        "Заявление на предоставление документов",
        PersonalDocumentApplicationDocument.class,
        PersonalDocumentApplication.class,
        "PersonalDocumentApplication"
    );

    public static final DocumentType<PersonalDocumentRequestDocument> PERSONAL_DOCUMENT_REQUEST
        = new DocumentType<>(
        "PERSONAL-DOCUMENT-REQUEST",
        "Сведения о запросе документов",
        PersonalDocumentRequestDocument.class,
        PersonalDocumentRequest.class,
        "PersonalDocumentRequest"
    );

    public static final DocumentType<FlowReportedErrorDocument> FLOW_REPORTED_ERROR = new DocumentType<>(
        "FLOW-REPORTED-ERROR",
        "Отчет об ошибках получения данных из ДГИ",
        FlowReportedErrorDocument.class,
        FlowReportedError.class,
        "FlowReportedError"
    );

    public static final DocumentType<SsrSmevResponseDocument> SSR_SMEV_RESPONSE = new DocumentType<>(
        "SSR-SMEV-RESPONSE",
        "Ответ на запрос БР",
        SsrSmevResponseDocument.class,
        SsrSmevResponse.class,
        "SsrSmevResponse"
    );

    public static final DocumentType<CompensationDocument> COMPENSATION
        = new DocumentType<>(
        "COMPENSATION",
        "Данные по квартирам на компенсацию",
        CompensationDocument.class,
        Compensation.class,
        "Compensation"
    );

    public static final DocumentType<OfferLetterParsingDocument> OFFER_LETTER_PARSING = new DocumentType<>(
        "OFFER-LETTER-PARSING",
        "Разбор письма с предложением",
        OfferLetterParsingDocument.class,
        OfferLetterParsing.class,
        "OfferLetterParsing"
    );

    public static final DocumentType<EgrnFlatRequestDocument> EGRN_FLAT_REQUEST = new DocumentType<>(
        "EGRN-FLAT-REQUEST",
        "Запрос в ЕГРН на получение выписки по квартире",
        EgrnFlatRequestDocument.class,
        EgrnFlatRequest.class,
        "EgrnFlat"
    );

    public static final DocumentType<EgrnBuildingRequestDocument> EGRN_BUILDING_REQUEST = new DocumentType<>(
        "EGRN-BUILDING-REQUEST",
        "Запрос в ЕГРН на получение выписки по ОН",
        EgrnBuildingRequestDocument.class,
        EgrnBuildingRequest.class,
        "EgrnBuilding"
    );

    public static final DocumentType<BusRequestDocument> BUS_REQUEST = new DocumentType<>(
        "BUS-REQUEST",
        "Запрос в платформенный модуль bus",
        BusRequestDocument.class,
        BusRequest.class,
        "BusRequest"
    );

    public static final DocumentType<NotaryCompensationDocument> NOTARY_COMPENSATION = new DocumentType<>(
        "NOTARY-COMPENSATION",
        "Возмещение оплаты услуг нотариуса",
        NotaryCompensationDocument.class,
        NotaryCompensation.class,
        "NotaryCompensation"
    );

    public static final DocumentType<ContractDigitalSignDocument> CONTRACT_DIGITAL_SIGN = new DocumentType<>(
        "CONTRACT-DIGITAL-SIGN",
        "Многостороннее подписание договора с использованием УКЭП",
        ContractDigitalSignDocument.class,
        ContractDigitalSign.class,
        "ContractDigitalSign"
    );

    public static final DocumentType<SsrCcoDocument> SSR_CCO = new DocumentType<>(
        "SSR-CCO",
        "ОКС в системе ССР",
        SsrCcoDocument.class,
        SsrCco.class,
        "SsrCco"
    );

    public static final DocumentType<ApartmentDefectConfirmationDocument> APARTMENT_DEFECT_CONFIRMATION =
        new DocumentType<>(
            "APARTMENT-DEFECT-CONFIRMATION",
            "Подтверждение сведений об устранении дефектов",
            ApartmentDefectConfirmationDocument.class,
            ApartmentDefectConfirmation.class,
            "ApartmentDefectConfirmation"
        );

    public static final DocumentType<RsmObjectRequestLogDocument> RSM_OBJECT_REQUEST_LOG = new DocumentType<>(
        "RSM-OBJECT-REQUEST-LOG",
        "Журнал запросов ОКС",
        RsmObjectRequestLogDocument.class,
        RsmObjectRequestLog.class,
        "RsmObjectRequestLog"
    );

    public static final DocumentType<DisabledPersonDocument> DISABLED_PERSON = new DocumentType<>(
        "DISABLED-PERSON",
        "Сведения о маломобильности жителя",
        DisabledPersonDocument.class,
        DisabledPerson.class,
        "DisabledPerson"
    );

    public static final DocumentType<DisabledPersonImportDocument> DISABLED_PERSON_IMPORT = new DocumentType<>(
        "DISABLED-PERSON-IMPORT",
        "Загрузка сведений по маломобильным гражданам",
        DisabledPersonImportDocument.class,
        DisabledPersonImport.class,
        "DisabledPersonImport"
    );

    public static final DocumentType<CourtInfoDocument> COURT_INFO = new DocumentType<>(
        "COURT-INFO",
        "Сведения о судах",
        CourtInfoDocument.class,
        CourtInfo.class,
        "CourtInfo"
    );

    public static final DocumentType<IntegrationFlowDocument> INTEGRATION_FLOW = new DocumentType<>(
        "INTEGRATION-FLOW",
        "Сообщение по потоку",
        IntegrationFlowDocument.class,
        IntegrationFlow.class,
        "IntegrationFlow"
    );

    public static final DocumentType<IpevLogDocument> IPEV_LOG = new DocumentType<>(
        "IPEV-LOG",
        "Журнал обмена с ИПЭВ",
        IpevLogDocument.class,
        IpevLog.class,
        "IpevLog"
    );

    public static final DocumentType<PfrSnilsRequestDocument> PFR_SNILS_REQUEST = new DocumentType<>(
        "PFR-SNILS-REQUEST",
        "Запрос СНИЛС в ПФР",
        PfrSnilsRequestDocument.class,
        PfrSnilsRequest.class,
        "PfrSnilsRequest"
    );

    public static final DocumentType<MdmExternalPersonInfoDocument> MDM_EXTERNAL_PERSON_INFO = new DocumentType<>(
        "MDM-EXTERNAL-PERSON-INFO",
        "Данные жителя из МДМ",
        MdmExternalPersonInfoDocument.class,
        MdmExternalPersonInfo.class,
        "MdmExternalPersonInfo"
    );

    public static final DocumentType<AffairCollationDocument> AFFAIR_COLLATION = new DocumentType<>(
        "AFFAIR-COLLATION",
        "Данные о запросе на сверку жителей",
        AffairCollationDocument.class,
        AffairCollation.class,
        "AffairCollation"
    );

    public static final DocumentType<IntegrationFlowQueueDocument> INTEGRATION_FLOW_QUEUE = new DocumentType<>(
        "INTEGRATION-FLOW-QUEUE",
        "Сообщение во внутренней очереди, отвечающее за обработку поточного сообщения",
        IntegrationFlowQueueDocument.class,
        IntegrationFlowQueue.class,
        "IntegrationFlowQueue"
    );

    public static final DocumentType<SurveyDocument> SURVEY = new DocumentType<>(
        "SURVEY",
        "Опрос",
        SurveyDocument.class,
        Survey.class,
        "Survey"
    );

    private final DocumentTypesRegistry documentTypes;

    @PostConstruct
    public void init() {
        documentTypes.registerDocumentType(REAL_ESTATE);
        documentTypes.registerDocumentType(PERSON);
        documentTypes.registerDocumentType(SOON_RESETTLEMENT_REQUEST);
        documentTypes.registerDocumentType(RESETTLEMENT_REQUEST);
        documentTypes.registerDocumentType(DASHBOARD);
        documentTypes.registerDocumentType(DASHBOARD_REQUEST);
        documentTypes.registerDocumentType(ARM_ISSUE_OFFER_LETTER_REQUEST);
        documentTypes.registerDocumentType(ARM_SHOW_APART_REQUEST);
        documentTypes.registerDocumentType(ARM_APPLY_REQUEST);
        documentTypes.registerDocumentType(CIP);
        documentTypes.registerDocumentType(FIRST_FLOW_ERROR_ANALYTICS);
        documentTypes.registerDocumentType(APARTMENT_INSPECTION);
        documentTypes.registerDocumentType(APARTMENT_DEFECT);
        documentTypes.registerDocumentType(SHIPPING_DAY_SCHEDULE);
        documentTypes.registerDocumentType(SHIPPING_APPLICATION);
        documentTypes.registerDocumentType(TRADE_ADDITION);
        documentTypes.registerDocumentType(TRADE_APPLICATION_FILE);
        documentTypes.registerDocumentType(TRADE_ADDITION_HISTORY);
        documentTypes.registerDocumentType(TRADE_DATA_BATCH_STATUS);
        documentTypes.registerDocumentType(COMMISSION_INSPECTION);
        documentTypes.registerDocumentType(COMMISSION_INSPECTION_ORGANISATION);
        documentTypes.registerDocumentType(PERSON_UPLOAD_LOG);
        documentTypes.registerDocumentType(NOTARY);
        documentTypes.registerDocumentType(NOTARY_APPLICATION);
        documentTypes.registerDocumentType(NOTARY_DAY_SCHEDULE);
        documentTypes.registerDocumentType(GUARDIANSHIP_REQUEST);
        documentTypes.registerDocumentType(CONTRACT_APPOINTMENT);
        documentTypes.registerDocumentType(CONTRACT_APPOINTMENT_DAY_SCHEDULE);
        documentTypes.registerDocumentType(FLAT_APPOINTMENT);
        documentTypes.registerDocumentType(FLAT_APPOINTMENT_DAY_SCHEDULE);
        documentTypes.registerDocumentType(PERSONAL_DOCUMENT);
        documentTypes.registerDocumentType(PERSONAL_DOCUMENT_APPLICATION);
        documentTypes.registerDocumentType(PERSONAL_DOCUMENT_REQUEST);
        documentTypes.registerDocumentType(FLOW_REPORTED_ERROR);
        documentTypes.registerDocumentType(SSR_SMEV_RESPONSE);
        documentTypes.registerDocumentType(COMPENSATION);
        documentTypes.registerDocumentType(OFFER_LETTER_PARSING);
        documentTypes.registerDocumentType(EGRN_FLAT_REQUEST);
        documentTypes.registerDocumentType(EGRN_BUILDING_REQUEST);
        documentTypes.registerDocumentType(BUS_REQUEST);
        documentTypes.registerDocumentType(NOTARY_COMPENSATION);
        documentTypes.registerDocumentType(CONTRACT_DIGITAL_SIGN);
        documentTypes.registerDocumentType(SSR_CCO);
        documentTypes.registerDocumentType(APARTMENT_DEFECT_CONFIRMATION);
        documentTypes.registerDocumentType(RSM_OBJECT_REQUEST_LOG);
        documentTypes.registerDocumentType(DISABLED_PERSON);
        documentTypes.registerDocumentType(DISABLED_PERSON_IMPORT);
        documentTypes.registerDocumentType(COURT_INFO);
        documentTypes.registerDocumentType(INTEGRATION_FLOW);
        documentTypes.registerDocumentType(IPEV_LOG);
        documentTypes.registerDocumentType(PFR_SNILS_REQUEST);
        documentTypes.registerDocumentType(MDM_EXTERNAL_PERSON_INFO);
        documentTypes.registerDocumentType(AFFAIR_COLLATION);
        documentTypes.registerDocumentType(INTEGRATION_FLOW_QUEUE);
        documentTypes.registerDocumentType(SURVEY);
    }

}
