package ru.croc.ugd.ssr.mq.listener;

import static java.util.Objects.nonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;

import javax.annotation.PostConstruct;

/**
 * Слушаем шину для получения статусов.
 */
@Service
public class ElkStatusListener {

    private static final Logger LOG = LoggerFactory.getLogger(ElkStatusListener.class);
    private final ElkUserNotificationService elkUserNotificationService;
    private StatusListener statusListener;

    /**
     * Конструктор.
     *
     * @param elkUserNotificationService сервис по отправке нотификаций
     */
    public ElkStatusListener(ElkUserNotificationService elkUserNotificationService) {
        this.elkUserNotificationService = elkUserNotificationService;
    }

    /**
     * Метод для подписи на события о приходящем сообщении в очереди.
     */
    @PostConstruct
    public void subscribe() {
        if (nonNull(statusListener)) {
            statusListener.subscribe(this::process);
        }
    }

    /**
     * Метод обработки входящего сообщения.
     *
     * @param message входящее сообщение
     */
    public void process(String message) {
        LOG.info("Получено сообщение из шины");
        LOG.info(message);

        elkUserNotificationService.parseReceiveElkMessage(message);
    }

    @Autowired(required = false)
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

}
