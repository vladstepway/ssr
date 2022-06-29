package ru.croc.ugd.ssr.dto.tradeaddition;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EstateInfoDto {
    private final String unom;
    private final String cadNumber;
    private final String address;
    private final String flatNumber;
    private final List<String> rooms;
}
