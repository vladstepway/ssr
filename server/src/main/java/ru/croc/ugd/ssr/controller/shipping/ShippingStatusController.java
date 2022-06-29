package ru.croc.ugd.ssr.controller.shipping;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.EdpResponseStatusDto;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.integration.command.PublishReasonCommand;
import ru.croc.ugd.ssr.integration.service.ShippingEventPublisher;
import ru.croc.ugd.ssr.mapper.mq.CoordinateMessageToBookingInformationMapper;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.shipping.ServiceProperties;
import ru.croc.ugd.ssr.mq.listener.ShippingRequestsListener;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

@RestController
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.shipping.enabled")
public class ShippingStatusController {
    private final ShippingEventPublisher shippingEventPublisher;
    private final ShippingRequestsListener shippingRequestsListener;
    private final CoordinateMessageToBookingInformationMapper
        coordinateMessageToBookingInformationMapper;

    @ApiOperation(value = "1050")
    @PostMapping(value = "/shipping-status/1050")
    public void send1050(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false)
        @RequestBody String message,
        @ApiParam(value = "Move date and time", required = false)
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime,
        @ApiParam(value = "Address from", required = false)
        @RequestParam String addressFrom,
        @ApiParam(value = "Address to", required = false)
        @RequestParam String addressTo
    ) {
        publishMessage(ShippingFlowStatus.RECORD_ADDED, eno, message, moveDateTime, addressFrom, addressTo);
    }

    @ApiOperation(value = "10090")
    @PostMapping(value = "/shipping-status/10090")
    public void send10090(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.RECALL_AVAILABLE, eno, message);
    }

    @ApiOperation(value = "8021.1")
    @PostMapping(value = "/shipping-status/8021.1")
    public void send80211(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false)
        @RequestBody String message,
        @ApiParam(value = "Move date and time", required = false)
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime
    ) {
        publishMessage(ShippingFlowStatus.RECORD_RESCHEDULED, eno, message, moveDateTime);
    }

    @ApiOperation(value = "103099")
    @PostMapping(value = "/shipping-status/103099")
    public void send103099(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.TECHNICAL_CRASH_SEND, eno, message);
    }

    @ApiOperation(value = "1069")
    @PostMapping(value = "/shipping-status/1069")
    public void send1069(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message") @RequestBody String message
    ) {
        shippingRequestsListener.receiveShippingStatuses(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_STATUS_MESSAGE)
            )
        );
    }

    @ApiOperation(value = "106999")
    @PostMapping(value = "/shipping-status/106999")
    public void send106999(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.TECHNICAL_CRASH_RECALL, eno, message);
    }

    @ApiOperation(value = "1080.1")
    @PostMapping(value = "/shipping-status/1080.1")
    public void send10801(
        @PathVariable(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_DECLINED_BY_PREFECTURE, eno, message);
    }

    @ApiOperation(value = "1080.2")
    @PostMapping(value = "/shipping-status/1080.2")
    public void send10802(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_REJECTED, eno, message);
    }

    @ApiOperation(value = "8021.2")
    @PostMapping(value = "/shipping-status/8021.2")
    public void send80212(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false)
        @RequestBody String message,
        @ApiParam(value = "Move date and time", required = false)
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime moveDateTime
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_REMINDING, eno, message);
    }

    @ApiOperation(value = "10190")
    @PostMapping(value = "/shipping-status/10190")
    public void send10190(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.RECALL_NOT_POSSIBLE, eno, message);
    }

    @ApiOperation(value = "1075")
    @PostMapping(value = "/shipping-status/1075")
    public void send1075(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_COMPLETE, eno, message);
    }

    @ApiOperation(value = "1080")
    @PostMapping(value = "/shipping-status/1080")
    public void send1080(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_REJECTED, eno, message);
    }

    @ApiOperation(value = "1090")
    @PostMapping(value = "/shipping-status/1090")
    public void send1090(
        @RequestParam(required = false) String eno,
        @ApiParam(value = "Message", required = false) @RequestBody String message
    ) {
        publishMessage(ShippingFlowStatus.SHIPPING_DECLINED, eno, message);
    }

    private void publishMessage(
        ShippingFlowStatus status,
        String eno,
        String message
    ) {
        publishMessage(status, eno, message, null);
    }

    private void publishMessage(
        ShippingFlowStatus status,
        String eno,
        String message,
        LocalDateTime moveDateTime
    ) {
        publishMessage(status, eno, message, moveDateTime, null, null);
    }

    private void publishMessage(
        ShippingFlowStatus status,
        String eno,
        String message,
        LocalDateTime moveDateTime,
        String addressFrom,
        String addressTo
    ) {
        if (message == null || !message.contains("CoordinateMessage")) {
            message = TEST_MESSAGE;
        }
        final CoordinateMessage parsedShippingRequest = parseReceiveMessage(message);

        final BookingInformation bookingInformation =
            coordinateMessageToBookingInformationMapper.toBookingInformation(
                parsedShippingRequest);
        final String enoToSend = StringUtils.isBlank(eno)
            ? bookingInformation.getEnoServiceNumber()
            : eno;

        bookingInformation.setEnoServiceNumber(enoToSend);
        String statusText = prepareStatusText(status, enoToSend, moveDateTime, addressFrom, addressTo);

        final PublishReasonCommand publishReasonCommand2 = PublishReasonCommand.builder()
            .bookingInformation(bookingInformation)
            .responseReasonDate(ZonedDateTime.now())
            .edpResponseStatusData(EdpResponseStatusDto.builder()
                .shippingFlowStatus(status)
                .edpResponseStatusText(statusText)
                .build())
            .build();
        shippingEventPublisher.publishCurrentShippingStatus(publishReasonCommand2);

    }

    private String prepareStatusText(
        ShippingFlowStatus status, String eno, LocalDateTime moveDateTime, String addressFrom, String addressTo
    ) {
        LocalDateTime moveDateTimeNonNull = ofNullable(moveDateTime).orElse(LocalDateTime.now());

        String moveDate = moveDateTimeNonNull.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String moveTime = moveDateTimeNonNull.format(DateTimeFormatter.ofPattern("hh:mm"));

        LocalDateTime cancelLocalDate = moveDateTimeNonNull.minusDays(1);

        String cancelDate = cancelLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String cancelTime = cancelLocalDate.format(DateTimeFormatter.ofPattern("hh:mm"));

        switch (status) {
            case SHIPPING_DECLINED:
            case SHIPPING_DECLINED_BY_PREFECTURE:
                return String.format(status.getStatusText(), eno);
            case RECORD_RESCHEDULED:
                return String.format(status.getStatusText(), moveDate, moveTime, cancelDate, eno);
            case RECORD_ADDED:
                return String.format(status.getStatusText(), addressFrom, addressTo, moveDate, moveTime, cancelTime,
                    cancelDate);
            case SHIPPING_REMINDING:
                return String.format(status.getStatusText(), moveDate, moveTime, cancelDate);
            default:
                return "";
        }
    }

    public CoordinateMessage parseReceiveMessage(String message) { // TODO generic implementation to abstract listener.
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(CoordinateMessage.class, ServiceProperties.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (CoordinateMessage) JAXBIntrospector.getValue(unmarshaller.unmarshal(new StringReader(
                message)));
        } catch (JAXBException e) {
            new RuntimeException(e); // TODO replace with well-named exception.
        }
        return null;
    }


    private static final String TEST_MESSAGE = "<v61:CoordinateMessage xmlns:v61=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
        + " <v61:CoordinateDataMessage>\n"
        + "  <v61:Service>\n"
        + "   <v61:RegNum>1900000780</v61:RegNum>\n"
        + "   <v61:RegDate>2019-09-13T10:05:01+03:00</v61:RegDate>\n"
        + "   <v61:ServiceNumber>8899-9300120-067801-00000309/20</v61:ServiceNumber>\n"
        + "   <v61:Responsible>\n"
        + "    <v61:LastName>оператор Портала</v61:LastName>\n"
        + "    <v61:FirstName>оператор Портала</v61:FirstName>\n"
        + "    <v61:MiddleName>оператор Портала</v61:MiddleName>\n"
        + "    <v61:JobTitle>оператор Портала</v61:JobTitle>\n"
        + "    <v61:Phone>+7 (495) 539-55-55</v61:Phone>\n"
        + "    <v61:Email>cpgu@mos.ru</v61:Email>\n"
        + "   </v61:Responsible>\n"
        + "   <v61:Department>\n"
        + "    <v61:Name>Департамент информационных технологий города Москвы</v61:Name>\n"
        + "    <v61:Code>2043</v61:Code>\n"
        + "    <v61:Inn>7710878000</v61:Inn>\n"
        + "    <v61:Ogrn>1107746943347</v61:Ogrn>\n"
        + "    <v61:RegDate>2019-09-12T00:00:00</v61:RegDate>\n"
        + "    <v61:SystemCode>9300003</v61:SystemCode>\n"
        + "   </v61:Department>\n"
        + "   <v61:CreatedByDepartment>\n"
        + "    <v61:Name>ПГУ</v61:Name>\n"
        + "    <v61:Code>1</v61:Code>\n"
        + "    <v61:Inn>7710878000</v61:Inn>\n"
        + "    <v61:Ogrn>1107746943347</v61:Ogrn>\n"
        + "    <v61:RegDate>2019-09-12T00:00:00</v61:RegDate>\n"
        + "    <v61:SystemCode>1</v61:SystemCode>\n"
        + "   </v61:CreatedByDepartment>\n"
        + "   <v61:OutputKind>Portal</v61:OutputKind>\n"
        + "   <v61:PortalNum>156178271</v61:PortalNum>\n"
        + "  </v61:Service>\n"
        + "  <v61:SignService Id=\"9f1f062b-a9c6-4a58-8cd5-ed841d6cc933\">\n"
        + "   <v61:ServiceType>\n"
        + "    <v61:Code>067801</v61:Code>\n"
        + "    <v61:Name>Помощь в организации переезда</v61:Name>\n"
        + "   </v61:ServiceType>\n"
        + "   <v61:Contacts>\n"
        + "    <v61:BaseDeclarant Id=\"declarant\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"v61:RequestContact\">\n"
        + "     <v61:Type>Declarant</v61:Type>\n"
        + "     <v61:LastName>Счётчиков</v61:LastName>\n"
        + "     <v61:FirstName>Повер</v61:FirstName>\n"
        + "     <v61:MiddleName>Заменович</v61:MiddleName>\n"
        + "     <v61:Gender>Male</v61:Gender>\n"
        + "     <v61:BirthDate>1992-01-19</v61:BirthDate>\n"
        + "     <v61:Snils>773-824-133 69</v61:Snils>\n"
        + "     <v61:MobilePhone>9051231111</v61:MobilePhone>\n"
        + "     <v61:EMail>poverkashetchikov@mailinator.com</v61:EMail>\n"
        + "     <v61:CitizenshipType xsi:nil=\"true\"/>\n"
        + "     <v61:SsoId>375f27c1-9d79-449e-bef5-8f6404a85bff</v61:SsoId>\n"
        + "    </v61:BaseDeclarant>\n"
        + "   </v61:Contacts>\n"
        + "   <v61:CustomAttributes>\n"
        + "    <NS1:ServiceProperties xmlns:NS1=\"http://mos.ru/gu/service/054201/\">\n"
        + "     <NS1:AddressOld>\n"
        + "      <NS1:Unom>11325</NS1:Unom>\n"
        + "      <NS1:Text>город Москва, улица Коштоянца, дом 13, квартира 75, кв. undefined</NS1:Text>\n"
        + "      <NS1:Room>2</NS1:Room>\n"
        + "      <NS1:Entrance>3</NS1:Entrance>\n"
        + "      <NS1:Entrance_code>3</NS1:Entrance_code>\n"
        + "      <NS1:Floor>3</NS1:Floor>\n"
        + "     </NS1:AddressOld>\n"
        + "     <NS1:AddressNew>\n"
        + "      <NS1:Unom>7775525</NS1:Unom>\n"
        + "      <NS1:Text>Улица Пушкина, дом Колотушкина 1, кв. 10</NS1:Text>\n"
        + "      <NS1:Room>1</NS1:Room>\n"
        + "      <NS1:Entrance>3</NS1:Entrance>\n"
        + "      <NS1:Entrance_code>3</NS1:Entrance_code>\n"
        + "      <NS1:Floor>3</NS1:Floor>\n"
        + "     </NS1:AddressNew>\n"
        + "     <NS1:Notes>3</NS1:Notes>\n"
        + "     <NS1:RemovalDateTimeStart>2020-12-11T08:00:00+03:00</NS1:RemovalDateTimeStart>\n"
        + "     <NS1:RemovalDateTimeEnd>2020-12-11T12:00:00+03:00</NS1:RemovalDateTimeEnd>\n"
        + "     <NS1:Confirm1>true</NS1:Confirm1>\n"
        + "     <NS1:Confirm2>true</NS1:Confirm2>\n"
        + "     <NS1:Confirm3>true</NS1:Confirm3>\n"
        + "     <NS1:BookingUid>608f443c-811d-3490-abbf-bd48eaf4ae4e</NS1:BookingUid>\n"
        + "    </NS1:ServiceProperties>\n"
        + "   </v61:CustomAttributes>\n"
        + "  </v61:SignService>\n"
        + " </v61:CoordinateDataMessage>\n"
        + "</v61:CoordinateMessage>";

    private static final String TEST_STATUS_MESSAGE =
        "<CoordinateStatusMessage xmlns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
            + "  <CoordinateStatusDataMessage>\n"
            + "    <Status>\n"
            + "      <StatusCode>1069</StatusCode>\n"
            + "      <StatusTitle>Запрос на отзыв заявки</StatusTitle>\n"
            + "      <StatusDate>2019-09-13T10:05:01+03:00</StatusDate>\n"
            + "    </Status>\n"
            + "    <Responsible xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
            + "    <ServiceNumber>8899-9300120-067801-00000309/20</ServiceNumber>\n"
            + "    <Department xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
            + "    <StatusId>1069</StatusId>\n"
            + "  </CoordinateStatusDataMessage>\n"
            + "</CoordinateStatusMessage>";

}
