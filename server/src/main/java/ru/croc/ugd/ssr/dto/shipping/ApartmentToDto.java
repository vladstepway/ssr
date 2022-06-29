package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApartmentToDto {

    private String uid;

    private String unomNew;

    private String cadNumberNew;

    private String address;

    private String entranceNew;

    private String floorNew;

    private String flatNumberNew;

    private Integer roomNumberNew;
}
