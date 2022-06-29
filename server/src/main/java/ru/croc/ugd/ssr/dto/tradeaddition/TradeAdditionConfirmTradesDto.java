package ru.croc.ugd.ssr.dto.tradeaddition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TradeAdditionConfirmTradesDto {

    @JsonProperty("isConfirmAction")
    private final boolean confirmAction;
}
