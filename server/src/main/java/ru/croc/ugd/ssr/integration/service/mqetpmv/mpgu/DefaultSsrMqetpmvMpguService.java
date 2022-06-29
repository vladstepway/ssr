package ru.croc.ugd.ssr.integration.service.mqetpmv.mpgu;

import static org.apache.commons.lang.StringUtils.isEmpty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.ObjectFactory;
import ru.croc.ugd.ssr.mq.validation.XmlValidation;
import ru.reinform.cdp.mqetpmv.api.MqetpmvRemoteService;
import ru.reinform.cdp.mqetpmv.model.EtpOutboundMessage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultSsrMqetpmvMpguService implements SsrMqetpmvMpguService {

    private final Environment environment;
    private final XmlUtils xmlUtils;
    private final XmlValidation xmlValidation;
    private final SystemProperties systemProperties;
    private final MqetpmvRemoteService mqetpmvRemoteService;

    /**
     * Метод для формирования выгрузки xml.
     *
     * @param messageObject message
     * @param etpProfile    наименование профиля
     * @param clazz         clazz
     * @param <T>           T
     */
    @Override
    public <T> void exportMessage(final T messageObject, final String etpProfile, final Class<T> clazz) {
        final String xmlString =
            xmlUtils.transformObjectToXmlString(messageObject, clazz, ObjectFactory.class);
        exportXml(xmlString, etpProfile);
    }

    /**
     * Метод для формирования выгрузки xml.
     *
     * @param xmlString  xml
     * @param etpProfile наименование профиля
     */
    private void exportXml(final String xmlString, final String etpProfile) {
        //сохраним полученную xml
        saveExportXmlFile(xmlString);
        //валидируем, если включен флаг валидации
        if (Boolean.parseBoolean(environment.getProperty("ibm.mq.validateBeforeSendNotification"))) {
            boolean isValid = xmlValidation.validateXml(xmlString);
            // Передаем в очередь
            if (isValid) {
                sendXml(xmlString, etpProfile);
            }
        } else {
            // Передаем в очередь
            sendXml(xmlString, etpProfile);
        }
    }

    /**
     * Отправка сообщения в очередь.
     *
     * @param xml       сообщение
     * @param etpProfile наименование очереди
     */
    private void sendXml(final String xml, final String etpProfile) {
        if (!Boolean.parseBoolean(environment.getProperty("ibm.mq.lockAppQueue"))) {
            log.debug("Отправка в очередь {} сообщения {}", etpProfile, xml);
            final EtpOutboundMessage etpOutboundMessage = new EtpOutboundMessage();
            etpOutboundMessage.setSystemCode(systemProperties.getSystem());
            etpOutboundMessage.setEtpProfile(etpProfile);
            etpOutboundMessage.setMessage(xml);
            mqetpmvRemoteService.sendMessage(etpOutboundMessage);
        } else {
            log.warn("Уведомление не отправлено в очередь, т.к. отправка в очередь отключена");
        }
    }

    /**
     * Сохранение экспортируемого xml.
     *
     * @param xmlString xml
     */
    private void saveExportXmlFile(final String xmlString) {
        ZonedDateTime now = ZonedDateTime.now();
        String absolutePath = environment.getProperty("ibm.mq.exportXmlAbsoluteDir");
        if (isEmpty(absolutePath)) {
            log.error("Переменная ibm.mq.exportXmlAbsoluteDir пуста. Файл не может быть создан.");
            return;
        }
        String fileName = "exportXml-" + now.toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss-n"));

        xmlUtils.writeXmlFile(absolutePath, fileName, xmlString);
    }
}
