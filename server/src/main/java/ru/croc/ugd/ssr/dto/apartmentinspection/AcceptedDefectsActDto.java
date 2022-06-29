package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AcceptedDefectsActDto {
    /**
     * ИД файла принятого акта дефектов
     * */
    private final String acceptedDefectsActFileId;

}