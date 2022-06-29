package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FullResHousesResponse {

    private StatsHouseResponse statResHouses;

    private List<HousesResponse> oksHouses;

    private List<FullFlatResponse> flats;

    public List<HousesResponse> getOksHouses() {
        if (isNull(oksHouses)) {
            oksHouses = new ArrayList<>();
        }
        return oksHouses;
    }

    public List<FullFlatResponse> getFlats() {
        if (isNull(flats)) {
            flats = new ArrayList<>();
        }
        return flats;
    }

    public StatsHouseResponse getStatResHouses() {
        return statResHouses;
    }

    public void setStatResHouses(StatsHouseResponse statsHouseResponse) {
        this.statResHouses = statsHouseResponse;
    }

}
