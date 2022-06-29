package ru.croc.ugd.ssr.integration.service;

import static org.apache.commons.lang.StringUtils.isEmpty;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.ObjectFactory;
import ru.croc.ugd.ssr.mq.interop.MqSender;
import ru.croc.ugd.ssr.mq.validation.XmlValidation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сервис по работе с xml по отправке-получению из шины етп мв.
 */
@Service
@AllArgsConstructor
public class EtpService {

    private static final Logger LOG = LoggerFactory.getLogger(EtpService.class);
    private final Environment environment;
    private final MqSender mqSender;
    private final XmlUtils xmlUtils;
    private final XmlValidation xmlValidation;

    /**
     * Метод для формирования выгрузки xml.
     *
     * @param messageObject message
     * @param queueName наименование очереди
     * @param clazz clazz
     * @param <T> T
     */
    public <T> void exportMessage(T messageObject, String queueName, Class<T> clazz) {
        final String xmlString =
            xmlUtils.transformObjectToXmlString(messageObject, clazz, ObjectFactory.class);
        exportXml(xmlString, queueName);
    }

    /**
     * Метод для формирования выгрузки xml.
     *
     * @param xmlString xml
     * @param queueName наименование очереди
     */
    public void exportXml(String xmlString, String queueName) {
        //сохраним полученную xml
        saveExportXmlFile(xmlString);
        //валидируем, если включен флаг валидации
        if (Boolean.parseBoolean(environment.getProperty("ibm.mq.validateBeforeSendNotification"))) {
            boolean isValid = xmlValidation.validateXml(xmlString);
            // Передаем в очередь
            if (isValid) {
                sendXml(xmlString, queueName);
            }
        } else {
            // Передаем в очередь
            sendXml(xmlString, queueName);
        }
    }

    /**
     * Отправка сообщения в очередь.
     *
     * @param xml       сообщение
     * @param queueName наименование очереди
     */
    private void sendXml(String xml, String queueName) {
        if (!Boolean.parseBoolean(environment.getProperty("ibm.mq.lockAppQueue"))) {
            LOG.debug("Отправка в очередь {} сообщения {}", queueName, xml);
            mqSender.send(queueName, xml);
        } else {
            LOG.warn("Уведомление не отправлено в очередь, т.к. отправка в очередь отключена");
        }
    }

    /**
     * Сохранение экспортируемого xml.
     *
     * @param xmlString xml
     */
    public void saveExportXmlFile(String xmlString) {
        ZonedDateTime now = ZonedDateTime.now();
        String absolutePath = environment.getProperty("ibm.mq.exportXmlAbsoluteDir");
        if (isEmpty(absolutePath)) {
            LOG.error("Переменная ibm.mq.exportXmlAbsoluteDir пуста. Файл не может быть создан.");
            return;
        }
        String fileName = "exportXml-" + now.toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"));

        xmlUtils.writeXmlFile(absolutePath, fileName, xmlString);
    }

    /**
     * Сохранение импортируемого xml.
     *
     * @param xmlString xml
     */
    public void saveImportXmlFile(String xmlString) {
        ZonedDateTime now = ZonedDateTime.now();
        // Путь, куда сохранять файлы
        String absolutePath = environment.getProperty("ibm.mq.importXmlAbsoluteDir");
        if (isEmpty(absolutePath)) {
            LOG.info("Свойство ibm.mq.importXmlAbsoluteDir не задано");
            return;
        }
        String fileName = "importXml-" + now.toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"));

        xmlUtils.writeXmlFile(absolutePath, fileName, xmlString);
    }

}
