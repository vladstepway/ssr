package ru.croc.ugd.ssr.integration.bus.processor;

import org.springframework.stereotype.Component;
import ru.reinform.cdp.bus.rest.listener.MessageProcessor;

import javax.annotation.Nonnull;

/**
 * Обработчик сообщений ПСО типа "ugd_v5_responseBRFinal".
 */
@Component
public class ResponseBrFinalRequestProcessorV5 implements MessageProcessor {

    @Nonnull
    @Override
    public String getMessageType() {
        return "ugd_v5_responseBRFinal";
    }

    @Override
    public String processMessage(@Nonnull String messageType, long  messageId, @Nonnull String message) {
        return null; // TODO implementation
    }
}
