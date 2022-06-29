package ru.croc.ugd.ssr.dto.egrn;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateEgrnFlatRequestDto {

    private final String cadastralNumber;
    private final String flatId;
    private final String flatNumber;
    private final String unom;
    private final String realEstateDocumentId;
    private final String ccoDocumentId;
}
