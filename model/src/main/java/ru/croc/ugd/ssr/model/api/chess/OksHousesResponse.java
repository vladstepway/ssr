package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class OksHousesResponse {

    private List<HousesResponse> oksHouses;

    public List<HousesResponse> getOksHouses() {
        if (isNull(oksHouses)) {
            oksHouses = new ArrayList<>();
        }
        return oksHouses;
    }
}
