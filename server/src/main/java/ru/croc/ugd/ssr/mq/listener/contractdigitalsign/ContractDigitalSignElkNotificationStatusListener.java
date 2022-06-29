package ru.croc.ugd.ssr.mq.listener.contractdigitalsign;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.dto.ElkUserNotificationDto;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.service.contractdigitalsign.ContractDigitalSignService;

/**
 * Листенер статусов по уведомлениям, направленным в ЕЛК,
 * в рамках Многостороннего подписания договора с использованием УКЭП.
 */
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.contract-digital-sign.enabled")
public class ContractDigitalSignElkNotificationStatusListener {

    private final ElkUserNotificationService elkUserNotificationService;
    private final ContractDigitalSignService contractDigitalSignService;

    /**
     * Чтение из очереди.
     *
     * @param message сообщение
     */
    @JmsListener(destination = "${ibm.mq.queue.elk-ms.statusInc}", containerFactory = "etpmvFactory")
    public void receiveMessage(final Message<String> message) {
        final String messagePayload = message.getPayload();

        log.debug("Contract digital sign notification status received, payload: {}", messagePayload);

        final ElkUserNotificationDto elkUserNotificationDto = elkUserNotificationService
            .parseReceiveElkMessage(messagePayload);
        contractDigitalSignService.addNotificationStatusToContractDigitalSign(elkUserNotificationDto);
    }
}
