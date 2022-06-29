package ru.croc.ugd.ssr.integration.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import ru.croc.cdp.eventmanager.listener.EventProcessor;
import ru.croc.cdp.eventmanager.model.BusEventResponse;
import ru.croc.ugd.ssr.dto.ipev.IpevOrrResponseDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.mapper.IpevLogMapper;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;
import ru.croc.ugd.ssr.service.document.IpevLogDocumentService;
import ru.croc.ugd.ssr.service.ipev.IpevEventService;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.math.BigInteger;

/**
 * IpevEventProcessor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpevEventProcessor implements EventProcessor {

    private static final String EVENT_TYPE = "UGDNPC_ORR_REGISTRY_INFORMATION";

    private final IntegrationProperties integrationProperties;
    private final SendRestUtils restUtils;
    private final IpevLogDocumentService ipevLogDocumentService;
    private final IpevLogMapper ipevLogMapper;
    private final IpevEventService ipevEventService;

    @Value("${common.playground.url}")
    private String playgroundUrl;

    @Value("${ugd.ssr.ipev.enabled:false}")
    private boolean ipevEnabled;

    @NotNull
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Обработка входящего события.
     *
     * @param eventType тип события, в общем случае совпадает с ответом метода getEventType().
     * @param eventId   идентификатор события в системе транспорта (BUS).
     * @param event     тело события (id документа и время).
     * @return результат обработки события.
     */
    @Override
    public String processEvent(
        final @NotNull String eventType, final @NotNull BigInteger eventId, final @NotNull BusEventResponse event
    ) {
        if (ipevEnabled) {
            final IpevOrrResponseDto ipevOrrResponseDto = restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getOrrRegistryEvents(),
                HttpMethod.GET,
                null,
                IpevOrrResponseDto.class,
                event.getDocumentID()
            );

            final IpevLogDocument document = ipevLogMapper.toIpevLogDocument(eventId, event, ipevOrrResponseDto);
            ipevLogDocumentService.createDocument(document, true, "startProcessEvent");

            ipevEventService.processEvent(ipevOrrResponseDto, document);
        } else {
            log.debug("Ipev event is disabled: eventId = {}, documentId = {}", eventId, event.getDocumentID());
        }
        return null;
    }
}
