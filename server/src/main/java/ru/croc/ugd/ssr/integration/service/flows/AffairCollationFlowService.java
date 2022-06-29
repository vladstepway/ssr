package ru.croc.ugd.ssr.integration.service.flows;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationType;

/**
 * Сервис 18 потока. Сведения для сверки жителей.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AffairCollationFlowService {

    private final MessageUtils messageUtils;
    private final IntegrationProperties integrationProperties;
    private final IntegrationPropertyConfig propertyConfig;
    private final EnoCreator enoCreator;
    private final MqetpmvProperties mqetpmvProperties;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    /**
     * Отправка запроса на сверку жителей.
     *
     * @param superServiceDgpAffairCollation superServiceDgpAffairCollation
     */
    public String send(final SuperServiceDGPAffairCollationType superServiceDgpAffairCollation) {
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getAffairCollation(),
            EnoSequenceCode.UGD_SSR_ENO_DGI_AFFAIR_COLLATION_SEQ
        );
        final String message = messageUtils.createCoordinateTaskMessage(eno, superServiceDgpAffairCollation);
        ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getAffairCollationTaskOutProfile(),
            message,
            eno,
            propertyConfig.getAffairCollation(),
            IntegrationFlowType.DGP_TO_DGI_AFFAIR_COLLATION_INFO,
            integrationProperties.getXmlExportAffairCollation()
        );

        log.info(
            "Sent affair collation to dgi: eno = {}, unom = {}, flatNumber = {}, affairId = {}",
            eno,
            superServiceDgpAffairCollation.getUnom(),
            superServiceDgpAffairCollation.getFlatNumber(),
            superServiceDgpAffairCollation.getAffairId()
        );
        return eno;
    }
}
