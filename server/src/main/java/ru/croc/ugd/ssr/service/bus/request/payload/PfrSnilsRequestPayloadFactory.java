package ru.croc.ugd.ssr.service.bus.request.payload;

import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;

import java.util.Map;

@Component
public class PfrSnilsRequestPayloadFactory implements BusRequestPayloadFactory {

    //TODO Konstantin, Alexander: Implement programmatic solution
    private static final String PFR_SNILS_EXTENDED_PAYLOAD_TEMPLATE =
        "<getSNILSExtended>"
            + "     <includeBinaryView>false</includeBinaryView>"
            + "     <serviceNumber>%s</serviceNumber>"
            + "     <serviceTypeCode>%s</serviceTypeCode>"
            + "     <orgProfile>%s</orgProfile>"
            + "     <ServiceProperties>"
            + "         <lastname>%s</lastname>"
            + "         <firstname>%s</firstname>"
            + "         <middlename>%s</middlename>"
            + "         <birthdate>%s</birthdate>"
            + "         <gendercode>%s</gendercode>"
            + "         <doc_type>%s</doc_type>"
            + "         <series>%s</series>"
            + "         <number>%s</number>"
            + "         <issuedate>%s</issuedate>"
            + "         <issuer>%s</issuer>"
            + "     </ServiceProperties>"
            + " </getSNILSExtended>";

    public static final String LAST_NAME_KEY = "lastname";
    public static final String FIRST_NAME_KEY = "firstname";
    public static final String MIDDLE_NAME_KEY = "middlename";
    public static final String BIRTH_DATE_KEY = "birthdate";
    public static final String GENDER_CODE_KEY = "gendercode";
    public static final String DOC_TYPE_KEY = "doc_type";
    public static final String SERIES_KEY = "series";
    public static final String NUMBER_KEY = "number";
    public static final String ISSUE_DATE_KEY = "issuedate";
    public static final String ISSUER_KEY = "issuer";

    @Override
    public BusRequestType getBusRequestType() {
        return BusRequestType.PFR_SNILS_EXTENDED;
    }

    @Override
    public String getRequestPayload(final CreateBusRequestDto createBusRequestDto) {
        final Map<String, String> customVariables = createBusRequestDto.getCustomVariables();

        final String lastName = customVariables.get(LAST_NAME_KEY);
        final String firstName = customVariables.get(FIRST_NAME_KEY);
        final String middleName = customVariables.get(MIDDLE_NAME_KEY);
        final String birthDate = customVariables.get(BIRTH_DATE_KEY);
        final String genderCode = customVariables.get(GENDER_CODE_KEY);
        final String docType = customVariables.get(DOC_TYPE_KEY);
        final String series = customVariables.get(SERIES_KEY);
        final String number = customVariables.get(NUMBER_KEY);
        final String issueDate = customVariables.get(ISSUE_DATE_KEY);
        final String issuer = customVariables.get(ISSUER_KEY);

        return String.format(
            PFR_SNILS_EXTENDED_PAYLOAD_TEMPLATE,
            createBusRequestDto.getServiceNumber(),
            createBusRequestDto.getServiceTypeCode(),
            BusRequestType.PFR_SNILS_EXTENDED.getOrgProfile(),
            lastName,
            firstName,
            middleName,
            birthDate,
            genderCode,
            docType,
            series,
            number,
            issueDate,
            issuer
        );
    }
}
