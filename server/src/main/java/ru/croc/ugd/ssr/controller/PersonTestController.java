package ru.croc.ugd.ssr.controller;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.OfferLetterDocData;
import ru.croc.ugd.ssr.integration.service.flows.OfferLettersFlowsService;
import ru.croc.ugd.ssr.mq.listener.AdministrativeDocumentListener;
import ru.croc.ugd.ssr.mq.listener.ContractPrReadyListener;
import ru.croc.ugd.ssr.mq.listener.ContractReadyListener;
import ru.croc.ugd.ssr.mq.listener.CourtInfoListener;
import ru.croc.ugd.ssr.mq.listener.InfoSettlementListener;
import ru.croc.ugd.ssr.mq.listener.OfferLettersListener;
import ru.croc.ugd.ssr.mq.listener.PersonResponseListener;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.reinform.cdp.mqetpmv.model.EtpInboundMessage;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class PersonTestController {

    private final PersonResponseListener personResponseListener;
    private final OfferLettersListener offerLettersListener;
    private final OfferLettersFlowsService offerLettersFlowsService;
    private final AdministrativeDocumentListener administrativeDocumentListener;
    private final InfoSettlementListener infoSettlementListener;
    private final PersonDocumentService personDocumentService;
    private final CourtInfoListener courtInfoListener;
    private final ContractPrReadyListener contractPrReadyListener;
    private final ContractReadyListener contractReadyListener;

    @ApiOperation(value = "Incoming person event from DGI")
    @PostMapping(value = "person/test-first-flow")
    public void testFirstFlow(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        personResponseListener.handle(ofNullable(message).orElse(PERSON_TEST_MESSSAGE));
    }

    @ApiOperation(value = "Incoming offer letter event from DGI")
    @PostMapping(value = "person/test-second-flow")
    public void testSecondFlow(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        offerLettersListener.handle(ofNullable(message).orElse(OFFER_TEST_MESSSAGE));
    }

    @ApiOperation(value = "Event for DGI")
    @PostMapping(value = "person/test-13-flow")
    public void test13Flow(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        administrativeDocumentListener.handle(ofNullable(message).orElse(AD_TEST_MESSSAGE));
    }

    @ApiOperation(value = "Extract offer letter pdf data")
    @GetMapping(value = "person/offer-letter/parse")
    public OfferLetterDocData testParseOfferLetter(
        @ApiParam("pdf filestore id") @RequestParam String fileStoreId
    ) {
        return offerLettersFlowsService.extractOfferLetterDocInfo(fileStoreId);
    }

    @PostMapping(value = "person/test-8-flow")
    public void test8FlowContractSign(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        infoSettlementListener.handle(ofNullable(message).orElse(CONTRACT_SIGN_TEST_MESSAGE));
    }

    /**
     * Метод для заполнения пустых данных о квартире после разбора письма с предложением
     */
    @PostMapping(value = "person/fill-offer-letter-flat-data")
    public void fillOfferLetterFlatData() {
        personDocumentService.fillOfferLetterFlatData();
    }

    @PostMapping(value = "person/court-flow")
    public void test21FlowCourtInfo(
        @RequestParam("messageType") final String messageType, @RequestBody(required = false) final String message
    ) {
        final String testMessage = ofNullable(message).orElse(COURT_INFO_TEST_MESSAGE);
        final EtpInboundMessage etpInboundMessage = new EtpInboundMessage();
        etpInboundMessage.setMessage(testMessage);
        courtInfoListener.receiveMessage(
            ofNullable(messageType).orElse(TEST_COURT_MESSAGE_TYPE), etpInboundMessage
        );
    }

    @PostMapping(value = "person/actualize-keys-issue-and-release-flat")
    public void actualizeKeysIssueAndReleaseFlat(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate historicalResettlementDate
    ) {
        personDocumentService.actualizeKeysIssueAndReleaseFlat(historicalResettlementDate);
    }

    @PostMapping(value = "person/contract-pr-ready-flow")
    public void test12ContractPrReadyFlow(
        @RequestParam("messageType") final String messageType, @RequestBody(required = false) final String message
    ) {
        final String testMessage = ofNullable(message).orElse(CONTRACT_PR_READY_TEST_MESSAGE);
        final EtpInboundMessage etpInboundMessage = new EtpInboundMessage();
        etpInboundMessage.setMessage(testMessage);
        contractPrReadyListener.receiveMessage(
            ofNullable(messageType).orElse(TEST_CONTRACT_PR_READY_MESSAGE_TYPE), etpInboundMessage
        );
    }

    @PostMapping(value = "person/contract-ready-flow")
    public void test7ContractReadyFlow(
        @RequestParam("messageType") final String messageType, @RequestBody(required = false) final String message
    ) {
        final String testMessage = ofNullable(message).orElse(CONTRACT_READY_TEST_MESSAGE);
        final EtpInboundMessage etpInboundMessage = new EtpInboundMessage();
        etpInboundMessage.setMessage(testMessage);
        contractReadyListener.receiveMessage(
            ofNullable(messageType).orElse(TEST_CONTRACT_READY_MESSAGE_TYPE), etpInboundMessage
        );
    }

    private static final String PERSON_TEST_MESSSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<tns:CoordinateSendTaskStatusesMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\">\n" +
        "   <tns:CoordinateTaskStatusDataMessage>\n" +
        "      <tns:MessageId>AD3BBCC6-2F3B-83E7-E050-400A7E795E6C</tns:MessageId>\n" +
        "      <tns:TaskId>44ed1114-dcc0-3c6a-aef7-ce0bedd95fb8</tns:TaskId>\n" +
        "      <tns:Status>\n" +
        "         <tns:StatusCode>0</tns:StatusCode>\n" +
        "         <tns:StatusDate>2020-08-19T16:11:10+03:00</tns:StatusDate>\n" +
        "      </tns:Status>\n" +
        "      <tns:Result>\n" +
        "         <tns:ResultType>PositiveAnswer</tns:ResultType>\n" +
        "         <tns:ResultCode>0</tns:ResultCode>\n" +
        "         <tns:XmlView>\n" +
        "            <SuperServiceDGPPersonsMessage>\n" +
        "               <SuperServiceDGPPersonsResult>\n" +
        "                  <person_id>659879</person_id>\n" +
        "                  <actual_date />\n" +
        "                  <snils>480-953-512 09</snils>\n" +
        "                  <lastname>Булгаков</lastname>\n" +
        "                  <firstname>Михаил</firstname>\n" +
        "                  <middlename>Афанасьевич</middlename>\n" +
        "                  <birthdate>1975-03-07</birthdate>\n" +
        "                  <sex>2</sex>\n" +
        "                  <IsQueue>Нет</IsQueue>\n" +
        "                  <IsDead />\n" +
        "                  <ChangeStatus>Начальная загрузка</ChangeStatus>\n" +
        "                  <delReason />\n" +
        "                  <LinkedFlats>\n" +
        "                     <Flat>\n" +
        "                        <affair_id>866358</affair_id>\n" +
        "                        <statusLiving>Пользователь</statusLiving>\n" +
        "                        <encumbrances>Нет данных</encumbrances>\n" +
        "                        <snos_unom>11325</snos_unom>\n" +
        "                        <snos_cadnum>77:04:0002016:1069</snos_cadnum>\n" +
        "                        <snos_flat_num>48</snos_flat_num>\n" +
        "                        <snos_rooms_num />\n" +
        "                        <noFlat />\n" +
        "                        <isFederal />\n" +
        "                        <inCourt>Нет</inCourt>\n" +
        "                     </Flat>\n" +
        "                  </LinkedFlats>\n" +
        "               </SuperServiceDGPPersonsResult>\n" +
        "            </SuperServiceDGPPersonsMessage>\n" +
        "         </tns:XmlView>\n" +
        "      </tns:Result>\n" +
        "   </tns:CoordinateTaskStatusDataMessage>\n" +
        "</tns:CoordinateSendTaskStatusesMessage>";

    private static final String AD_TEST_MESSSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n" +
        "<tns:SendTasksMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ ServiceV6_1_8.xsd\">\n" +
        "<tns:CoordinateTaskDataMessages>\n" +
        "<tns:CoordinateTaskData wsu:Id=\"ID_1\">\n" +
        "<tns:Task>\n" +
        "<tns:MessageId>ACFC586D-EC27-44DA-B3ED-736D41E33ED1</tns:MessageId>\n" +
        "<tns:TaskId>39D15362-0EE3-4B5D-BC56-C49DFE17414D</tns:TaskId> \n" +
        "<tns:TaskNumber>2021-05-217414</tns:TaskNumber> \n" +
        "<tns:TaskDate>2021-05-21T14:56:13+03:00</tns:TaskDate> \n" +
        "<tns:Responsible>\n" +
        "<tns:LastName>Петров</tns:LastName>\n" +
        "<tns:FirstName>Петр</tns:FirstName> \n" +
        "<tns:MiddleName>String</tns:MiddleName> \n" +
        "<tns:JobTitle>Директор</tns:JobTitle> \n" +
        "<tns:Phone>123</tns:Phone> \n" +
        "<tns:Email>13@123</tns:Email> \n" +
        "<tns:IsiId>1</tns:IsiId> \n" +
        "<tns:SsoId>2</tns:SsoId> \n" +
        "<tns:MdmId>3</tns:MdmId> \n" +
        "</tns:Responsible>\n" +
        "<tns:Department>\n" +
        "<tns:Name>Департамент городского имущества города Москвы</tns:Name> \n" +
        "<tns:Code>446</tns:Code> \n" +
        "<tns:Inn>7705031674</tns:Inn> \n" +
        "<tns:Ogrn>1037739510423</tns:Ogrn> \n" +
        "<tns:RegDate>1991-11-15T00:00:00</tns:RegDate>\n" +
        "<tns:SystemCode>1000874</tns:SystemCode> \n" +
        "</tns:Department>\n" +
        "<tns:ServiceNumber>0446-1000874-880045- 0000046/21</tns:ServiceNumber>\n" +
        "<tns:ServiceTypeCode>7777777</tns:ServiceTypeCode> \n" +
        "</tns:Task>\n" +
        "<tns:Data>\n" +
        "<tns:DocumentTypeCode>555</tns:DocumentTypeCode>\n" +
        "<tns:ParameterTypeCode>666</tns:ParameterTypeCode> \n" +
        "<tns:Parameter>\n" +
        "<SuperServiceDGPAdministrativeDocument>\n" +
        "<person_id>659879</person_id>\n" +
        "<affair_id>866358</affair_id>\n" +
        "<letter_id>-1</letter_id>\n" +
        "<administrativeDocumentLink>735e6ce9-65cd-4c6e-b556-192048f90a8f</administrativeDocumentLink>\n" +
        "<newFlats>\n" +
        "<newFlat>\n" +
        "<unom>7749</unom>\n" +
        "<cadnum>77:04:0002018:4369</cadnum>\n" +
        "<flatNumber>35</flatNumber>\n" +
        "</newFlat>\n" +
        "</newFlats>\n" +
        "<legalRepresentatives/>\n" +
        "</SuperServiceDGPAdministrativeDocument>\n" +
        "</tns:Parameter>\n" +
        "<tns:IncludeXmlView>false</tns:IncludeXmlView>\n" +
        "<tns:IncludeBinaryView>false</tns:IncludeBinaryView> \n" +
        "</tns:Data>\n" +
        "<tns:Signature>text</tns:Signature>\n" +
        "</tns:CoordinateTaskData>\n" +
        "</tns:CoordinateTaskDataMessages>\n" +
        "</tns:SendTasksMessage> ";

    private static final String OFFER_TEST_MESSSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n" +
        "<tns:SendTasksMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ ServiceV6_1_8.xsd\">\n" +
        "<tns:CoordinateTaskDataMessages>\n" +
        "<tns:CoordinateTaskData wsu:Id=\"ID_1\">\n" +
        "<tns:Task>\n" +
        "<tns:MessageId>0B835D2B-90A8-489A-87F3-392CF5289D83</tns:MessageId>\n" +
        "<tns:TaskId>F0885D5F-7193-4AF8-84B3-5AA078185174</tns:TaskId> \n" +
        "<tns:TaskNumber>2020-10-074425</tns:TaskNumber> \n" +
        "<tns:TaskDate>2020-10-07T12:17:35+03:00</tns:TaskDate> \n" +
        "<tns:Responsible>\n" +
        "<tns:LastName>Петров</tns:LastName>\n" +
        "<tns:FirstName>Петр</tns:FirstName> \n" +
        "<tns:MiddleName>String</tns:MiddleName> \n" +
        "<tns:JobTitle>Директор</tns:JobTitle> \n" +
        "<tns:Phone>123</tns:Phone> \n" +
        "<tns:Email>13@123</tns:Email> \n" +
        "<tns:IsiId>1</tns:IsiId> \n" +
        "<tns:SsoId>2</tns:SsoId> \n" +
        "<tns:MdmId>3</tns:MdmId> \n" +
        "</tns:Responsible>\n" +
        "<tns:Department>\n" +
        "<tns:Name>ДГП</tns:Name> \n" +
        "<tns:Code>String</tns:Code> \n" +
        "<tns:Inn>String</tns:Inn> \n" +
        "<tns:Ogrn>String</tns:Ogrn> \n" +
        "<tns:RegDate>2012-12-17T09:30:47Z</tns:RegDate>\n" +
        "<tns:SystemCode>90154</tns:SystemCode> \n" +
        "</tns:Department> \n" +
        "<tns:ServiceNumber>2591-9000154-067801-0000600/20</tns:ServiceNumber>\n" +
        "<tns:ServiceTypeCode>7777777</tns:ServiceTypeCode> \n" +
        "</tns:Task>\n" +
        "<tns:Data>\n" +
        "<tns:DocumentTypeCode>555</tns:DocumentTypeCode>\n" +
        "<tns:ParameterTypeCode>666</tns:ParameterTypeCode> \n" +
        "<tns:Parameter>\n" +
        "<SuperServiceDGPLetterRequest>\n" +
        "<person_id>9558516</person_id>\n" +
        "<affair_id>866101</affair_id>\n" +
        "<idCIP>3b998bd9-2d72-4cf9-9678-130713af884e</idCIP>\n" +
        "<pdfProposal>{DCB76000-F962-46DF-A373-CA263EF64ADC}</pdfProposal>\n" +
        "<pdfBlank></pdfBlank>\n" +
        "<letter_id>76696</letter_id>\n" +
        " </SuperServiceDGPLetterRequest>\n" +
        "</tns:Parameter>\n" +
        "<tns:IncludeXmlView>false</tns:IncludeXmlView>\n" +
        "<tns:IncludeBinaryView>false</tns:IncludeBinaryView> \n" +
        "</tns:Data>\n" +
        "<tns:Signature>text</tns:Signature>\n" +
        "</tns:CoordinateTaskData>\n" +
        "</tns:CoordinateTaskDataMessages>\n" +
        "</tns:SendTasksMessage>";

    private static final String CONTRACT_SIGN_TEST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<tns:CoordinateSendTaskStatusesMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\">\n" +
        "    <tns:CoordinateTaskStatusDataMessage>\n" +
        "        <tns:MessageId>AD3BBCC6-2F3B-83E7-E050-400A7E795E6C</tns:MessageId>\n" +
        "        <tns:TaskId>44ed1114-dcc0-3c6a-aef7-ce0bedd95fb8</tns:TaskId>\n" +
        "        <tns:Status>\n" +
        "            <tns:StatusCode>0</tns:StatusCode>\n" +
        "            <tns:StatusDate>2020-08-19T16:11:10+03:00</tns:StatusDate>\n" +
        "        </tns:Status>\n" +
        "        <tns:Result>\n" +
        "            <tns:ResultType>PositiveAnswer</tns:ResultType>\n" +
        "            <tns:ResultCode>0</tns:ResultCode>\n" +
        "            <tns:XmlView>\n" +
        "                <SuperServiceDGPSettleFlatInfo>\n" +
        "                    <person_id>659879</person_id>\n" +
        "                    <affair_id>659879</affair_id>\n" +
        "                    <order_id>659879</order_id>\n" +
        "                    <new_unom>659879</new_unom>\n" +
        "                    <new_cadnum>659879</new_cadnum>\n" +
        "                    <FlatNumber>659879</FlatNumber>\n" +
        "                </SuperServiceDGPSettleFlatInfo>\n" +
        "            </tns:XmlView>\n" +
        "        </tns:Result>\n" +
        "    </tns:CoordinateTaskStatusDataMessage>\n" +
        "</tns:CoordinateSendTaskStatusesMessage>";

    private static final String COURT_INFO_TEST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<tns:CoordinateSendTaskStatusesMessage xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\" xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
        + "    <tns:CoordinateTaskStatusDataMessage>\n"
        + "        <tns:MessageId>D0C5477A-A8A6-4B89-E050-400A7E7914F5</tns:MessageId>\n"
        + "        <tns:TaskId>D0C5477A-A8A6-4B89-E050-400A7E7914F5</tns:TaskId>\n"
        + "        <tns:Status>\n"
        + "            <tns:StatusCode>0</tns:StatusCode>\n"
        + "            <tns:StatusDate>2021-11-14T21:58:41+03:00</tns:StatusDate>\n"
        + "        </tns:Status>\n"
        + "        <tns:Result>\n"
        + "            <tns:ResultType>PositiveAnswer</tns:ResultType>\n"
        + "            <tns:ResultCode>0</tns:ResultCode>\n"
        + "            <tns:XmlView>\n"
        + "                <SuperServiceDGPCourtInfoType>\n"
        + "                    <affair_id>866358</affair_id>\n"
        + "                    <letter_id>-1</letter_id>\n"
        + "                    <case_id>735e6ce9-65cd-4c6e-b556-192048f90a8f</case_id>\n"
        + "                    <date_last_court>2022-11-15T00:00:00</date_last_court>\n"
        + "                    <date_last_act>2022-10-11T00:00:00</date_last_act>\n"
        + "                    <date_law>2022-09-12T00:00:00</date_law>\n"
        + "                    <result_delo>Наложен запрет о приближении</result_delo>\n"
        + "                </SuperServiceDGPCourtInfoType>\n"
        + "            </tns:XmlView>\n"
        + "        </tns:Result>\n"
        + "    </tns:CoordinateTaskStatusDataMessage>\n"
        + "</tns:CoordinateSendTaskStatusesMessage>";
    private static final String TEST_COURT_MESSAGE_TYPE = "UGD_RSM_COURT_TASK_INC";

    public static final String CONTRACT_PR_READY_TEST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
        + "<tns:SendTasksMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\">\n"
        + "    <tns:CoordinateTaskDataMessages>\n"
        + "        <tns:CoordinateTaskData wsu:Id=\"ID_1\">\n"
        + "            <tns:Task>\n"
        + "                <tns:MessageId>FE3376B6-6E49-41C3-91A9-DEBF9177F010</tns:MessageId>\n"
        + "                <tns:TaskId>A8642FCA-5561-49DD-A278-8D872FE9BD01</tns:TaskId>\n"
        + "                <tns:TaskNumber>2022-06-172333</tns:TaskNumber>\n"
        + "                <tns:TaskDate>2022-06-17T10:37:27+03:00</tns:TaskDate>\n"
        + "                <tns:Responsible>\n"
        + "                    <tns:LastName>Петров</tns:LastName>\n"
        + "                    <tns:FirstName>Петр</tns:FirstName>\n"
        + "                    <tns:MiddleName>Петрович</tns:MiddleName>\n"
        + "                    <tns:JobTitle>Советнмк</tns:JobTitle>\n"
        + "                </tns:Responsible>\n"
        + "                <tns:Department>\n"
        + "                    <tns:Name>Департамент городского имущества города Москвы</tns:Name>\n"
        + "                    <tns:Code>446</tns:Code>\n"
        + "                    <tns:Inn>7705031674</tns:Inn>\n"
        + "                    <tns:Ogrn>1037739510423</tns:Ogrn>\n"
        + "                    <tns:RegDate>1991-11-15T00:00:00</tns:RegDate>\n"
        + "                    <tns:SystemCode>1000874</tns:SystemCode>\n"
        + "                </tns:Department>\n"
        + "                <tns:ServiceNumber>0446-1000874-880087-0000743/22</tns:ServiceNumber>\n"
        + "                <tns:ServiceTypeCode xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
        + "            </tns:Task>\n"
        + "            <tns:Data>\n"
        + "                <tns:DocumentTypeCode>555</tns:DocumentTypeCode>\n"
        + "                <tns:ParameterTypeCode>666</tns:ParameterTypeCode>\n"
        + "                <tns:Parameter>\n"
        + "                    <SuperServiceDGPContractsPrReady>\n"
        + "                        <SuperServiceDGPContractPrReady>\n"
        + "                            <person_id>6099520</person_id>\n"
        + "                            <affair_id>720875</affair_id>\n"
        + "                            <order_id>2216815</order_id>\n"
        + "                            <pdfContract>{6E044E4C-4E03-426F-B1A5-158583D90143}</pdfContract>\n"
        + "                            <pdfProposal></pdfProposal>\n"
        + "                            <contractStatus>2</contractStatus>\n"
        + "                            <contractType>39</contractType>\n"
        + "                            <contractNumber>5733071785</contractNumber>\n"
        + "                            <contractDateEnd></contractDateEnd>\n"
        + "                            <letter_id>7244516</letter_id>\n"
        + "                        </SuperServiceDGPContractPrReady>\n"
        + "                    </SuperServiceDGPContractsPrReady>\n"
        + "                </tns:Parameter>\n"
        + "                <tns:IncludeXmlView>false</tns:IncludeXmlView>\n"
        + "                <tns:IncludeBinaryView>false</tns:IncludeBinaryView>\n"
        + "            </tns:Data>\n"
        + "        </tns:CoordinateTaskData>\n"
        + "    </tns:CoordinateTaskDataMessages>\n"
        + "</tns:SendTasksMessage>";

    private static final String TEST_CONTRACT_PR_READY_MESSAGE_TYPE = "UGD_CONTRACT_PR_READY_INC";

    public static final String CONTRACT_READY_TEST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
        + "<tns:SendTasksMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\">\n"
        + "    <tns:CoordinateTaskDataMessages>\n"
        + "        <tns:CoordinateTaskData wsu:Id=\"ID_1\">\n"
        + "            <tns:Task>\n"
        + "                <tns:MessageId>66726110-8CBC-4D77-91D2-FDA5EE34A35D</tns:MessageId>\n"
        + "                <tns:TaskId>FC2E42ED-6C74-4EDB-8DBD-E786C0B435AE</tns:TaskId>\n"
        + "                <tns:TaskNumber>2022-06-172694</tns:TaskNumber>\n"
        + "                <tns:TaskDate>2022-06-17T10:36:52+03:00</tns:TaskDate>\n"
        + "                <tns:Responsible>\n"
        + "                    <tns:LastName>Петров</tns:LastName>\n"
        + "                    <tns:FirstName>Петр</tns:FirstName>\n"
        + "                    <tns:MiddleName>Петрович</tns:MiddleName>\n"
        + "                    <tns:JobTitle>Советнмк</tns:JobTitle>\n"
        + "                </tns:Responsible>\n"
        + "                <tns:Department>\n"
        + "                    <tns:Name>Департамент городского имущества города Москвы</tns:Name>\n"
        + "                    <tns:Code>446</tns:Code>\n"
        + "                    <tns:Inn>7705031674</tns:Inn>\n"
        + "                    <tns:Ogrn>1037739510423</tns:Ogrn>\n"
        + "                    <tns:RegDate>1991-11-15T00:00:00</tns:RegDate>\n"
        + "                    <tns:SystemCode>1000874</tns:SystemCode>\n"
        + "                </tns:Department>\n"
        + "                <tns:ServiceNumber>0446-1000874-880045-0000742/22</tns:ServiceNumber>\n"
        + "                <tns:ServiceTypeCode xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
        + "            </tns:Task>\n"
        + "            <tns:Data>\n"
        + "                <tns:DocumentTypeCode>555</tns:DocumentTypeCode>\n"
        + "                <tns:ParameterTypeCode>666</tns:ParameterTypeCode>\n"
        + "                <tns:Parameter>\n"
        + "                    <SuperServiceDGPContractsReady>\n"
        + "                        <SuperServiceDGPContractReady>\n"
        + "                            <person_id>6619865</person_id>\n"
        + "                            <affair_id>933499</affair_id>\n"
        + "                            <order_id>2300385</order_id>\n"
        + "                            <RTFContractToSign>{4608ed01-29f4-4363-8bc2-055c2e5501fe}</RTFContractToSign>\n"
        + "                            <contractStatus>2</contractStatus>\n"
        + "                            <contractType>39</contractType>\n"
        + "                            <contractNumber>5733071906</contractNumber>\n"
        + "                            <contractDateEnd></contractDateEnd>\n"
        + "                            <RTFActToSign>{4a0a9e1d-70f1-4335-88de-d16fc4f4555d}</RTFActToSign>\n"
        + "                            <letter_id>14909418</letter_id>\n"
        + "                        </SuperServiceDGPContractReady>\n"
        + "                    </SuperServiceDGPContractsReady>\n"
        + "                </tns:Parameter>\n"
        + "                <tns:IncludeXmlView>false</tns:IncludeXmlView>\n"
        + "                <tns:IncludeBinaryView>false</tns:IncludeBinaryView>\n"
        + "            </tns:Data>\n"
        + "        </tns:CoordinateTaskData>\n"
        + "    </tns:CoordinateTaskDataMessages>\n"
        + "</tns:SendTasksMessage>";

    private static final String TEST_CONTRACT_READY_MESSAGE_TYPE = "UGD_CONTRACT_READY_INC";
}
