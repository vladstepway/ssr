package ru.croc.ugd.ssr.dto.offerletterparsing;

import lombok.Builder;
import lombok.Value;

/**
 * Разбор письма с предложением.
 */
@Value
@Builder
public class RestOfferLetterParsingDto {

    /**
     * Идентификатор разбора письма с предложением.
     */
    private final String id;
    /**
     * Адрес отселяемой квартиры.
     */
    private final String addressFrom;
    /**
     * ИД семьи.
     */
    private final String affairId;
    /**
     * ИД файла письма с предложением в FileStore.
     */
    private final String fileStoreId;
    /**
     * ИД письма с предложением.
     * */
    private final String letterId;
    /**
     * Автоматически распознанные данные о квартире.
     */
    private final RestAutomaticallyParsedFlatDataDto automaticallyParsedFlatData;
}
