package ru.croc.ugd.ssr.integration.service.flows;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.mapper.DisabledPersonMapper;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;

import java.util.List;

/**
 * Сервис 19 потока. Сведения о маломобильных жителях.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisabledPersonFlowService {

    private final MessageUtils messageUtils;
    private final EnoCreator enoCreator;
    private final MqetpmvProperties mqetpmvProperties;
    private final IntegrationPropertyConfig propertyConfig;
    private final IntegrationProperties integrationProperties;
    private final DisabledPersonMapper disabledPersonMapper;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;

    @Value("${ugd.ssr.disabled-person.enabled:false}")
    private boolean disabledPersonFlowEnabled;

    public void sendDisabledPersonsInfoFlow(final List<DisabledPersonDocument> disabledPersonDocuments) {
        if (disabledPersonFlowEnabled) {
            try {
                sendDisabledPersonsToDgi(disabledPersonDocuments);
            } catch (Exception e) {
                log.error("Unable to send disabled person flow to dgi: {}", e.getMessage());
            }
        } else {
            log.debug("Disabled person flow isn't sent: sending is disabled");
        }
    }

    private void sendDisabledPersonsToDgi(final List<DisabledPersonDocument> disabledPersons) {
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getDisabledPerson(), EnoSequenceCode.UGD_SSR_ENO_DISABLED_PERSON_SEQ
        );
        final String message = messageUtils.createCoordinateTaskMessage(
            eno,
            disabledPersonMapper.toSuperServiceDgpDisabledPersons(disabledPersons)
        );
        ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getDisabledPersonFlowTaskOutProfile(),
            message,
            eno,
            propertyConfig.getDisabledPerson(),
            IntegrationFlowType.DGP_TO_DGI_DISABLED_PERSON_INFO,
            integrationProperties.getXmlExportDisabledPerson()
        );
    }
}
