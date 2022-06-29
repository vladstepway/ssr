package ru.croc.ugd.ssr.model.api.chess;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatsHouseResponse {

    private String inProgress;
    private String done;
    private String court;

}
