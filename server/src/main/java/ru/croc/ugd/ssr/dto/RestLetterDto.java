package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestLetterDto {

    private final String id;
    private final String fileLink;
}
