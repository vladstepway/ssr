package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FullOksHousesResponse {

    private StatsHouseResponse statOksHouses;

    private List<HousesResponse> resHouses;

    private List<FullFlatResponse> flats;

    public List<HousesResponse> getResHouses() {
        if (isNull(resHouses)) {
            resHouses = new ArrayList<>();
        }
        return resHouses;
    }

    public List<FullFlatResponse> getFlats() {
        if (isNull(flats)) {
            flats = new ArrayList<>();
        }
        return flats;
    }

    public StatsHouseResponse getStatOksHouses() {
        return statOksHouses;
    }

    public void setStatOksHouses(StatsHouseResponse statsHouseResponse) {
        this.statOksHouses = statsHouseResponse;
    }

}
