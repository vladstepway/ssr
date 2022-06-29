package ru.croc.ugd.ssr.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.PersonNotificationProperties;

import java.util.Collections;
import java.util.List;

/**
 * Сервис отправки уведомлений по статусу загрузки жителей.
 */
@Slf4j
@AllArgsConstructor
@Service
public class PersonNotificationService {

    private static final String START_PERSON_UPLOAD_SUBJECT = "Запущен процесс загрузки жителей";
    private static final String START_PERSON_UPLOAD_MSG_TEMPLATE =
        "Пользователем \"%s\" запущен процесс загрузки жителей из файла \"%s\".%s";
    private static final String END_PERSON_UPLOAD_SUBJECT = "Процесс загрузки жителей завершен";
    private static final String END_PERSON_UPLOAD_MSG_TEMPLATE =
        "Загрузка файла с жителями \"%s\" завершена.<br/>Загружено: %s записей.%s%s";
    private static final String SIGNATURE = "<br/><br/> -- <br/> С уважением, <br/>"
        + "Департамент градостроительной политики и <br/> строительства города Москвы";

    private final MailService mailService;
    private final PersonNotificationProperties reportProperties;

    /**
     * Отправляет уведомление о начале загрузки жителей.
     *
     * @param fileName Название файла.
     * @param userName Имя пользователя.
     */
    public void startPersonUpload(final String fileName, final String userName) {
        mailService.sendMail(
            Collections.singletonList(reportProperties.getRecipientRole()),
            START_PERSON_UPLOAD_SUBJECT,
            String.format(START_PERSON_UPLOAD_MSG_TEMPLATE, userName, fileName, SIGNATURE)
        );
    }

    /**
     * Отправляет уведомление об окончании загрузки жителей.
     *
     * @param fileName Название файла.
     * @param processedRecordCount Количество обработанных записей.
     * @param exceptions Ошибки при загрузке жителей.
     */
    public void endPersonUpload(final String fileName, final int processedRecordCount, final List<String> exceptions) {
        String totalExceptions = "";
        if (!exceptions.isEmpty()) {
            totalExceptions = "<br/>Ошибки в ходе загрузки: " + String.join("<br/>", exceptions);
        }

        mailService.sendMail(
            Collections.singletonList(reportProperties.getRecipientRole()),
            END_PERSON_UPLOAD_SUBJECT,
            String.format(END_PERSON_UPLOAD_MSG_TEMPLATE, fileName, processedRecordCount, totalExceptions, SIGNATURE));
    }

}
