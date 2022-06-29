package ru.croc.ugd.ssr.mq.interop;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * Сервис отправки сообщений в очередь.
 */
@Service
public class MqSender {

    private static final Logger LOG = LoggerFactory.getLogger(MqSender.class);

    private static final Integer RECEIVE_TIMEOUT = 5000;

    private final JmsTemplate jmsMessagingTemplate;

    /**
     * Конструктор.
     *
     * @param jmsMessagingTemplate шаблон.
     */
    public MqSender(JmsTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    /**
     * Отправка сообщения в очередь.
     *
     * @param queueName наименование очереди
     * @param body      тело
     */
    public void send(String queueName, String body) {
        LOG.info("Trying to send message");
        Objects.requireNonNull(jmsMessagingTemplate)
                .setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        if (StringUtils.isNotEmpty(queueName)) {
            jmsMessagingTemplate.convertAndSend(queueName, body);
            LOG.info("Message sent. Queue name: {}", queueName);
        } else {
            LOG.error("Name queue is empty. Message body"
                    + body); //TODO убрать когда все очереди етмв будут созданы
        }
    }

    /**
     * Получение сообщения.
     *
     * @param queueName наименование очереди
     * @return сообщение
     */
    public String receive(String queueName) {
        return Objects.requireNonNull(this.jmsMessagingTemplate).execute(session -> {
            try (MessageConsumer consumer = session.createConsumer(this.jmsMessagingTemplate.getDestinationResolver()
                    .resolveDestinationName(session, queueName, false))) {
                Message received = consumer.receive(RECEIVE_TIMEOUT);
                String result =
                        (String) Objects.requireNonNull(this.jmsMessagingTemplate.getMessageConverter())
                                .fromMessage(received);
                received.acknowledge();
                return result;
            }
        }, true);
    }
}
