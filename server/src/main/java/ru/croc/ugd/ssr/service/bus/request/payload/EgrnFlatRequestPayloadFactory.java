package ru.croc.ugd.ssr.service.bus.request.payload;

import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;

import java.util.Map;

@Component
public class EgrnFlatRequestPayloadFactory implements BusRequestPayloadFactory {

    //TODO Konstantin: Implement programmatic solution
    private static final String EGRN_FLAT_PAYLOAD_TEMPLATE = "<egrn:SmevRequestEGRN "
        + "                      xmlns:egrn=\"http://reinform.ru/cdp/smev/egrn\"\n"
        + "                      xmlns:tob=\"http://rosreestr.ru/services/v0.1/commons/TObject\"\n"
        + "                      xmlns:tst=\"http://rosreestr.ru/services/v0.18/TStatementRequestEGRN\">\n"
        + "    <egrn:serviceNumber>%s</egrn:serviceNumber>"
        + "    <egrn:serviceTypeCode>%s</egrn:serviceTypeCode>"
        + "    <egrn:orgProfile>%s</egrn:orgProfile>"
        + "    <egrn:ochdFolderGuid>%s</egrn:ochdFolderGuid>"
        + "    <egrn:requestEGRNDataAction>"
        + "        <tst:extractDataAction>"
        + "            <tst:object>"
        + "                <tob:objectTypeCode>002001003000</tob:objectTypeCode>"
        + "                <tob:cadastralNumber>"
        + "                    <tob:cadastralNumber>%s</tob:cadastralNumber>"
        + "                </tob:cadastralNumber>"
        + "            </tst:object>"
        + "            <tst:requestType>extractRealty</tst:requestType>"
        + "        </tst:extractDataAction>"
        + "    </egrn:requestEGRNDataAction>"
        + "</egrn:SmevRequestEGRN>";

    public static final String CADASTRAL_NUMBER_KEY = "cadastralNumber";

    @Override
    public BusRequestType getBusRequestType() {
        return BusRequestType.EGRN_FLAT;
    }

    @Override
    public String getRequestPayload(final CreateBusRequestDto createBusRequestDto) {
        final Map<String, String> customVariables = createBusRequestDto.getCustomVariables();
        final String cadastralNumber = customVariables.get(CADASTRAL_NUMBER_KEY);

        return String.format(
            EGRN_FLAT_PAYLOAD_TEMPLATE,
            createBusRequestDto.getServiceNumber(),
            createBusRequestDto.getServiceTypeCode(),
            BusRequestType.EGRN_FLAT.getOrgProfile(),
            createBusRequestDto.getOchdFolderGuid(),
            cadastralNumber
        );
    }
}
