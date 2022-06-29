package ru.croc.ugd.ssr.service.trade;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.dto.CcoInfo;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionDecodedValues;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionPersonDecodedValue;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Decode for trade addition.
 */
@Service
@Slf4j
public class TradeAdditionValueDecoder {
    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonDocumentService personDocumentService;
    private final CapitalConstructionObjectService ccoService;

    private Map<String, String> realEstateUnomAddressMappings = new HashMap<>();
    private Map<String, CcoInfo> ccoUnomCcoInfoMappings = new HashMap<>();

    public TradeAdditionValueDecoder(RealEstateDocumentService realEstateDocumentService,
                                     PersonDocumentService personDocumentService,
                                     CapitalConstructionObjectService ccoService) {
        this.realEstateDocumentService = realEstateDocumentService;
        this.personDocumentService = personDocumentService;
        this.ccoService = ccoService;
    }

    public void loadMappings() {
        realEstateUnomAddressMappings = realEstateDocumentService.getUnomToAddressMapping();
        ccoUnomCcoInfoMappings = ccoService.getCcoInfoByUnoms();
        log.info("TradeAdditionValueDecoder.loadMappings: mappings are loaded");
    }

    public boolean existsMappings() {
        return !CollectionUtils.isEmpty(realEstateUnomAddressMappings)
            && !CollectionUtils.isEmpty(ccoUnomCcoInfoMappings);
    }

    /**
     * Decodes unoms, personIds affairIds and so on.
     *
     * @param tradeAddition tradeAddition to decode.
     * @return TradeAdditionDecodedValues.
     */
    public TradeAdditionDecodedValues decodeTradeAdditionCodes(final TradeAdditionType tradeAddition) {
        final TradeAdditionDecodedValues tradeAdditionDecodedValues = new TradeAdditionDecodedValues();
        decodePersonIds(tradeAdditionDecodedValues, tradeAddition);
        decodeOldUnom(tradeAdditionDecodedValues, tradeAddition);
        decodeNewUnoms(tradeAdditionDecodedValues, tradeAddition);
        return tradeAdditionDecodedValues;
    }

    public void populateDecodedValues(final TradeAdditionDecodedValues tradeAdditionDecodedValues,
                                       final TradeAdditionType tradeAdditionType) {
        tradeAdditionType.getNewEstates()
            .forEach(estateInfoType -> {
                estateInfoType
                    .setAddress(tradeAdditionDecodedValues
                        .getNewUnomAddressMapping()
                        .get(estateInfoType.getUnom()));
                estateInfoType.setCadNumber(tradeAdditionDecodedValues
                    .getNewUnomCadNumberMapping()
                    .get(estateInfoType.getUnom())
                );
            });
        if (tradeAdditionType.getOldEstate() != null) {
            tradeAdditionType
                .getOldEstate()
                .setAddress(tradeAdditionDecodedValues
                    .getOldUnomAddressMapping()
                    .get(tradeAdditionType
                        .getOldEstate()
                        .getUnom()));
        }
        ListUtils.emptyIfNull(tradeAdditionType.getPersonsInfo())
            .forEach(personInfo -> populatePersonInfo(personInfo, tradeAdditionDecodedValues));
    }

    private void populatePersonInfo(final PersonInfoType personInfoType,
                                    final TradeAdditionDecodedValues tradeAdditionDecodedValues) {
        if (personInfoType != null) {
            TradeAdditionPersonDecodedValue tradeAdditionPersonDecodedValue = tradeAdditionDecodedValues
                .getPersonIdDataMapping()
                .get(personInfoType.getPersonId());
            if (tradeAdditionPersonDecodedValue != null) {
                personInfoType.setPersonDocumentId(tradeAdditionPersonDecodedValue.getPersonDocumentId());
                personInfoType.setPersonFio(tradeAdditionPersonDecodedValue.getPersonFio());
            }
        }
    }

    public String extractCadNumber(final EstateInfoType estateInfoType) {
        final String cadNum = estateInfoType.getCadNumber();
        if (!StringUtils.isEmpty(cadNum)) {
            return cadNum;
        }
        final String unom = estateInfoType.getUnom();
        return ccoService.getCadNumberByUnom(unom);
    }

    private void decodeOldUnom(TradeAdditionDecodedValues tradeAdditionDecodedValues,
                               TradeAdditionType tradeAddition) {
        final String oldUnom = tradeAddition.getOldEstate() != null
            ? tradeAddition.getOldEstate().getUnom()
            : null;
        tradeAdditionDecodedValues.getOldUnomAddressMapping()
            .put(oldUnom, getAddressByUnom(oldUnom));
    }

    private void decodeNewUnoms(TradeAdditionDecodedValues tradeAdditionDecodedValues,
                                TradeAdditionType tradeAddition) {
        ListUtils.emptyIfNull(tradeAddition.getNewEstates())
            .stream()
            .filter(StreamUtils.distinctByKey(EstateInfoType::getUnom))
            .map(EstateInfoType::getUnom)
            .forEach(newUnom -> {
                tradeAdditionDecodedValues
                    .getNewUnomAddressMapping()
                    .put(newUnom, getAddressByUnomFromOks(newUnom));
                tradeAdditionDecodedValues
                    .getNewUnomCadNumberMapping()
                    .put(newUnom, getCadNumberByUnomFromOks(newUnom));
            });
    }

    private void decodePersonIds(TradeAdditionDecodedValues tradeAdditionDecodedValues,
                                 TradeAdditionType tradeAddition) {
        ListUtils.emptyIfNull(tradeAddition.getPersonsInfo())
            .stream()
            .forEach(personInfo ->
                tradeAdditionDecodedValues
                    .getPersonIdDataMapping()
                    .put(personInfo.getPersonId(), mapToPersonDecodedValue(personInfo.getPersonId(),
                        tradeAddition.getAffairId())));
    }

    private TradeAdditionPersonDecodedValue mapToPersonDecodedValue(String personId, String affairId) {
        return getPersonData(personId, affairId)
            .map(person -> TradeAdditionPersonDecodedValue
                .builder()
                .person(person)
                .personDocumentId(person.getDocumentID())
                .personFio(person.getPersonData().getFIO())
                .build())
            .orElse(null);
    }

    private Optional<Person> getPersonData(String personId, String affairId) {
        return personDocumentService
            .fetchOneByPersonIdAndAffairId(personId, affairId)
            .map(PersonDocument::getDocument);
    }

    private String getAddressByUnom(final String unom) {
        final String address = realEstateUnomAddressMappings.get(unom);
        return StringUtils.isEmpty(address) ? getAddressByUnomFromOks(unom) : address;
    }

    private String getAddressByUnomFromOks(final String unom) {
        return Optional
            .ofNullable(ccoUnomCcoInfoMappings.get(unom))
            .map(CcoInfo::getAddress)
            .orElse(null);
    }

    private String getCadNumberByUnomFromOks(final String unom) {
        return Optional
            .ofNullable(ccoUnomCcoInfoMappings.get(unom))
            .map(CcoInfo::getCadNumber)
            .orElse(null);
    }

}
