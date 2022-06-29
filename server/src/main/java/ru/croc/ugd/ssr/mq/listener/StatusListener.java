package ru.croc.ugd.ssr.mq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Листенер статусов доставки уведомления в ЕЛК.
 */
public class StatusListener {

    private static final Logger LOG = LoggerFactory.getLogger(StatusListener.class);

    private List<Consumer<String>> subscribers = new ArrayList<>();

    /**
     * Получение сообщения из шины.
     *
     * @param message сообщение
     */
    @JmsListener(destination = "${ibm.mq.queue.elk.statusInc}", containerFactory = "etpmvFactory")
    public void receiveMessage(Message<String> message) {
        LOG.info("Received message");

        for (Consumer<String> subscriber : subscribers) {
            subscriber.accept(message.getPayload());
        }
    }

    /**
     * Подписка.
     *
     * @param subscriber подписчик
     */
    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

}
