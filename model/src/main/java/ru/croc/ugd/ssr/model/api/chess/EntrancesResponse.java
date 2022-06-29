package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class EntrancesResponse {

    private List<EntranceResponse> entrances;

    public List<EntranceResponse> getEntrances() {
        if (isNull(entrances)) {
            entrances = new ArrayList<>();
        }
        return entrances;
    }

}
