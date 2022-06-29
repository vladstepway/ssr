package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
public class FullFlatResponse {

    private String entrance;
    private String flatNumber;
    private int floor;
    private int roomsCount;
    private int invalid;
    private int communal;
    private int removalType;
    private int defect;
    private String link;
    private double totalSq;
    private double livingSq;
    private double kitchenSq;
    private String flatStatus;
    private List<FamilyResponse> family;
    private List<String> defectList;

    public String getEntrance() {
        return entrance;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public int getFloor() {
        return floor;
    }

    public int getRoomsCount() {
        return roomsCount;
    }

    public int getInvalid() {
        return invalid;
    }

    public int getCommunal() {
        return communal;
    }

    public int getRemovalType() {
        return removalType;
    }

    public int getDefect() {
        return defect;
    }

    public String getLink() {
        return link;
    }

    public double getTotalSq() {
        return totalSq;
    }

    public double getLivingSq() {
        return livingSq;
    }

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
