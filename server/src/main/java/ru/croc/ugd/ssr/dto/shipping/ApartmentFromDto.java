package ru.croc.ugd.ssr.dto.shipping;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ApartmentFromDto {

    private String uid;

    private String unomOld;

    private String cadNumberOld;

    private String flatNumberOld;

    private String address;

    private String entranceOld;

    private String floorOld;

    private List<String> roomNumber;

    private Integer roomNumberOld;

    private LocalDate moveDate;
}
