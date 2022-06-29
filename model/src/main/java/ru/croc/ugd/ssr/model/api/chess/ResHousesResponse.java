package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ResHousesResponse {

    private List<HousesResponse> resHouses;

    public List<HousesResponse> getResHouses() {
        if (isNull(resHouses)) {
            resHouses = new ArrayList<>();
        }
        return resHouses;
    }
}
