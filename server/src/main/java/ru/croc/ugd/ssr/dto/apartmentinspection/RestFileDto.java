package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestFileDto {
    /**
     * Содержимое файла.
     */
    private final byte[] content;
    /**
     * Имя файла.
     */
    private final String fileName;
}
