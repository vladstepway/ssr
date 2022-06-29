package ru.croc.ugd.ssr.dto.resettlementrequest;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestCcoDto {
    /**
     * Адрес заселяемого дома.
     */
    private final String settleAddress;
    /**
     * УНОМ заселяемого дома.
     */
    private final String settleUnom;
    /**
     * Кадастровый номер заселяемого дома.
     */
    private final String settleCadastralNumber;
}
