package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Информация о cco из окса.
 */
@Value
@Builder
public class CcoInfo {

    private final String address;
    private final String unom;
    private final String cadNumber;
    private final String psDocumentId;
    private final List<String> districts;
    private final List<String> areas;
    private final Integer flatCount;

}
