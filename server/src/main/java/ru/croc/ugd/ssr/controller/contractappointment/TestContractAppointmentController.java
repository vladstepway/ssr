package ru.croc.ugd.ssr.controller.contractappointment;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.mq.listener.contractappointment.ContractAppointmentRequestListener;

/**
 * Контроллер для работы с заявлениями на запись на подписание договора.
 */
@RestController
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.contract-appointment.enabled")
public class TestContractAppointmentController {

    private final ContractAppointmentRequestListener contractAppointmentRequestListener;

    @ApiOperation(value = "Incoming contract appointment event from MPGU")
    @PostMapping(value = "/mpgu/contract-appointment-event")
    public void mpguCoordinateMessage(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        contractAppointmentRequestListener.handleCoordinateMessage(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_MESSAGE)
            )
        );
    }

    @ApiOperation(value = "Incoming contract appointment status event from MPGU")
    @PostMapping(value = "/mpgu/contract-appointment-status-event")
    public void mpguCoordinateStatusMessage(
        @ApiParam(value = "Message") @RequestBody(required = false) String message
    ) {
        contractAppointmentRequestListener.handleCoordinateStatusMessage(
            new GenericMessage<>(message)
        );
    }

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
        + "          <v61:EMail>test@contract.appointment</v61:EMail>\n"
        + "          <v61:LastName>LastName1</v61:LastName>\n"
        + "          <v61:FirstName>FirstName1</v61:FirstName>\n"
        + "          <v61:MiddleName>MiddleName1</v61:MiddleName>\n"
        + "        </v61:BaseDeclarant>\n"
        + "      </v61:Contacts>\n"
        + "      <v61:CustomAttributes>\n"
        + "        <NS1:ServiceProperties xmlns:NS1=\"http://mos.ru/gu/service/086601/\">\n"
        + "          <NS1:CipId>387ea0f4-78d1-40a7-b7ac-c3b9b6253354</NS1:CipId>\n"
        + "          <NS1:Address>Test Address</NS1:Address>\n"
        + "          <NS1:ContractOrderId>1</NS1:ContractOrderId>\n"
        + "          <NS1:PersonDocumentId>0a37d20a-0307-ed5f-ad59-096a03d2992e</NS1:PersonDocumentId>\n"
        + "          <NS1:BookingId>387ea0f4-78d1-40a7-b7ac-c3b9b6253355</NS1:BookingId>\n"
        + "          <NS1:AppointmentDateTime>2021-08-11T10:05:01+03:00</NS1:AppointmentDateTime>\n"
        + "          <NS1:AdditionalPhone>+375158952321</NS1:AdditionalPhone>\n"
        + "        </NS1:ServiceProperties>\n"
        + "      </v61:CustomAttributes>\n"
        + "    </v61:SignService>\n"
        + "  </v61:CoordinateDataMessage>\n"
        + "</v61:CoordinateMessage>";
}
