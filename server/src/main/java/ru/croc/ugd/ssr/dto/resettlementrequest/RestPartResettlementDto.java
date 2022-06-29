package ru.croc.ugd.ssr.dto.resettlementrequest;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class RestPartResettlementDto {
    /**
     * Дата начала переселения.
     */
    private final LocalDate startResettlementDate;
    /**
     * Сведения о заселяемом доме
     */
    private final RestCcoDto ccoInfo;
    /**
     * Список ИД переселяемых квартир.
     */
    private final List<String> flatIds;
}
