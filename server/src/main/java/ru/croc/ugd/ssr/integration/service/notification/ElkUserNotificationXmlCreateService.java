package ru.croc.ugd.ssr.integration.service.notification;

import ru.croc.ugd.ssr.model.PersonDocument;

import java.util.function.Function;

/**
 * Сервис по созданию XML сообщений-нотификаций ЛК/Почты для отправки в ЕТП МВ.
 */
public interface ElkUserNotificationXmlCreateService {

    /**
     * Формирование XML в ЕЛК о начале расселения.
     *
     * @param personDocument житель
     * @param cipAddress     Адрес ЦИПа
     * @param cipPhone       Телефон ЦИПа
     * @return XML
     */
    String createStartRenovationXml(PersonDocument personDocument,
                                    String cipAddress,
                                    String cipPhone);

    /**
     * Формирование XML в ЕЛК о письме с предложением.
     *
     * @param personDocument житель
     * @param cipAddress     Адрес ЦИПа
     * @param cipPhone       Телефон ЦИПа
     * @param fileUrl        ссылка на файл
     * @return XML
     */
    String createOfferLetterXml(PersonDocument personDocument,
                                String cipAddress,
                                String cipPhone,
                                String fileUrl);

    /**
     * Формирование XML в ЕЛК после осмотра квартиры.
     *
     * @param personDocument житель
     * @param cipAddress     Адрес ЦИПа
     * @param cipPhone       Телефон ЦИПа
     * @param fileUrl        ссылка на файл
     * @return XML
     */
    String createFlatInspectionXml(PersonDocument personDocument,
                                   String cipAddress,
                                   String cipPhone,
                                   String fileUrl);

    /**
     * Формирование XML в ЕЛК о согласии по квартире.
     *
     * @param personDocument житель
     * @return XML
     */
    String createFlatAgreementXml(PersonDocument personDocument);

    /**
     * Формирование XML в ЕЛК об отказе на квартиру.
     *
     * @param personDocument житель
     * @param cipAddress     Адрес ЦИПа
     * @param cipPhone       Телефон ЦИПа
     * @return XML
     */
    String createFlatRefusalXml(PersonDocument personDocument, String cipAddress, String cipPhone);

    /**
     * Формирование XML в ЕЛК о подписанном договоре.
     *
     * @param personDocument    житель
     * @param cipAddress        Адрес ЦИПа
     * @param governmentAddress Адрес Управы района
     * @param newFlatAddress    Адрес новой квартиры
     * @param oldFlatAddress    Адрес старой квартиры
     * @return XML
     */
    String createSignedContractXml(PersonDocument personDocument,
                                   String cipAddress,
                                   String governmentAddress,
                                   String newFlatAddress,
                                   String oldFlatAddress);

    /**
     * Формирование XML в ЕЛК о готовности договора.
     *
     * @param personDocument житель
     * @param cipAddress     Адрес ЦИПа
     * @param cipPhone       Телефон ЦИПа
     * @return XML
     */
    String createContractReadyXml(PersonDocument personDocument,
                                  String cipAddress,
                                  String cipPhone);

    /**
     * Формирование запроса в ЕЛК об устранении дефектов.
     * @param personDocument документ
     * @param cipAddress цип адрес
     * @param cipPhone цип телефон
     * @return xml
     */
    String createFixDefectsXml(PersonDocument personDocument, String cipAddress, String cipPhone);

    /**
     * Creates notification request for shipping.
     * @param personDocument personDocument
     * @param notificationDescriptor notificationDescriptor
     * @param paramsResolver paramsResolver
     * @return notification request
     */
    String createXmlByDescriptor(
        final PersonDocument personDocument,
        final NotificationDescriptor notificationDescriptor,
        final Function<String, String> paramsResolver);
}
