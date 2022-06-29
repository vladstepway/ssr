package ru.croc.ugd.ssr.controller.notary;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.mq.listener.notary.NotaryApplicationRequestListener;

/**
 * Контроллер для работы с заявлениями на посещение нотариуса.
 */
@RestController
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.notary-application.enabled")
public class TestNotaryApplicationController {

    private final NotaryApplicationRequestListener notaryApplicationRequestListener;

    @ApiOperation(value = "Incoming notary application event from MPGU")
    @PostMapping(value = "/mpgu/notary-application-event")
    public void mpguCoordinateMessage(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        notaryApplicationRequestListener.handleCoordinateMessage(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_MESSAGE)
            )
        );
    }

    @ApiOperation(value = "Incoming notary application status event from MPGU")
    @PostMapping(value = "/mpgu/notary-application-status-event")
    public void mpguCoordinateStatusMessage(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        notaryApplicationRequestListener.handleCoordinateStatusMessage(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_STATUS_MESSAGE)
            )
        );
    }

    private static final String TEST_STATUS_MESSAGE =
        "<CoordinateStatusMessage xmlns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
            + "  <CoordinateStatusDataMessage>\n"
            + "    <Status>\n"
            + "      <StatusCode>8011</StatusCode>\n"
            + "      <StatusTitle>Документы от заявителя переданы в ведомство</StatusTitle>\n"
            + "      <StatusDate>2021-05-13T10:05:01+03:00</StatusDate>\n"
            + "    </Status>\n"
            + "    <Reason>\n"
            + "      <Code>1</Code>\n"
            + "    </Reason>\n"
            + "    <Responsible xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "    <ServiceNumber>8899-9300120-0840-10000014/21</ServiceNumber>\n"
            + "    <Department xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "    <StatusId>8011.1</StatusId>\n"
            + "  </CoordinateStatusDataMessage>\n"
            + "</CoordinateStatusMessage>";

    private static final String TEST_MESSAGE = "<v61:CoordinateMessage xmlns:v61=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
        + "  <v61:CoordinateDataMessage>\n"
        + "    <v61:Service>\n"
        + "      <v61:RegDate>2021-04-11T10:05:01+03:00</v61:RegDate>\n"
        + "      <v61:ServiceNumber>8899-9300120-0840-10000014/21</v61:ServiceNumber>\n"
        + "    </v61:Service>\n"
        + "    <v61:SignService Id=\"9f1f062b-a9c6-4a58-8cd5-ed841d6cc933\">\n"
        + "      <v61:Contacts>\n"
        + "        <v61:BaseDeclarant Id=\"declarant\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"v61:RequestContact\">\n"
        + "          <v61:Type>Declarant</v61:Type>\n"
        + "          <v61:Snils>480-953-512 08</v61:Snils>\n"
        + "          <v61:MobilePhone>9051231111</v61:MobilePhone>\n"
        + "          <v61:EMail>test@commission.inspection</v61:EMail>\n"
        + "          <v61:LastName>LastName1</v61:LastName>\n"
        + "          <v61:FirstName>FirstName1</v61:FirstName>\n"
        + "          <v61:MiddleName>MiddleName1</v61:MiddleName>\n"
        + "        </v61:BaseDeclarant>\n"
        + "      </v61:Contacts>\n"
        + "      <v61:CustomAttributes>\n"
        + "        <EtpCreateNotaryApplication>          \n"
        + "          <notaryId>679c28a0-30c0-4059-ab28-4508a95130ba</notaryId>\n"
        + "          <comment>Тестовый коментарий</comment>\n"
        + "          <affairId>866358</affairId>\n"
        + "          <additionalPhone>+375158952321</additionalPhone>\n"
        + "        <apartmentFrom>\n"
        + "          <address>addressFrom1</address>\n"
        + "          <unom>unomFrom1</unom>\n"
        + "          <flatNumber>flatNumberFrom1</flatNumber>\n"
        + "          <roomNum>roomNumFrom1</roomNum>\n"
        + "          <roomNum>roomNumFrom2</roomNum>\n"
        + "        </apartmentFrom>\n"
        + "        <apartmentTo>\n"
        + "          <apartment>\n"
        + "            <address>addressTo1</address>\n"
        + "            <unom>unomTo1</unom>\n"
        + "            <flatNumber>flatNumberTo1</flatNumber>\n"
        + "            <roomNum>roomNumTo1</roomNum>\n"
        + "          </apartment>\n"
        + "          <apartment>\n"
        + "            <address>addressTo2</address>\n"
        + "            <unom>unomTo2</unom>\n"
        + "            <flatNumber>flatNumberTo2</flatNumber>\n"
        + "            <roomNum>roomNumTo2</roomNum>\n"
        + "          </apartment>\n"
        + "        </apartmentTo>\n"
        + "        </EtpCreateNotaryApplication>\n"
        + "      </v61:CustomAttributes>\n"
        + "    </v61:SignService>\n"
        + "  </v61:CoordinateDataMessage>\n"
        + "</v61:CoordinateMessage>";
}
