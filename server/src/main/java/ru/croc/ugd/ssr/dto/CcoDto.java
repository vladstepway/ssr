package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.db.projection.BuildingData;

@Value
@Builder
public class CcoDto implements BuildingData {

    private final String id;
    private final String address;
    private final String unom;
    private final String subjectRfp1Json;
    private final String elementP7Json;
    private final String houseL1TypeJson;
    private final String houseL1Value;
    private final String townP4Json;
    private final String settlementP3Json;
    private final String localityP6Json;
    private final String corpL2TypeJson;
    private final String corpL2Value;
    private final String buildingL3TypeJson;
    private final String buildingL3Value;
}
