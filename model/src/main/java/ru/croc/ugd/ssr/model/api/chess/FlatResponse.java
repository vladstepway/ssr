package ru.croc.ugd.ssr.model.api.chess;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FlatResponse {

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
    private String flatStatus;

}
