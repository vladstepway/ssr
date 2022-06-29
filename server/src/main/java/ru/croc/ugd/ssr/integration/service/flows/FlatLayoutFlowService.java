package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatLayoutType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatsLayout;

import java.util.List;

/**
 * Сервис 20 потока. Сведения о проектной квартирографии (проектных экспликациях).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlatLayoutFlowService {

    private final MessageUtils messageUtils;
    private final IntegrationProperties integrationProperties;
    private final IntegrationPropertyConfig propertyConfig;
    private final EnoCreator enoCreator;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    /**
     * Рассылка сведений о квартирографии.
     *
     * @param superServiceDgpFlatLayoutList список данных по квартирам
     */
    public void sendFlatsLayout(final List<SuperServiceDGPFlatLayoutType> superServiceDgpFlatLayoutList) {
        if (superServiceDgpFlatLayoutList.isEmpty()) {
            throw new SsrException("Empty superServiceDgpFlatLayoutList");
        }
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getFlatLayout(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_FLAT_LAYOUT_SEQ
        );
        final SuperServiceDGPFlatsLayout superServiceDgpFlatsLayout = new SuperServiceDGPFlatsLayout();
        superServiceDgpFlatsLayout.getSuperServiceDGPFlatLayout().addAll(superServiceDgpFlatLayoutList);
        final String message = messageUtils.createCoordinateTaskMessage(eno, superServiceDgpFlatsLayout);
        ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getFlatLayoutFlowTaskOutProfile(),
            message,
            eno,
            propertyConfig.getFlatLayout(),
            IntegrationFlowType.DGP_TO_DGI_FLAT_LAYOUT_INFO,
            integrationProperties.getXmlExportFlowTwentieth()
        );
    }
}
