package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateShippingDateResponseDto {
    private Boolean isExist;
    private Boolean canBeReplaced;
}
