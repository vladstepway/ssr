package ru.croc.ugd.ssr.dto.resettlementrequest;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestFullResettlementDto {

    /**
     * Дата начала переселения.
     */
    private final LocalDate startResettlementDate;
    /**
     * Сведения о заселяемом доме.
     */
    private final RestCcoDto ccoInfo;
}
