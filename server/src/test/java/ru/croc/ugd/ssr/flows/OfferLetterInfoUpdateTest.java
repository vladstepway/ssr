package ru.croc.ugd.ssr.flows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.controller.DashboardController;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class OfferLetterInfoUpdateTest extends AbstractFlowTest {

    public static final String AFFAIR_ID = "914963";
    public static final String PERSON_ID = "6579259";
    @Autowired
    private RiAuthenticationUtils riAuthenticationUtils;

    @Autowired
    private PersonDocumentService personDocumentService;

    @Autowired
    private MqSender sender;

    @Autowired
    private QueueProperties queueProperties;

    @Before
    public void init(){
        riAuthenticationUtils.setSecurityContextByServiceuser();
        PersonDocument personDocument = new PersonDocument();
        Person person = new Person();
        PersonType personType = new PersonType();
        personType.setUNOM(new BigInteger("29825"));
        personType.setAffairId(AFFAIR_ID);
        personType.setPersonID(PERSON_ID);
        personType.setFlatID("flatId");
        person.setPersonData(personType);
        personDocument.setDocument(person);

        personDocumentService.createDocument(personDocument, false, "test");

    }

    @Test
    public void testUpdateInfo(){
        sender.send("RENOVATION.UGD.LETTERS_REQ", constant);
        waitAtMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(personDocumentService.fetchByPersonIdAndAffairId(PERSON_ID, AFFAIR_ID))
                    .hasSize(1).satisfies(documents -> {
                PersonType personDocument = documents.get(0).getDocument().getPersonData();
                assertThat(personDocument.getOfferLetters()).isNotNull();
            });
        });
    }

    final String constant ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<tns:SendTasksMessage xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\"\n" +
            "                      xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"\n" +
            "                      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                      xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ ServiceV6_1_8.xsd\">\n" +
            "    <tns:CoordinateTaskDataMessages>\n" +
            "        <tns:CoordinateTaskData wsu:Id=\"ID_1\">\n" +
            "            <tns:Task>\n" +
            "                <tns:MessageId>AD3C5182-C90D-A6D8-E050-400A7E796DB1</tns:MessageId>\n" +
            "                <tns:TaskId>AD3C5182-C912-A6D8-E050-400A7E796DB1</tns:TaskId>\n" +
            "                <tns:TaskNumber>2020-08-198971</tns:TaskNumber>\n" +
            "                <tns:TaskDate>2020-08-19T16:52:45+03:00</tns:TaskDate>\n" +
            "                <tns:Responsible>\n" +
            "                    <tns:LastName>Петров</tns:LastName>\n" +
            "                    <tns:FirstName>Петр</tns:FirstName>\n" +
            "                    <tns:MiddleName>String</tns:MiddleName>\n" +
            "                    <tns:JobTitle>Директор</tns:JobTitle>\n" +
            "                    <tns:Phone>123</tns:Phone>\n" +
            "                    <tns:Email>13@123</tns:Email>\n" +
            "                    <tns:IsiId>1</tns:IsiId>\n" +
            "                    <tns:SsoId>2</tns:SsoId>\n" +
            "                    <tns:MdmId>3</tns:MdmId>\n" +
            "                </tns:Responsible>\n" +
            "                <tns:Department>\n" +
            "                    <tns:Name>ДГП</tns:Name>\n" +
            "                    <tns:Code>String</tns:Code>\n" +
            "                    <tns:Inn>String</tns:Inn>\n" +
            "                    <tns:Ogrn>String</tns:Ogrn>\n" +
            "                    <tns:RegDate>2012-12-17T09:30:47Z</tns:RegDate>\n" +
            "                    <tns:SystemCode>90154</tns:SystemCode>\n" +
            "                </tns:Department>\n" +
            "                <tns:ServiceNumber>2591-9000154-067801-0000600/20</tns:ServiceNumber>\n" +
            "                <tns:ServiceTypeCode>7777777</tns:ServiceTypeCode>\n" +
            "            </tns:Task>\n" +
            "            <tns:Data>\n" +
            "                <tns:DocumentTypeCode>555</tns:DocumentTypeCode>\n" +
            "                <tns:ParameterTypeCode>666</tns:ParameterTypeCode>\n" +
            "                <tns:Parameter>\n" +
            "                    <SuperServiceDGPLetterRequest>\n" +
            "                        <person_id>6579259</person_id>\n" +
            "                        <affair_id>914963</affair_id>\n" +
            "                        <idCIP></idCIP>\n" +
            "                        <pdfProposal></pdfProposal>\n" +
            "                        <pdfBlank></pdfBlank>\n" +
            "                        <letter_id>8678558</letter_id>\n" +
            "                    </SuperServiceDGPLetterRequest>\n" +
            "                    <SuperServiceDGPLetterRequest>\n" +
            "                        <person_id>6579251</person_id>\n" +
            "                        <affair_id>914961</affair_id>\n" +
            "                        <idCIP></idCIP>\n" +
            "                        <pdfProposal></pdfProposal>\n" +
            "                        <pdfBlank></pdfBlank>\n" +
            "                        <letter_id>8678551</letter_id>\n" +
            "                    </SuperServiceDGPLetterRequest>\n" +
            "                </tns:Parameter>\n" +
            "                <tns:IncludeXmlView>false</tns:IncludeXmlView>\n" +
            "                <tns:IncludeBinaryView>false</tns:IncludeBinaryView>\n" +
            "            </tns:Data>\n" +
            "            <tns:Signature>text</tns:Signature>\n" +
            "        </tns:CoordinateTaskData>\n" +
            "    </tns:CoordinateTaskDataMessages>\n" +
            "</tns:SendTasksMessage>";
}
