package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrContractProjectInfoMapper;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrContractProjectInfoMapper.DgpToMfrContractProjectInfoMapperInput;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrContractProjectInfo;
import ru.croc.ugd.ssr.service.integrationflow.IntegrationFlowService;
import ru.croc.ugd.ssr.utils.PersonUtils;

/**
 *  3. Сведения о проекте договора при равнозначном переселении
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractProjectInfoMfrFlowService extends AbstractMfrFlowService<DgpToMfrContractProjectInfo> {

    private final DgpToMfrContractProjectInfoMapper dgpToMfrContractProjectInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;
    private final IntegrationFlowService integrationFlowService;

    public void sendContractProjectInfo(final PersonType personType, final String contractOrderId) {
        sendContractProjectInfo(personType, contractOrderId, false);
    }

    public void sendContractProjectInfo(
        final PersonType personType, final String contractOrderId, final boolean duplicateSendingEnabled
    ) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendContractProjectInfoFlow(personType, contractOrderId, duplicateSendingEnabled);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendContractProjectInfoFlow(
        final PersonType personType, final String contractOrderId, final boolean duplicateSendingEnabled
    ) {
        final DgpToMfrContractProjectInfoMapperInput mapperInput = getMapperInput(personType, contractOrderId);
        if (shouldSendFlow(mapperInput, duplicateSendingEnabled)) {
            final DgpToMfrContractProjectInfo dgpToMfrContractInfo = dgpToMfrContractProjectInfoMapper
                .toDgpToMfrContractProjectInfo(mapperInput);
            final MfrFlowData mfrFlowData = dgpToMfrContractProjectInfoMapper.toMfrFlowData(dgpToMfrContractInfo);
            sendToMfr(dgpToMfrContractInfo, mfrFlowData);
        }
    }

    private boolean shouldSendFlow(
        final DgpToMfrContractProjectInfoMapperInput mapperInput, final boolean duplicateSendingEnabled
    ) {
        final String affairId = ofNullable(mapperInput)
            .map(DgpToMfrContractProjectInfoMapperInput::getPersonType)
            .map(PersonType::getAffairId)
            .orElse(null);
        final String contractOrderId = ofNullable(mapperInput)
            .map(DgpToMfrContractProjectInfoMapperInput::getContract)
            .map(PersonType.Contracts.Contract::getOrderId)
            .orElse(null);
        final String contractProjectStatus = ofNullable(mapperInput)
            .map(DgpToMfrContractProjectInfoMapperInput::getContract)
            .map(dgpToMfrContractProjectInfoMapper::toContractStatus)
            .orElse(null);
        return nonNull(affairId) && nonNull(contractOrderId)
            && (duplicateSendingEnabled
            || !integrationFlowService.existsSentDgpToMfrContractProjectInfoFlow(
            affairId, contractOrderId, contractProjectStatus
        ));
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_CONTRACT_PROJECT_INFO_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrContractProjects();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrContractProject();
    }

    @Override
    protected int getFlowNumber() {
        return 3;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_CONTRACT_PROJECT_INFO;
    }

    private DgpToMfrContractProjectInfoMapperInput getMapperInput(
        final PersonType personType, final String contractOrderId
    ) {
        return PersonUtils.getContractByOrderId(personType, contractOrderId)
            .map(contract -> DgpToMfrContractProjectInfoMapperInput.builder()
                .contract(contract)
                .personType(personType)
                .newFlat(PersonUtils.getNewFlatByOrderId(personType, contract.getOrderId()))
                .build())
            .orElse(null);
    }
}
