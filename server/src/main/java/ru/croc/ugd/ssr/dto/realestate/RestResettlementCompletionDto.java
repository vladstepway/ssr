package ru.croc.ugd.ssr.dto.realestate;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestResettlementCompletionDto {

    /**
     * Дата завершения переселения.
     */
    private final LocalDate date;
    /**
     * Номер документа о завершении переселения.
     */
    private final String documentNumber;
    /**
     * Дата документа о завершении переселения.
     */
    private final LocalDate documentDate;
    /**
     * ИД в FileStore файла документа о завершении переселения.
     */
    private final String fileStoreId;
}
