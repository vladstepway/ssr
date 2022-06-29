package ru.croc.ugd.ssr.dto.egrn;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CreateEgrnBuildingRequestDto {

    private final String cadastralNumber;
    private final String unom;
    private final String address;
    private final String realEstateDocumentId;
    private final String ccoDocumentId;
}
