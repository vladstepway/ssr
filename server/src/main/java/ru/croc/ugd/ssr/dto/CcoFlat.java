package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

/**
 * Информация о квартире из окса.
 */
@Value
@Builder
public class CcoFlat {

    private final BigInteger unom;
    private final String address;
    private final String flatNumber;

}
