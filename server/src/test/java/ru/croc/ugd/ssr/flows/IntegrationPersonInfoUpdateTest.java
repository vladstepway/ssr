package ru.croc.ugd.ssr.flows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.IbmMqContainer;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.TestPostgresqlContainer;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class IntegrationPersonInfoUpdateTest extends AbstractFlowTest {
    @ClassRule
    public static IbmMqContainer ibmMqContainer = IbmMqContainer.getInstance();

    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = TestPostgresqlContainer.getInstance();


    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private QueueProperties queueProperties;

    @Autowired
    private RealEstateDocumentService realEstateDocumentService;

    @Autowired
    private PersonDocumentService personDocumentService;

    @Autowired
    private RiAuthenticationUtils riAuthenticationUtils;

    @Before
    public void init() {
        riAuthenticationUtils.setSecurityContextByServiceuser();

        RealEstateDocument realEstateDocument = new RealEstateDocument();
        RealEstate realEstate = new RealEstate();
        RealEstateDataType realEstateDataType = new RealEstateDataType();
        RealEstateDataType.Flats flats = new RealEstateDataType.Flats();
        FlatType flatType = new FlatType();
        flatType.setFlatNumber("48");
        flatType.setFlatID("flatId");
        flats.getFlat().add(flatType);
        realEstateDataType.setFlats(flats);
        realEstateDataType.setUNOM(new BigInteger("29825"));
        realEstate.setRealEstateData(realEstateDataType);
        realEstateDocument.setDocument(realEstate);

        realEstateDocumentService.createDocument(realEstateDocument, false, "test");

        PersonDocument personDocument = new PersonDocument();
        Person person = new Person();
        PersonType personType = new PersonType();
        personType.setUNOM(new BigInteger("29825"));
        personType.setSNILS("064-655-164 75");
        personType.setFlatID("flatId");
        person.setPersonData(personType);
        personDocument.setDocument(person);

        personDocumentService.createDocument(personDocument, false, "test");

    }

    @Test
    public void testSendController() throws Exception {
        jmsMessagingTemplate.getJmsTemplate().convertAndSend("RENOVATION.UGD.PERSONS_RES", constnat);
        waitAtMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(personDocumentService.fetchByUnom("29825")).hasSize(1).satisfies(documents -> {
                PersonType personDocument = documents.get(0).getDocument().getPersonData();
                assertThat(personDocument.getLastName()).isEqualTo("Марков");
                assertThat(personDocument.getAffairId()).isEqualTo("914835");
                assertThat(personDocument.getBirthDate()).isEqualTo("1963-06-02");
            });
        });
    }


    private String constnat;

    {
        final String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        constnat = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                " <tns:CoordinateSendTaskStatusesMessage xsi:schemaLocation=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/ServiceV6_1_8.xsd\" xmlns:tns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> \n" +
                " <tns:CoordinateTaskStatusDataMessage> \n" +
                " <tns:MessageId>AD3BBCC6-2F3B-83E7-E050-400A7E795E6C</tns:MessageId> \n" +
                " <tns:TaskId>44ed1114-dcc0-3c6a-aef7-ce0bedd95fb8</tns:TaskId> \n" +
                " <tns:Status> \n" +
                " <tns:StatusCode>0</tns:StatusCode> \n" +
                " <tns:StatusDate>2020-08-19T16:11:10+03:00</tns:StatusDate> \n" +
                " </tns:Status> \n" +
                " <tns:Result> \n" +
                " <tns:ResultType>PositiveAnswer</tns:ResultType> \n" +
                " <tns:ResultCode>0</tns:ResultCode> \n" +
                " <tns:XmlView> \n" +
                "<SuperServiceDGPPersonsMessage>\n" +
                "<SuperServiceDGPPersonsResult>\n" +
                "<person_id>2018106</person_id>\n" +
                "<actual_date></actual_date>\n" +
                "<snils>064-655-164 75</snils>\n" +
                "<lastname>Марков</lastname>\n" +
                "<firstname>Василий</firstname>\n" +
                "<middlename>Анатольевич</middlename>\n" +
                "<birthdate>02.06.1963</birthdate>\n" +
                "<sex>Мужской</sex>\n" +
                "<IsQueue>Нет</IsQueue>\n" +
                "<IsDead></IsDead>\n" +
                "<ChangeStatus>Начальная загрузка </ChangeStatus>\n" +
                "<delReason></delReason>\n" +
                "<LinkedFlats>\n" +
                "<Flat>\n" +
                "<affair_id>914835</affair_id>\n" +
                "<statusLiving>Пользователь</statusLiving>\n" +
                "<encumbrances>Нет данных</encumbrances>\n" +
                "<snos_unom>29825</snos_unom>\n" +
                "<snos_cadnum>77:04:0002016:1069</snos_cadnum>\n" +
                "<snos_flat_num>48</snos_flat_num>\n" +
                "<snos_rooms_num></snos_rooms_num>\n" +
                "<noFlat> </noFlat>\n" +
                "<isFederal></isFederal>\n" +
                "<inCourt>Нет</inCourt>\n" +
                " </Flat>\n" +
                "</LinkedFlats>\n" +
                "</SuperServiceDGPPersonsResult>\n" +
                "</SuperServiceDGPPersonsMessage>\n" +
                " </tns:XmlView>\n" +
                "</tns:Result>\n" +
                "</tns:CoordinateTaskStatusDataMessage>\n" +
                "</tns:CoordinateSendTaskStatusesMessage>";
    }

}
