package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FlatsResponse {

    private List<FlatResponse> flats;

    public List<FlatResponse> getFlats() {
        if (isNull(flats)) {
            flats = new ArrayList<>();
        }
        return flats;
    }

}
