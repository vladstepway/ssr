package ru.croc.ugd.ssr.flows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.jms.JMSTextMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStartRemovalType;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;

public class IntegrationInfoBeginningResettlementTest extends AbstractFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private QueueProperties queueProperties;

    @Test
    public void testSendController() throws Exception {

        SuperServiceDGPStartRemovalType.SettlementHouses settlementHouses =
                new SuperServiceDGPStartRemovalType.SettlementHouses();
        List<SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse> list =
                settlementHouses.getSettlementHouse();
        SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse house =
                new SuperServiceDGPStartRemovalType.SettlementHouses.SettlementHouse();
        house.setNewCadnum("1");
        house.setNewUnom("1");
        list.add(house);

        SuperServiceDGPStartRemovalType.Letter letter = new SuperServiceDGPStartRemovalType.Letter();
        letter.setLetterDate(LocalDate.now());
        letter.setLetterNum(UUID.randomUUID().toString());

        SuperServiceDGPStartRemovalType dgpStartRemovalType = new SuperServiceDGPStartRemovalType();
        dgpStartRemovalType.setLetter(letter);
        dgpStartRemovalType.setSettlementHouses(settlementHouses);

        final String dgpStartRemovalTypeJson = objectMapper.writeValueAsString(dgpStartRemovalType);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/ugd/ssr/sendInfoBeginningResettlement")
                .contextPath("/app/ugd/ssr")
                .content(dgpStartRemovalTypeJson)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful());

        waitAtMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(jmsMessagingTemplate.getJmsTemplate().receive(queueProperties.getPersonBuildingsRequest()))
                    .satisfies(message -> {
                        try {
                            assertThat(((JMSTextMessage) message).getText()).isInstanceOf(String.class);
                        } catch (JMSException e) {
                            throw new RuntimeException("error");
                        }
                    });
        });
    }

    private String constnat;

    {
        final String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        constnat = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:CoordinateTaskMessage xmlns:ns2=\"http://asguf.mos"
                + ".ru/rkis_gu/coordinate/v6_1/\">\n"
                + "    <ns2:CoordinateTaskDataMessage>\n"
                + "        <ns2:Task>\n"
                + "            <ns2:MessageId>5020705b-4b22-4c52-b0ca-29b218483e2d</ns2:MessageId>\n"
                + "            <ns2:TaskId>ec8ea85a-c9e0-4304-937e-74afdb6c1561</ns2:TaskId>\n"
                + "            <ns2:TaskNumber>2591-9000154-880019-000447/20</ns2:TaskNumber>\n"
                + "            <ns2:TaskDate>2020-08-25T18:42:51.597+03:00</ns2:TaskDate>\n"
                + "            <ns2:Responsible>\n"
                + "                <ns2:LastName>Гнездилов</ns2:LastName>\n"
                + "                <ns2:FirstName>Роман</ns2:FirstName>\n"
                + "                <ns2:JobTitle>Советник отдела информационной безопасности</ns2:JobTitle>\n"
                + "            </ns2:Responsible>\n"
                + "            <ns2:Department>\n"
                + "                <ns2:Name>Департамент градостроительной политики г. Москвы</ns2:Name>\n"
                + "                <ns2:Code>2591</ns2:Code>\n"
                + "                <ns2:Inn>7703742961</ns2:Inn>\n"
                + "                <ns2:Ogrn>1117746321219</ns2:Ogrn>\n"
                + "                <ns2:RegDate>2020-08-25T18:42:51.337+03:00</ns2:RegDate>\n"
                + "                <ns2:SystemCode>9000154</ns2:SystemCode>\n"
                + "            </ns2:Department>\n"
                + "            <ns2:ServiceNumber xsi:nil=\"true\" xmlns:xsi=\"http://www.w3"
                + ".org/2001/XMLSchema-instance\"/>\n"
                + "            <ns2:ServiceTypeCode xsi:nil=\"true\" xmlns:xsi=\"http://www.w3"
                + ".org/2001/XMLSchema-instance\"/>\n"
                + "            <ns2:FunctionTypeCode>067801</ns2:FunctionTypeCode>\n"
                + "        </ns2:Task>\n"
                + "        <ns2:Data>\n"
                + "            <ns2:DocumentTypeCode>1</ns2:DocumentTypeCode>\n"
                + "            <ns2:Parameter>\n"
                + "                <SuperServiceDGPStartRemoval>\n"
                + "                    <ResettlementHouses>\n"
                + "                        <ResettlementHouse>\n"
                + "                            <snos_unom>1</snos_unom>\n"
                + "                            <snos_cadnum>1</snos_cadnum>\n"
                + "                        </ResettlementHouse>\n"
                + "                    </ResettlementHouses>\n"
                + "                    <Letters>\n"
                + "                        <Letter>\n"
                + "                            <letter_id>43a8ce88-e59c-4bb7-954c-53a1e6589854</letter_id>\n"
                + "                            <LetterDate>"
                + date
                + "</LetterDate>\n"
                + "                        </Letter>\n"
                + "                    </Letters>\n"
                + "                </SuperServiceDGPStartRemoval>\n"
                + "            </ns2:Parameter>\n"
                + "            <ns2:IncludeXmlView>false</ns2:IncludeXmlView>\n"
                + "            <ns2:IncludeBinaryView>false</ns2:IncludeBinaryView>\n"
                + "        </ns2:Data>\n"
                + "    </ns2:CoordinateTaskDataMessage>\n"
                + "</ns2:CoordinateTaskMessage>";
    }
}
