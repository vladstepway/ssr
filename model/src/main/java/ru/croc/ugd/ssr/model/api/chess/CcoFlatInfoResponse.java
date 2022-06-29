package ru.croc.ugd.ssr.model.api.chess;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CcoFlatInfoResponse {

    private double kitchenSq;
    private double livingSquare;
    private double totalSquareWithSummer;
    private int roomAmount;
    private int floor;
    private String ccoId;
    private String ccoAddress;
    private String flatStatus;

}
