package ru.croc.ugd.ssr.service.mdm;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.dto.mdm.request.Binary;
import ru.croc.ugd.ssr.dto.mdm.request.Condition;
import ru.croc.ugd.ssr.dto.mdm.request.Logic;
import ru.croc.ugd.ssr.dto.mdm.request.MdmRequest;
import ru.croc.ugd.ssr.dto.mdm.request.Query;
import ru.croc.ugd.ssr.dto.mdm.response.DocPassportRf;
import ru.croc.ugd.ssr.dto.mdm.response.DocSnils;
import ru.croc.ugd.ssr.dto.mdm.response.Documents;
import ru.croc.ugd.ssr.dto.mdm.response.Ids;
import ru.croc.ugd.ssr.dto.mdm.response.MdmResponse;
import ru.croc.ugd.ssr.dto.mdm.response.PersonDetail;
import ru.croc.ugd.ssr.dto.mdm.response.Sso;
import ru.croc.ugd.ssr.dto.pfr.PersonDocumentDto;
import ru.croc.ugd.ssr.enums.GenderType;
import ru.croc.ugd.ssr.mapper.MdmExternalPersonInfoMapper;
import ru.croc.ugd.ssr.model.MdmExternalPersonInfoDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.MdmExternalPersonInfoDocumentService;
import ru.croc.ugd.ssr.service.pfr.PfrSnilsRequestService;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultMdmExternalPersonInfoService implements MdmExternalPersonInfoService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = new LevenshteinDistance(2);

    private final MdmSendRestUtils mdmSendRestUtils;
    private final MdmExternalPersonInfoDocumentService mdmExternalPersonInfoDocumentService;
    private final MdmExternalPersonInfoMapper mdmExternalPersonInfoMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;
    private final ObjectMapper objectMapper;

    @Value("${ugd.ssr.new-update-snils.mdm-request.real-estate-request-enabled:false}")
    private boolean realEstateRequestEnabled;

    @Override
    public PersonDocument updatePersonFromMdmExternal(final PersonDocument personDocument) {
        log.info("updateSnilsFromPfr: start mdm request for personDocumentId = {}", personDocument.getId());

        personDocumentService.updatePersonFioInformation(personDocument);

        final PersonType person = personDocument.getDocument().getPersonData();

        if (StringUtils.isBlank(person.getLastName()) || StringUtils.isBlank(person.getFirstName())) {
            log.info("updateSnilsFromPfr: skip mdm request for personDocumentId = {},"
                    + " lastName = '{}', firstName = '{}'",
                personDocument.getId(),
                person.getLastName(),
                person.getFirstName()
            );
            return personDocument;
        }

        if (person.getBirthDate() == null) {
            log.info("updateSnilsFromPfr: skip mdm request for personDocumentId = {}, empty birthDate",
                personDocument.getId()
            );
            return personDocument;
        }

        if (!requestAndUpdatePersonByAddressAndBirthDate(personDocument)
            && !requestAndUpdatePersonByFioAndBirthDate(personDocument)) {
            log.info("updateSnilsFromPfr: mdm information for personDocumentId = {} not found",
                personDocument.getId()
            );
            return personDocument;
        }

        return personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, "updatePersonFromMdmExternal"
        );
    }

    @Override
    public MdmResponse request(final MdmRequest mdmRequest) {
        final String result = sendMdmRequest(mdmRequest);
        final MdmResponse mdmResponse = parseMdmResponse(result);

        final MdmExternalPersonInfoDocument mdmExternalPersonInfoDocument = mdmExternalPersonInfoMapper
            .toMdmExternalPersonInfoDocument(result, mdmRequest, mdmResponse, null);

        mdmExternalPersonInfoDocumentService
            .createDocument(mdmExternalPersonInfoDocument, false, "request");

        return mdmResponse;
    }

    @Override
    public void requestByRealEstate(final RealEstateDocument realEstateDocument) {
        if (!realEstateRequestEnabled) {
            log.info("Mdm request by realEstate is disabled");
            return;
        }
        ofNullable(realEstateDocument)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getUNOM)
            .map(BigInteger::toString)
            .ifPresent(this::requestByUnom);
    }

    @Override
    public String requestSsoIdBySnils(final String snils) {
        final MdmRequest mdmRequest = createMdmRequest(conditionsBySnils(snils));

        log.info("updateSnilsFromPfr: sending mdm request by snils {}", snils);

        final String result = sendMdmRequest(mdmRequest);
        final MdmResponse mdmResponse = parseMdmResponse(result);

        final MdmExternalPersonInfoDocument mdmExternalPersonInfoDocument = mdmExternalPersonInfoMapper
            .toMdmExternalPersonInfoDocument(result, mdmRequest, mdmResponse, null);

        mdmExternalPersonInfoDocumentService
            .createDocument(mdmExternalPersonInfoDocument, false, "requestSsoIdBySnils");

        return ofNullable(mdmResponse)
            .map(MdmResponse::getData)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(PersonDetail::getIds)
            .filter(Objects::nonNull)
            .map(Ids::getSsoList)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(Sso::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private boolean requestAndUpdatePersonByAddressAndBirthDate(final PersonDocument personDocument) {
        final PersonType person = personDocument.getDocument().getPersonData();

        final MdmRequest mdmRequest = createMdmRequest(conditionsByAddressAndBirthDate(person));

        log.info("updateSnilsFromPfr: sending mdm request by address and birthDate for personDocumentId = {}",
            personDocument.getId()
        );

        final String result = sendMdmRequest(mdmRequest);
        final MdmResponse mdmResponse = parseMdmResponse(result);

        final MdmExternalPersonInfoDocument mdmExternalPersonInfoDocument = mdmExternalPersonInfoMapper
            .toMdmExternalPersonInfoDocument(result, mdmRequest, mdmResponse, personDocument.getId());

        mdmExternalPersonInfoDocumentService
            .createDocument(mdmExternalPersonInfoDocument, false, "requestByAddressAndBirthDate");

        final PersonDetail personDetail = ofNullable(mdmResponse)
            .map(MdmResponse::getData)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(data -> matchPersonDataToPersonByFio(data, person))
            .filter(data -> matchPersonDataToPersonByPassport(data, person))
            .max(Comparator.comparingInt(this::personDetailRating))
            .orElse(null);

        if (personDetail == null) {
            return false;
        }

        getGender(personDetail).ifPresent(person::setGender);
        getSsoId(personDetail).ifPresent(person::setSsoID);
        getSnils(personDetail).ifPresent(person::setSNILS);

        return true;
    }

    private boolean requestAndUpdatePersonByFioAndBirthDate(final PersonDocument personDocument) {
        final PersonType person = personDocument.getDocument().getPersonData();

        final MdmRequest mdmRequest = createMdmRequest(conditionsByFioAndBirthDate(person));

        log.info("updateSnilsFromPfr: sending mdm request by FIO and birthDate for personDocumentId = {}",
            personDocument.getId()
        );

        final String result = sendMdmRequest(mdmRequest);
        final MdmResponse mdmResponse = parseMdmResponse(result);

        final MdmExternalPersonInfoDocument mdmExternalPersonInfoDocument = mdmExternalPersonInfoMapper
            .toMdmExternalPersonInfoDocument(result, mdmRequest, mdmResponse, personDocument.getId());

        mdmExternalPersonInfoDocumentService
            .createDocument(mdmExternalPersonInfoDocument, false, "requestByFioAndBirthDate");

        PersonDetail personDetail = ofNullable(mdmResponse)
            .map(MdmResponse::getData)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(data -> matchPersonDataToPersonByPassport(data, person, true))
            .max(Comparator.comparingInt(this::personDetailRating))
            .orElse(null);

        if (personDetail != null) {
            getSnils(personDetail).ifPresent(person::setSNILS);
            getGender(personDetail).ifPresent(person::setGender);
            getSsoId(personDetail).ifPresent(person::setSsoID);
            return true;
        }

        personDetail = ofNullable(mdmResponse)
            .map(MdmResponse::getData)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(data -> matchPersonDataToPersonByPassport(data, person))
            .max(Comparator.comparingInt(this::personDetailRating))
            .orElse(null);

        if (personDetail == null) {
            return false;
        }

        getGender(personDetail).ifPresent(person::setGender);
        getSsoId(personDetail).ifPresent(person::setSsoID);

        return true;
    }

    private void requestByUnom(final String unom) {
        final MdmRequest mdmRequest = createMdmRequest(conditionsByUnom(unom));

        log.info("updateSnilsFromPfr: sending mdm request by unom {}", unom);

        final String result = sendMdmRequest(mdmRequest);
        final MdmResponse mdmResponse = parseMdmResponse(result);

        final MdmExternalPersonInfoDocument mdmExternalPersonInfoDocument = mdmExternalPersonInfoMapper
            .toMdmExternalPersonInfoDocument(result, mdmRequest, mdmResponse, null);

        mdmExternalPersonInfoDocumentService
            .createDocument(mdmExternalPersonInfoDocument, false, "requestByUnom");
    }

    private String sendMdmRequest(final MdmRequest mdmRequest) {
        try {
            final String result = mdmSendRestUtils.sendMdmRequest(mdmRequest);
            log.info("updateSnilsFromPfr: mdm response = {}", result);
            return result;
        } catch (Exception e) {
            log.info("updateSnilsFromPfr: unable to get MdmResponse", e);
            return null;
        }
    }

    private MdmResponse parseMdmResponse(final String response) {
        try {
            if (nonNull(response)) {
                return objectMapper.readValue(response, MdmResponse.class);
            }
        } catch (Exception e) {
            log.info("updateSnilsFromPfr: unable to parse MdmResponse: {}", response, e);
        }
        return null;
    }

    private boolean matchPersonDataToPersonByFio(final PersonDetail personDetail, final PersonType person) {
        if (StringUtils.isBlank(person.getFirstName())
            || StringUtils.isBlank(personDetail.getFirstName())
            || LEVENSHTEIN_DISTANCE.apply(person.getFirstName(), personDetail.getFirstName()) > 1
        ) {
            return false;
        }

        if (StringUtils.isBlank(person.getLastName())
            || StringUtils.isBlank(personDetail.getLastName())
            || LEVENSHTEIN_DISTANCE.apply(person.getLastName(), personDetail.getLastName()) > 1
        ) {
            return false;
        }


        return (StringUtils.isBlank(person.getMiddleName())
            && StringUtils.isBlank(personDetail.getMiddleName()))
            || (StringUtils.isNotBlank(person.getMiddleName())
            && StringUtils.isNotBlank(personDetail.getMiddleName())
            && LEVENSHTEIN_DISTANCE.apply(person.getMiddleName(), personDetail.getMiddleName()) <= 1);
    }

    private boolean matchPersonDataToPersonByPassport(final PersonDetail personDetail, final PersonType person) {
        return matchPersonDataToPersonByPassport(personDetail, person, false);
    }

    private boolean matchPersonDataToPersonByPassport(
        final PersonDetail personDetail, final PersonType person, final boolean exactMatch
    ) {
        final PersonDocumentDto personDocumentDto = PfrSnilsRequestService.getPersonDocumentDto(person.getPassport());

        if (isNull(personDocumentDto)) {
            return !exactMatch;
        }

        final List<DocPassportRf> passports = Optional.of(personDetail)
            .map(PersonDetail::getDocuments)
            .map(Documents::getPassports)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .collect(Collectors.toList());

        if (passports.isEmpty()) {
            return !exactMatch;
        }

        if (personDocumentDto.getDocType() != 1) {
            return false;
        }

        return passports
            .stream()
            .anyMatch(passport ->
                Objects.equals(passport.getSeries(), personDocumentDto.getSeries())
                    && Objects.equals(passport.getNumber(), personDocumentDto.getNumber())
            );
    }

    private int personDetailRating(final PersonDetail personDetail) {
        return (getSnils(personDetail).isPresent() ? 10 : 0)
            + (getSsoId(personDetail).isPresent() ? 10 : 0)
            + (getGender(personDetail).isPresent() ? 10 : 0)
            + (getPassport(personDetail).isPresent() ? 1 : 0);
    }

    private Optional<String> getPassport(final PersonDetail personDetail) {
        return ofNullable(personDetail)
            .map(PersonDetail::getDocuments)
            .map(Documents::getPassports)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(DocPassportRf::getNumber)
            .filter(Objects::nonNull)
            .findFirst();
    }

    private Optional<String> getSnils(final PersonDetail personDetail) {
        return ofNullable(personDetail)
            .map(PersonDetail::getDocuments)
            .map(Documents::getSnils)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(DocSnils::getValue)
            .filter(Objects::nonNull)
            .findFirst()
            .map(PfrSnilsRequestService::formatSnils);
    }

    private Optional<String> getSsoId(final PersonDetail personDetail) {
        return ofNullable(personDetail)
            .map(PersonDetail::getIds)
            .map(Ids::getSsoList)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(Sso::getValue)
            .filter(Objects::nonNull)
            .findFirst();
    }

    private Optional<String> getGender(final PersonDetail personDetail) {
        return ofNullable(personDetail)
            .map(PersonDetail::getGender)
            .filter(gender -> "M".equals(gender) || "F".equals(gender))
            .map(gender -> GenderType.fromCharCode(gender.charAt(0)))
            .map(GenderType::getName);
    }

    private MdmRequest createMdmRequest(final List<Condition> conditions) {
        final Logic logic = Logic.builder()
            .operator("AND")
            .conditions(conditions)
            .build();
        final Query query = Query.builder()
            .logic(logic)
            .build();
        return MdmRequest.builder()
            .query(query)
            .protocol("kri")
            .build();
    }

    private List<Condition> conditionsBySnils(final String snils) {
        final List<Condition> conditions = new ArrayList<>();
        conditions.add(createCondition("documents.doc_snils.value", snils));
        return conditions;
    }

    private List<Condition> conditionsByFioAndBirthDate(final PersonType person) {
        final String birthDate = DATE_TIME_FORMATTER.format(person.getBirthDate().atStartOfDay());

        final List<Condition> conditions = new ArrayList<>();

        conditions.add(createCondition("birth_date", birthDate));
        conditions.add(createCondition("last_name", person.getLastName()));
        conditions.add(createCondition("first_name", person.getFirstName()));

        if (StringUtils.isNotBlank(person.getMiddleName())) {
            conditions.add(createCondition("middle_name", person.getMiddleName()));
        }

        return conditions;
    }

    private List<Condition> conditionsByAddressAndBirthDate(final PersonType person) {
        final String birthDate = DATE_TIME_FORMATTER.format(person.getBirthDate().atStartOfDay());
        final String unom = person.getUNOM().toString();

        final String flatNum = ofNullable(person.getFlatNum())
            .orElseGet(() -> getFlatNumberFromRealEstate(person));

        final List<Condition> conditions = new ArrayList<>();

        conditions.add(createCondition("birth_date", birthDate));
        conditions.add(createCondition("addresses.addr_gku.unom", unom));
        conditions.add(createCondition("addresses.addr_gku.flat", flatNum));

        return conditions;
    }

    private List<Condition> conditionsByUnom(final String unom) {
        final List<Condition> conditions = new ArrayList<>();
        conditions.add(createCondition("addresses.addr_gku.unom", unom));
        return conditions;
    }

    private String getFlatNumberFromRealEstate(final PersonType person) {
        return ofNullable(person)
            .map(PersonType::getFlatID)
            .map(flatService::fetchFlat)
            .map(FlatType::getApartmentL4VALUE)
            .orElse(null);
    }

    private Condition createCondition(final String fieldName, final String fieldValue) {
        Binary binary = Binary.builder()
            .field(fieldName)
            .value(fieldValue)
            .operator("=")
            .build();
        return Condition.builder()
            .binary(binary)
            .build();
    }
}
