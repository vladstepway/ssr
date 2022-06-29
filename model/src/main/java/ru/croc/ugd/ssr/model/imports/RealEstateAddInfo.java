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
public class RealEstateAddInfo {

    private String unom;
    private String geoData;
    private List<WearAddInfo> wears;
    private String procZa;
    private String procProtiv;

}
