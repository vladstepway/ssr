package ru.croc.ugd.ssr.solr.converter;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.solr.UgdSsrRealEstate;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.document.model.DocumentType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация конвертера для Объекта недвижимости.
 */
@Slf4j
@Component
public class RealEstateDocumentConverter extends SsrDocumentConverter<RealEstateDocument, UgdSsrRealEstate> {

    @Override
    public DocumentType<RealEstateDocument> getDocumentType() {
        return SsrDocumentTypes.REAL_ESTATE;
    }

    @Override
    public UgdSsrRealEstate convertDocument(@NotNull RealEstateDocument document) {
        final UgdSsrRealEstate position = createDocument(getAnyAccessType(), document.getId());
        final RealEstateDataType source = of(document.getDocument())
            .map(RealEstate::getRealEstateData)
            .orElseThrow(() -> new SolrDocumentConversionException(document.getId()));


        // Переносим данные из документа в то, что отправим на индексацию в Solr
        // Разработчик, помни! Исходный документ - это всего лишь JSON - поэтому null-ом может
        // оказаться всё что угодно. Предохраняйтесь от NPE!
        if (null != source.getADRTYPE()) {
            position.setUgdSsrRealEstateAdrType(source.getADRTYPE().getCode());
        }
        if (null != source.getAID()) {
            position.setUgdSsrRealEstateAid(source.getAID().longValue());
        }
        if (null != source.getCadastralNums() && null != source.getCadastralNums().getCadastralNum()) {
            position.setUgdSsrRealEstateCadastalNums(
                source.getCadastralNums().getCadastralNum()
                    .stream()
                    .map(RealEstateDataType.CadastralNums.CadastralNum::getGlobalID)
                    .map(BigInteger::longValue)
                    .collect(Collectors.toList())
            );
            position.setUgdSsrRealEstateCadastralNumbersValue(
                source.getCadastralNums().getCadastralNum()
                    .stream()
                    .map(RealEstateDataType.CadastralNums.CadastralNum::getValue)
                    .collect(Collectors.joining(", "))
            );
        }
        if (null != source.getCadastralNumsZU() && null != source.getCadastralNumsZU().getCadastralZU()) {
            position.setUgdSsrRealEstateCadastalNumsZu(
                source.getCadastralNumsZU().getCadastralZU()
                    .stream()
                    .map(RealEstateDataType.CadastralNumsZU.CadastralZU::getGlobalID)
                    .map(BigInteger::longValue)
                    .collect(Collectors.toList())
            );
        }
        if (null != source.getDISTRICT()) {
            position.setUgdSsrRealEstateDistrict(source.getDISTRICT().getCode());
        }
        if (null != source.getFlats()) {
            position.setUgdSsrRealEstateFlatcount((long) source.getFlats().getFlat().size());
        } else {
            position.setUgdSsrRealEstateFlatcount(0L);
        }
        position.setUgdSsrRealEstateGeo(source.getGEO());
        if (null != source.getGlobalID()) {
            position.setUgdSsrRealEstateGlobalId(source.getGlobalID().longValue());
        }
        if (null != source.getHouseYear()) {
            position.setUgdSsrRealEstateHouseYear(source.getHouseYear().longValue());
        }
        if (null != source.getHouseL1TYPE()) {
            position.setUgdSsrRealEstateHouseL1Type(source.getHouseL1TYPE().getCode());
        }
        position.setUgdSsrRealEstateHouseL1Value(source.getHouseL1VALUE());
        if (null != source.getCorpL2TYPE()) {
            position.setUgdSsrRealEstateCorpL2Type(source.getCorpL2TYPE().getCode());
        }
        position.setUgdSsrRealEstateCorpL2Value(source.getCorpL2VALUE());
        if (null != source.getBuildingL3TYPE()) {
            position.setUgdSsrRealEstateBuildingL3Type(source.getBuildingL3TYPE().getCode());
        }
        position.setUgdSsrRealEstateBuildingL3Value(source.getBuildingL3VALUE());
        position.setUgdSsrRealEstateNFias(source.getNFIAS());
        if (null != source.getNREG()) {
            position.setUgdSsrRealEstateNreg(source.getNREG().longValue());
        }
        if (null != source.getOBJTYPE()) {
            position.setUgdSsrRealEstateObjType(source.getOBJTYPE().getCode());
        }
        if (null != source.getSubjectRFP1()) {
            position.setUgdSsrRealEstateSubjectRfP1(source.getSubjectRFP1().getCode());
        }
        if (null != source.getSettlementP3()) {
            position.setUgdSsrRealEstateSettlementP3(source.getSettlementP3().getCode());
        }
        if (null != source.getTownP4()) {
            position.setUgdSsrRealEstateTownP4(source.getTownP4().getCode());
        }
        if (null != source.getMunOkrugP5()) {
            position.setUgdSsrRealEstateMunOkrugP5(source.getMunOkrugP5().getCode());
            position.setUgdSsrRealEstateMunOkrug(source.getMunOkrugP5().getName());
        }
        if (null != source.getLocalityP6()) {
            position.setUgdSsrRealEstateLocalityP6(source.getLocalityP6().getCode());
        }
        if (null != source.getElementP7()) {
            position.setUgdSsrRealEstateElementP7(source.getElementP7().getCode());
        }
        if (null != source.getAdditionalAddrElementP90()) {
            position.setUgdSsrRealEstateAdditionalAddrDocumentP90(source.getAdditionalAddrElementP90().getCode());
        }
        if (null != source.getRefinementAddrP91()) {
            position.setUgdSsrRealEstateRefinementAddrP91(source.getRefinementAddrP91().getCode());
        }

        final String address = getAddress(source);
        ofNullable(address).ifPresent(position::setUgdSsrRealEstateAddress);

        if (null != source.getADMAREA()) {
            position.setUgdSsrRealEstateAdmArea(source.getADMAREA().getName());
        }
        position.setUgdSsrRealEstateResettlementSoon(source.getResettlementSoon());
        position.setUgdSsrRealEstateSeria(source.getSeria());
        position.setUgdSsrRealEstateServiceCompany(source.getServiceCompany());
        position.setUgdSsrRealEstateServiceCompanyAddrPhone(source.getServiceCompanyAddrPhone());
        if (null != source.getSOSTAD()) {
            position.setUgdSsrRealEstateSostad(source.getSOSTAD().getCode());
        }
        if (null != source.getSTATUS()) {
            position.setUgdSsrRealEstateStatus(source.getSTATUS().getCode());
        }
        position.setUgdSsrRealEstateUpdatedDate(source.getUpdatedDate());

        String sourceUpdatedStatus = source.getUpdatedStatus();
        String updatedStatus;
        if (nonNull(sourceUpdatedStatus)) {
            updatedStatus = "Превышен суточный лимит запросов на документ".equals(sourceUpdatedStatus)
                ? "в процессе обогащения"
                : sourceUpdatedStatus;
        } else {
            updatedStatus = "не обогащалось";
        }
        position.setUgdSsrRealEstateUpdatedStatus(updatedStatus);
        if (null != source.getUNOM()) {
            position.setUgdSsrRealEstateUnom(source.getUNOM().toString());
        }
        if (null != source.getVID()) {
            position.setUgdSsrRealEstateVid(source.getVID().getCode());
        }
        if (nonNull(source.getSsoIdCount())) {
            position.setUgdSsrRealEstateSsoidCount(source.getSsoIdCount());
        }
        position.setUgdSsrRealEstateResettlementStatus(source.getResettlementStatus());

        final List<String> flatNumbers = ofNullable(source.getFlats())
            .map(RealEstateDataType.Flats::getFlat)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(FlatType::getFlatNumber)
            .collect(Collectors.toList());
        if (!flatNumbers.isEmpty()) {
            position.setUgdSsrRealEstateFlatNumbers(flatNumbers);
        }

        position.setUgdSsrRealEstateCcoUnoms(source.getCcoUnoms());
        position.setUgdSsrRealEstateResettledFlatCountDgi((long) source.getResettledFlatCountDgi());
        position.setUgdSsrRealEstateResettledFlatCountMfr((long) source.getResettledFlatCountMfr());
        position.setUgdSsrRealEstateCourtFlatCountDgi((long) source.getCourtFlatCountDgi());
        position.setUgdSsrRealEstateCourtFlatCountMfr((long) source.getCourtFlatCountMfr());
        position.setUgdSsrRealEstateActiveResettlementFlatCountDgi((long) source.getActiveResettlementFlatCountDgi());
        position.setUgdSsrRealEstateActiveResettlementFlatCountMfr((long) source.getActiveResettlementFlatCountMfr());
        return position;
    }

    private String getAddress(final RealEstateDataType source) {
        return RealEstateUtils.getRealEstateAddress(source);
    }

}
