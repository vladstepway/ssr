package ru.croc.ugd.ssr.model.imports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateEntranceAddInfo {

    private String unom;
    private List<FlatAddInfo> flats;
    private String entrances;
    private String floors;

}
