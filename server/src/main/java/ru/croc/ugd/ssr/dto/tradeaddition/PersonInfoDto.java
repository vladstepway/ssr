package ru.croc.ugd.ssr.dto.tradeaddition;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonInfoDto {
    private final String personId;
    private final String personFio;
    private final String personDocumentId;
}
