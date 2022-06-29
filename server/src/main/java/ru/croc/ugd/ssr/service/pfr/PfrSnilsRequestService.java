package ru.croc.ugd.ssr.service.pfr;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.BIRTH_DATE_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.DOC_TYPE_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.FIRST_NAME_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.GENDER_CODE_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.ISSUER_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.ISSUE_DATE_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.LAST_NAME_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.MIDDLE_NAME_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.NUMBER_KEY;
import static ru.croc.ugd.ssr.service.bus.request.payload.PfrSnilsRequestPayloadFactory.SERIES_KEY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.dto.pfr.CreatePfrSnilsRequestDto;
import ru.croc.ugd.ssr.dto.pfr.PersonDocumentDto;
import ru.croc.ugd.ssr.enums.GenderType;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.mapper.PfrSnilsRequestMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.PfrSnilsRequestDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequest;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequestCriteria;
import ru.croc.ugd.ssr.pfr.PfrSnilsRequestType;
import ru.croc.ugd.ssr.pfr.PfrSnilsResponse;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.PfrSnilsRequestDocumentService;
import ru.croc.ugd.ssr.service.mdm.MdmExternalPersonInfoService;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class PfrSnilsRequestService {

    private static final String SSR_ASUR_CODE = "0874-9000154";
    private static final String GU_CODE = "086601";

    private static final String VOWEL_LETTERS = "аеёиоуыэюя";

    public static final String BUS_REQUEST_SENT_STATUS = "0";
    private static final String BUS_REQUEST_ERROR_STATUS = "-1";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final BusRequestService busRequestService;
    private final EnoCreator enoCreator;
    private final PfrSnilsRequestMapper pfrSnilsRequestMapper;
    private final PfrSnilsRequestDocumentService pfrSnilsRequestDocumentService;
    private final PersonDocumentService personDocumentService;
    private final MdmExternalPersonInfoService mdmExternalPersonInfoService;

    public void createByPersonDocumentIdList(final List<String> personDocumentIdList) {
        createByPersonDocumentList(personDocumentIdList
            .stream()
            .map(personDocumentService::fetchById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(personDocument -> StringUtils.isBlank(personDocument
                .getDocument().getPersonData().getSNILS()))
            .collect(Collectors.toList())
        );
    }

    public void createByPersonDocumentList(final List<PersonDocument> personDocumentList) {
        create(personDocumentList
            .stream()
            .map(this::createDto)
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
        );
    }

    private CreatePfrSnilsRequestDto createDto(final PersonDocument personDocument) {
        final PersonType person = personDocument.getDocument().getPersonData();
        final PersonDocumentDto documentDto = getPersonDocumentDto(person.getPassport());

        if (documentDto == null) {
            log.info("updateSnilsFromPfr: skip pfr request for personDocumentId = {}, unknown passport {}",
                personDocument.getId(),
                person.getPassport()
            );
            updatePersonStatus(personDocument, "недостаточно данных для запроса в ПФР");
            return null;
        }

        personDocumentService.updatePersonFioInformation(personDocument);

        if (StringUtils.isBlank(person.getLastName()) || StringUtils.isBlank(person.getFirstName())) {
            log.info("updateSnilsFromPfr: skip pfr request for personDocumentId = {},"
                    + " lastName = '{}', firstName = '{}'",
                personDocument.getId(),
                person.getLastName(),
                person.getFirstName()
            );
            updatePersonStatus(personDocument, "недостаточно данных для запроса в ПФР");
            return null;
        }

        if (person.getBirthDate() == null) {
            log.info("updateSnilsFromPfr: skip pfr request for personDocumentId = {}, empty birthDate",
                personDocument.getId()
            );
            updatePersonStatus(personDocument, "недостаточно данных для запроса в ПФР");
            return null;
        }

        final GenderType gender = ofNullable(person.getGender())
            .map(String::trim)
            .map(GenderType::fromName)
            .orElseGet(() -> getGenderFromMiddleName(person.getMiddleName()));

        if (gender == null) {
            log.info("updateSnilsFromPfr: skip pfr request for personDocumentId = {}, unknown gender {}",
                personDocument.getId(),
                person.getGender()
            );
            updatePersonStatus(personDocument, "недостаточно данных для запроса в ПФР");
            return null;
        }

        return CreatePfrSnilsRequestDto
            .builder()
            .personDocumentId(personDocument.getId())
            .lastName(person.getLastName())
            .firstName(person.getFirstName())
            .middleName(person.getMiddleName())
            .birthDate(person.getBirthDate().atStartOfDay())
            .genderCode(gender.getCode())
            .docType(documentDto.getDocType())
            .series(documentDto.getSeries())
            .number(documentDto.getNumber())
            .issueDate(documentDto.getIssueDate())
            .issuer(documentDto.getIssuer())
            .build();
    }

    private GenderType getGenderFromMiddleName(final String middleName) {
        if (middleName == null) {
            return null;
        }
        return VOWEL_LETTERS.contains(middleName.toLowerCase().substring(middleName.length() - 1))
            ? GenderType.Female
            : GenderType.Male;
    }

    private static String cleanQuotes(final String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        return source.replace("\"", " ");
    }

    private static String cleanDoubleSpaces(final String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        String temp = source.trim();
        while (temp.contains("  ")) {
            temp = temp.replace("  ", " ");
        }
        return temp;
    }

    private static String cleanSpaces(final String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        return source.replace(" ", "");
    }

    public static PersonDocumentDto getPersonDocumentDto(final String passport) {
        if (passport == null) {
            return null;
        } else if (passport.startsWith("паспорт") && passport.indexOf("РФ") > 0) {
            return parsePassportRf(passport);
        } else if (passport.startsWith("паспорт")) {
            return parsePassportUssr(passport);
        } else if (passport.startsWith("свидетельство о рождении")) {
            return parseBirthCertificateRf(passport);
        } else if (passport.startsWith("заграничный паспорт")) {
            return parseZagran(passport);
        } else if (passport.startsWith("вид на жительство")) {
            return parseResidencePermit(passport);
        } else if (passport.startsWith("иностранный паспорт")) {
            return parseForeignPassport(passport);
        } else {
            return null;
        }
    }

    private static PersonDocumentDto parsePassportRf(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String number = passport.substring(startNum + 2, endNum);
        return PersonDocumentDto
            .builder()
            .docType(1)
            .series(cleanSpaces(number.substring(7)))
            .number(cleanSpaces(number.substring(0, 6)))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    private static PersonDocumentDto parseForeignPassport(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String series = passport.substring(startNum + 2, endNum);
        return PersonDocumentDto
            .builder()
            .docType(2)
            .series(cleanSpaces(series))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    private static PersonDocumentDto parseResidencePermit(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String number = passport.substring(startNum + 2, endNum);
        final int space = number.indexOf(" ");
        if (space <= 0) {
            return null;
        }
        return PersonDocumentDto
            .builder()
            .docType(3)
            .series(cleanSpaces(number.substring(space + 1)))
            .number(cleanSpaces(number.substring(0, space)))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    private static PersonDocumentDto parseZagran(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String number = passport.substring(startNum + 2, endNum);
        final int space = number.indexOf(" ");
        if (space <= 0) {
            return null;
        }
        return PersonDocumentDto
            .builder()
            .docType(4)
            .series(cleanSpaces(number.substring(space + 1)))
            .number(cleanSpaces(number.substring(0, space)))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    private static PersonDocumentDto parsePassportUssr(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String number = passport.substring(startNum + 2, endNum);
        return PersonDocumentDto
            .builder()
            .docType(7)
            .series(cleanSpaces(number.substring(7)))
            .number(cleanSpaces(number.substring(0, 6)))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    private static PersonDocumentDto parseBirthCertificateRf(final String passport) {
        final int length = passport.length();
        final int startNum = passport.indexOf("№");
        final int endNum = passport.indexOf(",");
        if (startNum <= 0 || endNum <= 0 || startNum >= endNum) {
            return null;
        }
        final String number = passport.substring(startNum + 2, endNum);
        return PersonDocumentDto
            .builder()
            .docType(8)
            .series(cleanSpaces(number.substring(7)))
            .number(cleanSpaces(number.substring(0, 6)))
            .issueDate(LocalDate.parse(passport.substring(length - 10, length), DATE_TIME_FORMATTER).atStartOfDay())
            .issuer(cleanDoubleSpaces(cleanQuotes(passport.substring(endNum + 8, length - 11))))
            .build();
    }

    public void create(final List<CreatePfrSnilsRequestDto> dtoList) {
        dtoList.forEach(this::create);
    }

    public void create(final CreatePfrSnilsRequestDto dto) {
        final String serviceNumber = enoCreator.generateAsurEnoNumber(SSR_ASUR_CODE, GU_CODE);

        log.info("updateSnilsFromPfr: send pfr snils request; serviceNumber {}", serviceNumber);

        final PfrSnilsRequestDocument pfrSnilsRequestDocument = pfrSnilsRequestMapper
            .toPfrSnilsRequestDocument(dto, serviceNumber);

        pfrSnilsRequestDocumentService.createDocument(pfrSnilsRequestDocument, false, null);

        final PfrSnilsRequestType pfrSnilsRequest = pfrSnilsRequestDocument.getDocument().getPfrSnilsRequestData();

        try {
            final BusRequestDocument busRequestDocument = sendBusRequest(serviceNumber, dto);
            pfrSnilsRequest.setBusRequestDocumentId(busRequestDocument.getId());
            pfrSnilsRequest.setStatusCode(BUS_REQUEST_SENT_STATUS);
            pfrSnilsRequestDocumentService.updateDocument(pfrSnilsRequestDocument);
        } catch (Exception e) {
            pfrSnilsRequest.setStatusCode(BUS_REQUEST_ERROR_STATUS);
            pfrSnilsRequestDocumentService.updateDocument(pfrSnilsRequestDocument);
            updatePersonStatus(pfrSnilsRequest, "ошибка отправки запроса в ПФР");
            throw e;
        }
    }

    private BusRequestDocument sendBusRequest(final String serviceNumber, final CreatePfrSnilsRequestDto dto) {
        final Map<String, String> customVariables = new HashMap<>();
        customVariables.put(LAST_NAME_KEY, dto.getLastName());
        customVariables.put(FIRST_NAME_KEY, dto.getFirstName());
        customVariables.put(MIDDLE_NAME_KEY, dto.getMiddleName());
        customVariables.put(BIRTH_DATE_KEY, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dto.getBirthDate()));
        customVariables.put(GENDER_CODE_KEY, String.valueOf(dto.getGenderCode()));
        customVariables.put(DOC_TYPE_KEY, String.valueOf(dto.getDocType()));
        customVariables.put(SERIES_KEY, dto.getSeries());
        customVariables.put(NUMBER_KEY, dto.getNumber());
        customVariables.put(ISSUE_DATE_KEY, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dto.getIssueDate()));
        customVariables.put(ISSUER_KEY, dto.getIssuer());

        final CreateBusRequestDto createBusRequestDto = CreateBusRequestDto
            .builder()
            .busRequestType(BusRequestType.PFR_SNILS_EXTENDED)
            .serviceNumber(serviceNumber)
            .serviceTypeCode(GU_CODE)
            .customVariables(customVariables)
            .build();
        return busRequestService.sendBusRequest(createBusRequestDto);
    }

    public PfrSnilsRequestDocument fillResponseData(
        final PfrSnilsRequestDocument pfrSnilsRequestDocument, final SmevResponse response
    ) {
        final PfrSnilsRequestType pfrSnilsRequest = pfrSnilsRequestDocument.getDocument().getPfrSnilsRequestData();

        pfrSnilsRequest.setStatusCode(response.getStatusCode());
        pfrSnilsRequest.setErrorDescription(null);

        final String snils = getStringByXpath(response.getResultDescription(),
            "/ns2:SnilsByAdditionalDataResponse/ns2:Snils");
        final String gender = getStringByXpath(response.getResultDescription(),
            "/ns2:SnilsByAdditionalDataResponse/ns2:Gender");

        final PfrSnilsResponse pfrSnilsResponse = ofNullable(pfrSnilsRequest.getResponse())
            .orElseGet(PfrSnilsResponse::new);
        pfrSnilsResponse.setSnils(formatSnils(snils));
        pfrSnilsResponse.setGender(gender);
        pfrSnilsRequest.setResponse(pfrSnilsResponse);

        updatePersonWithPfrSnilsRequest(pfrSnilsRequest);

        return pfrSnilsRequestDocument;
    }

    public static String formatSnils(final String snils) {
        if (isNull(snils) || snils.length() != 11) {
            return snils;
        }
        return snils.substring(0, 3)
            + "-"
            + snils.substring(3, 6)
            + "-"
            + snils.substring(6, 9)
            + " "
            + snils.substring(9, 11);
    }

    private static String getStringByXpath(String xml, String expression) {
        try {
            InputSource inputXml = new InputSource(new StringReader(xml));
            XPath path = XPathFactory.newInstance().newXPath();
            path.setNamespaceContext(new NamespaceContext() {
                @Override
                public Iterator getPrefixes(final String namespaceUri) {
                    return null;
                }

                @Override
                public String getPrefix(final String namespaceUri) {
                    return null;
                }

                @Override
                public String getNamespaceURI(final String prefix) {
                    if ("ns2".equals(prefix)) {
                        return "http://kvs.pfr.com/snils-by-additionalData/1.0.1";
                    }
                    return null;
                }
            });
            return path.compile(expression).evaluate(inputXml);
        } catch (XPathExpressionException e) {
            log.error("updateSnilsFromPfr: error {}", e.getMessage(), e);
            return null;
        }
    }

    private void updatePersonWithPfrSnilsRequest(final PfrSnilsRequestType pfrSnilsRequest) {
        final String personDocumentId = pfrSnilsRequest.getRequestCriteria().getPersonDocumentId();
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        final PersonType person = personDocument.getDocument().getPersonData();

        final String snils = pfrSnilsRequest.getResponse().getSnils();

        if (StringUtils.isNotBlank(snils) && !snils.equals(person.getSNILS())) {
            person.setSNILS(snils);
            person.setSsoID(mdmExternalPersonInfoService.requestSsoIdBySnils(snils));
        }

        final GenderType oldGender = ofNullable(person.getGender())
            .map(String::trim)
            .map(GenderType::fromName)
            .orElse(null);

        if (oldGender == null) {
            final String newGender = ofNullable(pfrSnilsRequest.getResponse().getGender())
                .map(String::trim)
                .map(GenderType::fromEnglishName)
                .map(GenderType::getName)
                .orElse(null);

            if (StringUtils.isNotBlank(newGender)) {
                person.setGender(newGender);
            }
        }

        updatePersonStatus(personDocument, "обогащено");
    }

    public void updatePersonStatus(final PfrSnilsRequestType pfrSnilsRequest, final String pfrStatus) {
        final String personDocumentId = pfrSnilsRequest.getRequestCriteria().getPersonDocumentId();
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        updatePersonStatus(personDocument, pfrStatus);
    }

    private void updatePersonStatus(final PersonDocument personDocument, final String pfrStatus) {
        final PersonType person = personDocument.getDocument().getPersonData();

        person.setUpdatedFromPFRdate(LocalDate.now());
        person.setUpdatedFromPFRstatus(pfrStatus);
        person.setUpdatedFromELKdate(LocalDate.now());
        person.setUpdatedFromELKstatus("обогащено");

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "updateSnilsFromPfr"
        );
    }

    public void processLimitedRequests() {
        createByPersonDocumentIdList(
            pfrSnilsRequestDocumentService.fetchDocumentsWithLimitedRequests()
                .stream()
                .map(PfrSnilsRequestDocument::getDocument)
                .map(PfrSnilsRequest::getPfrSnilsRequestData)
                .map(PfrSnilsRequestType::getRequestCriteria)
                .map(PfrSnilsRequestCriteria::getPersonDocumentId)
                .collect(Collectors.toList())
        );
    }
}
