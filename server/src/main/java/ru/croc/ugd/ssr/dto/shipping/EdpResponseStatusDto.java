package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EdpResponseStatusDto {

    private ShippingFlowStatus shippingFlowStatus;

    private String edpResponseStatusText;
}
