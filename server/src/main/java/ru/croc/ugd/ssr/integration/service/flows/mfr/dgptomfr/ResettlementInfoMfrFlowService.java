package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.compensationflat.CompensationFlatType;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrResettlementInfoMapper;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrResettlementInfoMapper.DgpToMfrResettlementInfoMapperInput;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrResettlementInfoMapper.FromFlatInput;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrResettlementInfo;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  1. Сведения о начале отселения
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResettlementInfoMfrFlowService extends AbstractMfrFlowService<DgpToMfrResettlementInfo> {

    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final DgpToMfrResettlementInfoMapper dgpToMfrResettlementInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;

    public void sendResettlementInfo(final ResettlementRequestType resettlementRequestType) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendResettlementInfoFlow(resettlementRequestType);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendResettlementInfoFlow(final ResettlementRequestType resettlementRequestType) {
        final List<DgpToMfrResettlementInfo> dgpToMfrResettlementInfos = dgpToMfrResettlementInfoMapper
            .toDgpToMfrResettlementInfoList(createMapperInputList(resettlementRequestType));
        dgpToMfrResettlementInfos.forEach(this::sendToMfr);
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_RESETTLEMENT_INFO_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrResettlementInfo();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrResettlementInfo();
    }

    @Override
    protected int getFlowNumber() {
        return 1;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_RESETTLEMENT_INFO;
    }

    private List<DgpToMfrResettlementInfoMapperInput> createMapperInputList(
        final ResettlementRequestType resettlementRequestType
    ) {
        return resettlementRequestType.getHousesToSettle()
            .stream()
            .map(housesToSettle -> retrieveMapperInputs(housesToSettle, resettlementRequestType))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<DgpToMfrResettlementInfoMapperInput> retrieveMapperInputs(
        final HouseToSettle houseToSettle,
        final ResettlementRequestType resettlementRequestType
    ) {
        final CipType cipType = cipService
            .fetchCipTypeById(houseToSettle.getInformationCenterCode())
            .orElse(null);

        return houseToSettle.getHousesToResettle()
            .stream()
            .map(houseToResettle -> DgpToMfrResettlementInfoMapperInput.builder()
                .resettlementRequestType(resettlementRequestType)
                .houseToResettle(houseToResettle)
                .houseToSettle(houseToSettle)
                .fromCadastralNumber(retrieveCadastralNumber(houseToResettle))
                .cipType(cipType)
                .flatInputs(getFlatInputs(houseToResettle))
                .build()
            )
            .collect(Collectors.toList());
    }

    private List<FromFlatInput> getFlatInputs(
        final HouseToResettle houseToResettle
    ) {
        final String unom = houseToResettle.getRealEstateUnom();
        final RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        final List<PersonType> allHouseLivers = getAllLiversInHouse(houseToResettle);

        final Map<String, List<PersonType>> flatMappedToAffairs = houseToResettle.getFlats()
            .stream()
            .collect(Collectors.toMap(
                Function.identity(),
                flatId -> allHouseLivers.stream()
                    .filter(personType -> flatId.equals(personType.getFlatID()))
                    .filter(personType -> StringUtils.isNotBlank(personType.getAffairId()))
                    .filter(StreamUtils.distinctByKey(PersonType::getAffairId))
                    .collect(Collectors.toList()),
                (f1, f2) -> f1
            ));

        return flatMappedToAffairs.entrySet()
            .stream()
            .map(entry -> retrieveFromFlatInput(
                entry.getKey(),
                entry.getValue(),
                realEstateDocument,
                houseToResettle.getCompensationFlats(),
                allHouseLivers.stream()
                    .filter(personType -> entry.getKey().equals(personType.getFlatID()))
                    .collect(Collectors.toList())
            ))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<FromFlatInput> retrieveFromFlatInput(
        final String flatId,
        final List<PersonType> personTypes,
        final RealEstateDocument realEstateDocument,
        final List<CompensationFlatType> compensationFlats,
        final List<PersonType> allFlatLivers
    ) {
        final FlatType flatType = RealEstateUtils.findFlat(flatId, realEstateDocument);

        if (personTypes.isEmpty()) {
            return Collections.singletonList(
                FromFlatInput.builder()
                    .flatType(flatType)
                    .personType(null)
                    .resettlementDirection(retrieveResettlementDirection(compensationFlats, flatId, null))
                    .build()
            );
        } else {
            return personTypes.stream()
                .map(personType -> FromFlatInput.builder()
                    .flatType(flatType)
                    .personType(personType)
                    .resettlementDirection(retrieveResettlementDirection(
                        compensationFlats, flatId, personType.getAffairId()
                    ))
                    .hasWaiterPerson(existsWaiterPerson(allFlatLivers, personType.getAffairId()))
                    .hasDisablePerson(existsDisablePerson(allFlatLivers, personType.getAffairId()))
                    .build())
                .collect(Collectors.toList());
        }
    }

    private boolean existsWaiterPerson(final List<PersonType> allFlatLivers, final String affairId) {
        return nonNull(allFlatLivers) && allFlatLivers.stream()
            .filter(personType -> Objects.equals(affairId, personType.getAffairId()))
            .anyMatch(personType -> "1".equals(personType.getWaiter()));
    }

    private boolean existsDisablePerson(final List<PersonType> allFlatLivers, final String affairId) {
        return nonNull(allFlatLivers) && allFlatLivers.stream()
            .filter(personType -> Objects.equals(affairId, personType.getAffairId()))
            .map(PersonType::getAddInfo)
            .filter(Objects::nonNull)
            .anyMatch(addInfo -> "1".equals(addInfo.getIsDisable()));
    }

    private String retrieveResettlementDirection(
        final List<CompensationFlatType> compensationFlats, final String flatId, final String affairId
    ) {
        if (isNull(compensationFlats)) {
            return null;
        }

        final boolean isCompensationFlat = compensationFlats.stream()
            .filter(compensationFlat -> isNull(affairId) || isNull(compensationFlat.getAffairId())
                || affairId.equals(compensationFlat.getAffairId()))
            .anyMatch(compensationFlat -> Objects.equals(compensationFlat.getFlatId(), flatId));

        return isCompensationFlat ? "1" : null;
    }

    private List<PersonType> getAllLiversInHouse(final HouseToResettle houseToResettle) {
        return personDocumentService.fetchByUnom(houseToResettle.getRealEstateUnom())
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());
    }

    private String retrieveCadastralNumber(final HouseToResettle houseToResettle) {
        return ofNullable(houseToResettle.getRealEstateId())
            .flatMap(realEstateDocumentService::fetchById)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateDataType::getCadastralNums)
            .map(RealEstateDataType.CadastralNums::getCadastralNum)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(RealEstateDataType.CadastralNums.CadastralNum::getValue)
            .orElse(null);
    }
}
