package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrAdministrativeDocumentsInfoMapper;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrAdministrativeDocumentsInfoMapper.DgpToMfrAdministrativeDocumentsInfoMapperInput;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.integrationflow.MfrFlowData;
import ru.croc.ugd.ssr.model.api.chess.CcoFlatInfoResponse;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrAdministrativeDocumentsInfo;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.integrationflow.IntegrationFlowService;

/**
 *  2. Сведения о РД при равнозначном переселении
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdministrativeDocumentsInfoMfrFlowService
    extends AbstractMfrFlowService<DgpToMfrAdministrativeDocumentsInfo> {

    private final DgpToMfrAdministrativeDocumentsInfoMapper dgpToMfrAdministrativeDocumentsInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final IntegrationFlowService integrationFlowService;

    public void sendAdministrativeDocumentsInfo(
        final PersonType personType, final String letterId, final NewFlat newFlat
    ) {
        sendAdministrativeDocumentsInfo(personType, letterId, newFlat, false);
    }

    public void sendAdministrativeDocumentsInfo(
        final PersonType personType, final String letterId, final NewFlat newFlat, final boolean duplicateSendingEnabled
    ) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendAdministrativeDocumentsInfoFlow(personType, letterId, newFlat, duplicateSendingEnabled);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendAdministrativeDocumentsInfoFlow(
        final PersonType personType, final String letterId, final NewFlat newFlat, final boolean duplicateSendingEnabled
    ) {
        final DgpToMfrAdministrativeDocumentsInfoMapperInput mapperInput = getMapperInput(personType, newFlat);
        if (shouldSendFlow(mapperInput, letterId, duplicateSendingEnabled)) {
            final DgpToMfrAdministrativeDocumentsInfo dgpToMfrAdministrativeDocumentsInfo =
                dgpToMfrAdministrativeDocumentsInfoMapper.toDgpToMfrAdministrativeDocumentsInfo(mapperInput);
            final MfrFlowData mfrFlowData = dgpToMfrAdministrativeDocumentsInfoMapper.toMfrFlowData(
                dgpToMfrAdministrativeDocumentsInfo, letterId
            );
            sendToMfr(dgpToMfrAdministrativeDocumentsInfo, mfrFlowData);
        }
    }

    private boolean shouldSendFlow(
        final DgpToMfrAdministrativeDocumentsInfoMapperInput mapperInput,
        final String letterId,
        final boolean duplicateSendingEnabled
    ) {
        final String affairId = ofNullable(mapperInput)
            .map(DgpToMfrAdministrativeDocumentsInfoMapperInput::getPersonType)
            .map(PersonType::getAffairId)
            .orElse(null);
        return nonNull(affairId) && nonNull(letterId)
            && (duplicateSendingEnabled
            || !integrationFlowService.existsSentDgpToMfrAdministrativeDocumentsInfoFlow(affairId, letterId));
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_ADMINISTRATIVE_DOCUMENTS_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrAdministrativeDocuments();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrAdministrativeDocuments();
    }

    @Override
    protected int getFlowNumber() {
        return 2;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_ADMINISTRATIVE_DOCUMENTS_INFO;
    }

    private DgpToMfrAdministrativeDocumentsInfoMapperInput getMapperInput(
        final PersonType personType, final NewFlat newFlat
    ) {
        final CcoFlatInfoResponse ccoFlatInfoResponse = capitalConstructionObjectService.getCcoChessFlatInfoByUnom(
            newFlat.getUnom(), newFlat.getFlatNumber()
        );

        return DgpToMfrAdministrativeDocumentsInfoMapperInput.builder()
            .personType(personType)
            .newFlat(newFlat)
            .ccoFlatInfoResponse(ccoFlatInfoResponse)
            .build();
    }
}
