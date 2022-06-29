package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtInfoType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.service.courtinfo.CourtInfoService;

/**
 * Сведения о судах договора. 21 поток.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourtInfoFlowService {

    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final MqetpmvProperties mqetpmvProperties;
    private final CourtInfoService courtInfoService;
    private final IntegrationProperties integrationProperties;

    public void processMessage(
        final String eno,
        final SuperServiceDGPCourtInfoType courtInfoType,
        final IntegrationFlowDocument integrationFlowDocument
    ) {
        try {
            courtInfoService.createOrUpdateCourtInfo(courtInfoType);
            sendStatus(DgpToDgiCourtInfoFlowStatus.OK, eno, integrationFlowDocument);
        } catch (Exception e) {
            log.warn("Unable to process dgi court info flow message: {}", e.getMessage(), e);
            sendStatus(DgpToDgiCourtInfoFlowStatus.FAILED, eno, integrationFlowDocument);
        }
    }

    private void sendStatus(
        final DgpToDgiCourtInfoFlowStatus status,
        final String eno,
        final IntegrationFlowDocument integrationFlowDocument
    ) {
        ssrMqetpmvFlowService.sendFlowStatusMessage(
            integrationFlowDocument,
            mqetpmvProperties.getCourtFlowStatusOutProfile(),
            eno,
            status.getCode(),
            status.getTitle(),
            integrationProperties.getXmlExportCourtInfo()
        );
    }
}
