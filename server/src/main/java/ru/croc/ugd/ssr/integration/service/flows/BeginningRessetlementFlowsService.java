package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPStartRemovalType;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.interop.QueueProperties;

/**
 * Сервис рассылки сведений о переселении. Поток 3.
 */
@Service
@RequiredArgsConstructor
public class BeginningRessetlementFlowsService {

    private final MessageUtils messageUtils;

    private final MqSender mqSender;

    private final QueueProperties queueProperties;

    private final XmlUtils xmlUtils;

    private final IntegrationProperties integrationProperties;

    private final IntegrationPropertyConfig integrationConfig;

    /**
     * Рассылка сведений о начале расселения.
     *
     * @param info
     *            сообщение
     */
    public void sendInfoBeginningResettlement(SuperServiceDGPStartRemovalType info) {

        final String taskMessage =
                messageUtils.createCoordinateTaskMessage(EnoSequenceCode.UGD_SSR_ENO_DGI_RESETELMENT_BEGINING_SEQ,
                        integrationConfig.getRessetelmentBegining(),
                        info);
        xmlUtils.writeXmlFile(taskMessage, integrationProperties.getXmlExportFlowThird());
        mqSender.send(queueProperties.getPersonBuildingsRequest(), taskMessage);

    }

}
