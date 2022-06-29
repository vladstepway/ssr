package ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.service.flows.mfr.config.MfrFlowProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.dgptomfr.DgpToMfrReleaseFlatInfoMapper;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatFreeType;
import ru.croc.ugd.ssr.model.integration.mfr.DgpToMfrReleaseFlatInfo;

/**
 *  18. Сведения об освобождении отселяемого помещения
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseFlatInfoMfrFlowService
    extends AbstractMfrFlowService<DgpToMfrReleaseFlatInfo> {

    private final DgpToMfrReleaseFlatInfoMapper dgpToMfrReleaseFlatInfoMapper;
    private final MfrFlowProperties mfrFlowProperties;

    public void sendReleaseFlatInfo(final SuperServiceDGPFlatFreeType info) {
        if (mfrFlowProperties.isEnabled()) {
            try {
                sendReleaseFlatInfoFlow(info);
            } catch (Exception e) {
                log.error("Unable to send {} flow to mfr: {}", getFlowNumber(), e.getMessage());
            }
        } else {
            log.debug("Mfr flow {} isn't sent: sending is disabled", getFlowNumber());
        }
    }

    private void sendReleaseFlatInfoFlow(final SuperServiceDGPFlatFreeType info) {
        final DgpToMfrReleaseFlatInfo dgpToMfrReleaseFlatInfo =
            dgpToMfrReleaseFlatInfoMapper.toDgpToMfrReleaseFlatInfo(info);
        sendToMfr(dgpToMfrReleaseFlatInfo);
    }

    @Override
    protected String getEnoSequenceCode() {
        return EnoSequenceCode.UGD_SSR_ENO_MFR_RELEASE_FLAT_INFO_SEQ;
    }

    @Override
    protected String getGuNumber() {
        return propertyConfig.getMfrReleaseFlat();
    }

    @Override
    protected String getExportXmlDirectory() {
        return integrationProperties.getXmlExportMfrReleaseFlat();
    }

    @Override
    protected int getFlowNumber() {
        return 18;
    }

    @Override
    protected IntegrationFlowType getIntegrationFlowType() {
        return IntegrationFlowType.DGP_TO_MFR_RELEASE_FLAT_INFO;
    }
}
