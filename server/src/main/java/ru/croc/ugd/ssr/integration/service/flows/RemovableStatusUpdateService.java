package ru.croc.ugd.ssr.integration.service.flows;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPKeyIssueType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPRemovalStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSettleFlatInfoType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.reinform.cdp.exception.RIException;
import ru.reinform.cdp.utils.mapper.JsonMapper;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.util.Collections;
import java.util.List;


/**
 * Сервис обновления статусов квартир в заселяемом доме.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemovableStatusUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(RemovableStatusUpdateService.class);

    private final IntegrationProperties integrationProperties;
    private final SendRestUtils restUtils;
    private final JsonMapper jsonMapper;

    @Value("${common.playground.url}")
    private String playgroundUrl;

    /**
     * Получение сообщения со статусом квартиры. Получение 4 потока - статус из сообщения.
     *
     * @param flat квартира
     */
    public void receiveStatusFlat(final FlowReceivedMessageDto<SuperServiceDGPRemovalStatusType> flat) {
        try {
            updateRemovalStatus(
                flat.getParsedMessage().getFlatNumber(),
                flat.getParsedMessage().getRemovalStatus(),
                flat.getParsedMessage().getNewUnom(),
                flat.getParsedMessage().getNewCadnum()
            );
        } catch (Exception e) {
            log.error("Unable to process flat status: {}", e.getMessage(), e);
        }
    }

    /**
     * Обновление статуса квартиры. Получение 8 потока - статус "Договор подписан" (код 6).
     *
     * @param flat квартира
     */
    public void updateStatusFlat(SuperServiceDGPSettleFlatInfoType flat) {
        updateRemovalStatus(
            flat.getFlatNumber(),
            Collections.singletonList("6"),
            flat.getNewUnom(),
            flat.getNewCadnum()
        );
    }

    /**
     * Обновление статуса квартиры. Передача 9 потока - статус "Выданы ключи" (код 13).
     *
     * @param flat квартира
     */
    public void updateStatusFlat(SuperServiceDGPKeyIssueType flat) {
        updateRemovalStatus(
            flat.getFlatNumber(),
            Collections.singletonList("13"),
            flat.getNewUnom(),
            flat.getNewCadnum()
        );
    }

    /**
     * Обновление статуса квартиры.
     *
     * @param flatNumber     номер квартиры
     * @param removalStatus  статус
     * @param unom           unom дома
     * @param cadNum         кадастровый номер дома
     */
    public void updateRemovalStatus(
        String flatNumber,
        List<String> removalStatus,
        String unom,
        String cadNum
    ) {
        if (StringUtils.isBlank(unom)) {
            return;
        }

        final FlatPojo flatPojo = new FlatPojo();
        flatPojo.setNumber(flatNumber);
        flatPojo.setRemovalStatus(removalStatus);

        try {
            restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsUpdateFlatController(),
                HttpMethod.POST,
                jsonMapper.writeObject(flatPojo),
                String.class,
                unom,
                cadNum
            );
        } catch (RIException exception) {
            LOG.warn("Building with unom {} and cadastr {} doesnt found", unom, cadNum);
        }
    }

    /**
     * FlatPojo.
     */
    @Getter
    @Setter
    class FlatPojo {
        private String number;
        private List<String> removalStatus;
    }
}

