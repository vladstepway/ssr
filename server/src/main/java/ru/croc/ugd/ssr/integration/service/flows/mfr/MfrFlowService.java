package ru.croc.ugd.ssr.integration.service.flows.mfr;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.FlowTypeDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.flows.mfr.enums.DgpToMfrFlowStatus;
import ru.croc.ugd.ssr.integration.service.flows.mfr.enums.MfrToDgpFlowDataType;
import ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp.MfrToDgpFlowService;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;

@Slf4j
@Service
@AllArgsConstructor
public class MfrFlowService {

    private final MqetpmvProperties mqetpmvProperties;
    private final ApplicationContext applicationContext;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final IntegrationProperties integrationProperties;

    public void processMessage(
        final String eno,
        final CoordinateTaskData coordinateTaskData,
        final IntegrationFlowDocument integrationFlowDocument,
        final String serviceBeanName
    ) {
        try {
            ofNullable(serviceBeanName)
                .map(beanName -> applicationContext.getBean(beanName, MfrToDgpFlowService.class))
                .ifPresent(mfrToDgpFlowService -> mfrToDgpFlowService.processMessage(
                    coordinateTaskData, integrationFlowDocument
                ));
            sendStatus(DgpToMfrFlowStatus.OK, eno, integrationFlowDocument);
        } catch (Exception e) {
            log.warn("Unable to process mfr flow message: {}", e.getMessage(), e);
            sendStatus(DgpToMfrFlowStatus.FAILED, eno, integrationFlowDocument);
        }
    }

    public FlowTypeDto retrieveFlowTypeDto(final CoordinateTaskData coordinateTaskData) {
        final MfrToDgpFlowDataType mfrToDgpFlowDataType = retrieveMfrToDgpFlowDataType(coordinateTaskData);
        return FlowTypeDto.builder()
            .serviceCode(mfrToDgpFlowDataType.getCode())
            .integrationFlowType(mfrToDgpFlowDataType.getIntegrationFlowType())
            .flowMessageProcessorBeanName(mfrToDgpFlowDataType.getServiceBeanName())
            .build();
    }

    private void sendStatus(
        final DgpToMfrFlowStatus status, final String eno, final IntegrationFlowDocument integrationFlowDocument
    ) {
        ssrMqetpmvFlowService.sendFlowStatusMessage(
            integrationFlowDocument,
            mqetpmvProperties.getMfrFlowStatusOutProfile(),
            eno,
            status.getCode(),
            status.getTitle(),
            integrationProperties.getXmlExportMfrStatus()
        );
    }

    private MfrToDgpFlowDataType retrieveMfrToDgpFlowDataType(final CoordinateTaskData coordinateTaskData) {
        final String serviceTypeCode = coordinateTaskData.getTask()
            .getFunctionTypeCode();
        final Object customData = coordinateTaskData.getData()
            .getParameter()
            .getAny();
        final MfrToDgpFlowDataType mfrToDgpFlowDataType = MfrToDgpFlowDataType.of(serviceTypeCode)
            .orElseThrow(() -> new SsrException(
                String.format("Unable to find flow type: serviceTypeCode = %s", serviceTypeCode)
            ));
        final Class<?> clazz = mfrToDgpFlowDataType.getClazz();
        if (isNull(customData) || !clazz.isInstance(customData)) {
            throw new SsrException(
                String.format(
                    "Unable to cast custom data to %s: serviceTypeCode = %s", clazz.getName(), serviceTypeCode
                )
            );
        }
        return mfrToDgpFlowDataType;
    }
}
