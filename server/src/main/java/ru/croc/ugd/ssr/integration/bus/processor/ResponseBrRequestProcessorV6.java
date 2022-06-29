package ru.croc.ugd.ssr.integration.bus.processor;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.parser.SmevResponseParser;
import ru.reinform.cdp.bus.rest.listener.MessageProcessor;

import javax.annotation.Nonnull;

/**
 * Обработчик сообщений ПСО типа "ugd_v6_responseBR".
 */
@Component
@AllArgsConstructor
public class ResponseBrRequestProcessorV6 implements MessageProcessor {

    private final SmevResponseParser smevResponseParser;

    @Nonnull
    @Override
    public String getMessageType() {
        return "ugd_v6_responseBR";
    }

    @Override
    public String processMessage(@Nonnull String messageType, long messageId, @Nonnull String message) {
        return smevResponseParser.processMessageSmev(message);
    }
}
