package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrPersonInfoMapper;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrPersonInfoMapper.DgpToMfrPersonInfoMapperType;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrPersonInfo;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  8. Сведения о жителях
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonInfoMfrFlowService extends AbstractMfrFlowService<DgpToMfrPersonInfo> {

    private final DgpToMfrPersonInfoMapper dgpToMfrPersonInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final PersonDocumentService personDocumentService;

    public void sendPersonInfo(final String affairId, final String sellId) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendPersonInfoFlow(affairId, sellId);
            } catch (Exception e) {
                log.error(
                    "Unable to send {} flow to mfr (affairId = {}, sellId = {}): {}",
                    getFlowNumber(),
                    affairId,
                    sellId,
                    e.getMessage()
                );
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    public void sendPersonInfo(final List<PersonDocument> personDocuments) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendPersonInfoFlow(personDocuments);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendPersonInfoFlow(final String affairId, final String sellId) {
        final DgpToMfrPersonInfo mapperInput = dgpToMfrPersonInfoMapper.toDgpToMfrPersonInfo(
            getMapperInput(affairId, sellId)
        );
        sendToMfr(mapperInput);
    }

    private void sendPersonInfoFlow(final List<PersonDocument> personDocuments) {
        final List<DgpToMfrPersonInfo> dgpToMfrContractInfos = dgpToMfrPersonInfoMapper
            .toDgpToMfrPersonInfoList(getMapperInputs(personDocuments));
        dgpToMfrContractInfos.forEach(this::sendToMfr);
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_PERSON_INFO_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrPersonInfo();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrPersonInfo();
    }

    @Override
    protected int getFlowNumber() {
        return 8;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_PERSON_INFO;
    }

    private DgpToMfrPersonInfoMapperType getMapperInput(final String affairId, final String sellId) {
        final List<PersonType> persons = personDocumentService.fetchByAffairId(affairId).stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());
        return DgpToMfrPersonInfoMapperType.builder()
            .affairId(affairId)
            .personTypes(persons)
            .sellId(sellId)
            .build();
    }

    private List<DgpToMfrPersonInfoMapperType> getMapperInputs(final List<PersonDocument> personDocuments) {
        return personDocuments.stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .filter(personType -> StringUtils.isNotBlank(personType.getAffairId()))
            .filter(StreamUtils.distinctByKey(PersonType::getAffairId))
            .collect(Collectors.groupingBy(PersonType::getAffairId))
            .entrySet()
            .stream()
            .map(data -> DgpToMfrPersonInfoMapperType.builder()
                .affairId(data.getKey())
                .personTypes(data.getValue())
                .build())
            .map(this::retrieveDgpToMfrPersonInfoMapperTypeWithSellIds)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<DgpToMfrPersonInfoMapperType> retrieveDgpToMfrPersonInfoMapperTypeWithSellIds(
        final DgpToMfrPersonInfoMapperType mapperInput
    ) {
        return ofNullable(mapperInput.getAffairId())
            .map(tradeAdditionDocumentService::fetchByAffairId)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .map(TradeAdditionType::getSellId)
            .filter(Objects::nonNull)
            .map(sellId -> mapperInput.toBuilder()
                .sellId(sellId)
                .build())
            .collect(Collectors.toList());
    }
}
