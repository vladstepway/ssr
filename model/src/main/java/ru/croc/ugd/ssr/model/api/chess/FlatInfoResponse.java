package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
public class FlatInfoResponse {

    private double kitchenSq;
    private String flatStatus;
    private List<String> defectList;
    private List<FamilyResponse> family;

    public double getKitchenSq() {
        return kitchenSq;
    }

    public String getFlatStatus() {
        return flatStatus;
    }

    public List<FamilyResponse> getFamily() {
        if (isNull(family)) {
            family = new ArrayList<>();
        }
        return family;
    }

    public List<String> getDefectList() {
        if (isNull(defectList)) {
            defectList = new ArrayList<>();
        }
        return defectList;
    }

}
