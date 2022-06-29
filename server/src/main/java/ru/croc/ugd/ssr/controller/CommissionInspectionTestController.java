package ru.croc.ugd.ssr.controller;

import static java.util.Optional.ofNullable;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.mq.listener.commissioninspection.CommissionInspectionRequestListener;
import ru.croc.ugd.ssr.service.commissioninspection.CommissionInspectionService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;

@RestController
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.commission-inspection.enabled")
public class CommissionInspectionTestController {

    private final CommissionInspectionRequestListener commissionInspectionRequestListener;
    private final CommissionInspectionDocumentService commissionInspectionDocumentService;
    private final CommissionInspectionService commissionInspectionService;

    @ApiOperation(value = "Send commission inspection status to MPGU")
    @PostMapping(value = "/mpgu/commission-inspection-status")
    public void mpguSendStatus(
        @ApiParam(value = "eno", required = true) @RequestParam String eno,
        @ApiParam(value = "status", required = true) @RequestParam String status
    ) {
        final CommissionInspectionFlowStatus flowStatus = CommissionInspectionFlowStatus.of(status);
        final CommissionInspectionDocument commissionInspectionDocument = commissionInspectionDocumentService
            .findByEno(eno)
            .orElseThrow(() -> new RuntimeException("Несуществует заявления с указанным ено"));

        commissionInspectionService.sendFlowStatus(commissionInspectionDocument, flowStatus);
    }

    @ApiOperation(value = "Incoming commission inspection event from MPGU")
    @PostMapping(value = "/mpgu/commission-inspection-event")
    public void mpguCoordinateMessage(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        commissionInspectionRequestListener.handleCoordinateMessage(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_MESSAGE)
            )
        );
    }

    @ApiOperation(value = "Incoming commission inspection status event from MPGU")
    @PostMapping(value = "/mpgu/commission-inspection-status-event")
    public void mpguCoordinateStatusMessage(
        @ApiParam(value = "Message", required = false) @RequestBody(required = false) String message
    ) {
        commissionInspectionRequestListener.handleCoordinateStatusMessage(
            new GenericMessage<>(
                ofNullable(message).orElse(TEST_STATUS_MESSAGE)
            )
        );
    }

    private static final String TEST_STATUS_MESSAGE =
        "<CoordinateStatusMessage xmlns=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
            + "  <CoordinateStatusDataMessage>\n"
            + "    <Status>\n"
            + "      <StatusCode>1068</StatusCode>\n"
            + "      <StatusTitle>Запрос на изменение списка дефектов</StatusTitle>\n"
            + "      <StatusDate>2019-09-13T10:05:01+03:00</StatusDate>\n"
            + "    </Status>\n"
            + "    <Responsible xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "    <ServiceNumber>0001-9300120-0834-00001000/21</ServiceNumber>\n"
            + "    <Department xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "    <StatusId>1068</StatusId>\n"
            + "    <Documents>\n"
            + "      <ServiceDocument>\n"
            + "        <CustomAttributes>\n"
            + "          <NS1:ServiceDocumentStatus xmlns:NS1=\"http://mos.ru/gu/service/0834/\">\n"
            + "            <NS1:defects>\n"
            + "              <NS1:defect>\n"
            + "                <NS1:flatElement>Комната 1</NS1:flatElement>\n"
            + "                <NS1:description>В розетку не входит вилка до конца</NS1:description>\n"
            + "              </NS1:defect>\n"
            + "              <NS1:defect>\n"
            + "                <NS1:flatElement>Комната 3</NS1:flatElement>\n"
            + "                <NS1:description>Грязный лоток</NS1:description>\n"
            + "              </NS1:defect>\n"
            + "            </NS1:defects>\n"
            + "          </NS1:ServiceDocumentStatus>\n"
            + "        </CustomAttributes>\n"
            + "      </ServiceDocument>\n"
            + "    </Documents>\n"
            + "  </CoordinateStatusDataMessage>\n"
            + "</CoordinateStatusMessage>";

    private static final String TEST_MESSAGE =
        "<v61:CoordinateMessage xmlns:v61=\"http://asguf.mos.ru/rkis_gu/coordinate/v6_1/\">\n"
            + " <v61:CoordinateDataMessage>\n"
            + "  <v61:Service>\n"
            + "   <v61:RegNum>156271283</v61:RegNum>\n"
            + "   <v61:RegDate>2021-04-23T12:26:48+03:00</v61:RegDate>\n"
            + "   <v61:ServiceNumber>0001-9300120-0834-00001000/21</v61:ServiceNumber>\n"
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
            + "  <v61:SignService Id=\"4b226c9c-059c-47f4-9fb5-5ae73e503086\">\n"
            + "   <v61:ServiceType>\n"
            + "    <v61:Code>0834</v61:Code>\n"
            + "    <v61:Name>Оформление заявления на устранение строительных дефектов</v61:Name>\n"
            + "   </v61:ServiceType>\n"
            + "   <v61:Contacts>\n"
            + "    <v61:BaseDeclarant Id=\"declarant\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:type=\"v61:RequestContact\">\n"
            + "     <v61:Type>Declarant</v61:Type>\n"
            + "     <v61:LastName>Каширин</v61:LastName>\n"
            + "     <v61:FirstName>Сергей</v61:FirstName>\n"
            + "     <v61:MiddleName>Петрович</v61:MiddleName>\n"
            + "     <v61:Gender xsi:nil=\"true\"/>\n"
            + "     <v61:BirthDate xsi:nil=\"true\"/>\n"
            + "     <v61:Snils>142-233-975 39</v61:Snils>\n"
            + "     <v61:MobilePhone>7915145224</v61:MobilePhone>\n"
            + "     <v61:EMail>ttest@gmail.com</v61:EMail>\n"
            + "     <v61:CitizenshipType xsi:nil=\"true\"/>\n"
            + "     <v61:SsoId>DIT2012@mos.ruUUID12345</v61:SsoId>\n"
            + "    </v61:BaseDeclarant>\n"
            + "   </v61:Contacts>\n"
            + "   <v61:CustomAttributes>\n"
            + "    <NS1:ServiceProperties xmlns:NS1=\"http://mos.ru/gu/service/0834/\">\n"
            + "     <NS1:letterId>7456-963</NS1:letterId>\n"
            + "     <NS1:tradeType>Предлагаемая квартира</NS1:tradeType>\n"
            + "     <NS1:flatStatus>Заселение</NS1:flatStatus>\n"
            + "     <NS1:defects>\n"
            + "      <NS1:defect>\n"
            + "       <NS1:flatElement>Комната 1</NS1:flatElement>\n"
            + "       <NS1:description>В розетку не входит вилка до конца</NS1:description>\n"
            + "      </NS1:defect>\n"
            + "      <NS1:defect>\n"
            + "       <NS1:flatElement>Комната 1</NS1:flatElement>\n"
            + "       <NS1:description>Не установлены розетки и выключатели утопленного типа улучшенного "
            + "дизайна из материала, стойкого к ультрафиолетовым излучениям и появлению царапин</NS1:description>\n"
            + "      </NS1:defect>      \n"
            + "     </NS1:defects>\n"
            + "     <NS1:additionalPhone>(111) 111-11-11</NS1:additionalPhone>\n"
            + "     <NS1:useElectronicFormat>true</NS1:useElectronicFormat>\n"
            + "    </NS1:ServiceProperties>\n"
            + "   </v61:CustomAttributes>\n"
            + "  </v61:SignService>\n"
            + " </v61:CoordinateDataMessage>\n"
            + "</v61:CoordinateMessage>";
}
