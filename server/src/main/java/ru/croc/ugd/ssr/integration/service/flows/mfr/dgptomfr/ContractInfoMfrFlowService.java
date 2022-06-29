package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrContractInfoMapper;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrContractInfoMapper.DgpToMfrContractInfoMapperInput;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrContractInfo;
import ru.croc.ugd.ssr.service.integrationflow.IntegrationFlowService;
import ru.croc.ugd.ssr.utils.PersonUtils;

/**
 *  4. Сведения о договоре при равнозначном переселении
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractInfoMfrFlowService extends AbstractMfrFlowService<DgpToMfrContractInfo> {

    private final DgpToMfrContractInfoMapper dgpToMfrContractInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;
    private final IntegrationFlowService integrationFlowService;

    public void sendContractInfo(final PersonType personType, final String contractOrderId) {
        sendContractInfo(personType, contractOrderId, false);
    }

    public void sendContractInfo(
        final PersonType personType, final String contractOrderId, final boolean duplicateSendingEnabled
    ) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendContractInfoFlow(personType, contractOrderId, duplicateSendingEnabled);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendContractInfoFlow(
        final PersonType personType, final String contractOrderId, final boolean duplicateSendingEnabled
    ) {
        final DgpToMfrContractInfoMapperInput mapperInput = getMapperInput(personType, contractOrderId);
        if (shouldSendFlow(mapperInput, duplicateSendingEnabled)) {
            final DgpToMfrContractInfo dgpToMfrContractInfo = dgpToMfrContractInfoMapper
                .toDgpToMfrContractInfo(mapperInput);
            final MfrFlowData mfrFlowData = dgpToMfrContractInfoMapper.toMfrFlowData(dgpToMfrContractInfo);
            sendToMfr(dgpToMfrContractInfo, mfrFlowData);
        }
    }

    private boolean shouldSendFlow(
        final DgpToMfrContractInfoMapperInput mapperInput, final boolean duplicateSendingEnabled
    ) {
        final String affairId = ofNullable(mapperInput)
            .map(DgpToMfrContractInfoMapperInput::getPersonType)
            .map(PersonType::getAffairId)
            .orElse(null);
        final String contractOrderId = ofNullable(mapperInput)
            .map(DgpToMfrContractInfoMapperInput::getContract)
            .map(PersonType.Contracts.Contract::getOrderId)
            .orElse(null);
        final String contractStatus = ofNullable(mapperInput)
            .map(DgpToMfrContractInfoMapperInput::getContract)
            .map(dgpToMfrContractInfoMapper::toContractStatus)
            .orElse(null);
        return nonNull(affairId) && nonNull(contractOrderId)
            && (duplicateSendingEnabled
            || !integrationFlowService.existsSentDgpToMfrContractInfoFlow(affairId, contractOrderId, contractStatus));
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_CONTRACT_INFO_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrContracts();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrContract();
    }

    @Override
    protected int getFlowNumber() {
        return 4;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_CONTRACT_INFO;
    }

    private DgpToMfrContractInfoMapperInput getMapperInput(
        final PersonType personType, final String contractOrderId
    ) {
        return PersonUtils.getContractByOrderId(personType, contractOrderId)
            .map(contract -> DgpToMfrContractInfoMapperInput.builder()
                .contract(contract)
                .personType(personType)
                .newFlat(PersonUtils.getNewFlatByOrderId(personType, contract.getOrderId()))
                .build())
            .orElse(null);
    }
}
