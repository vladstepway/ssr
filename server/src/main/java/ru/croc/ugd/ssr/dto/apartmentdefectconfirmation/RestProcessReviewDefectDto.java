package ru.croc.ugd.ssr.dto.apartmentdefectconfirmation;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestProcessReviewDefectDto {
    /**
     * Причина отклонения
     */
    private final String rejectionReason;
    /**
     * ИД дефектов с согласованными изменениями
     */
    private final List<String> approvedDefectIds;
    /**
     * ИД дефектов с отклоненными изменениями
     */
    private final List<String> rejectedDefectIds;
}
