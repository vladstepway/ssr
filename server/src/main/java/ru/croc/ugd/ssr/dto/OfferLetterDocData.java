package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferLetterDocData {
    final String address;
    final String cadNumber;
    final String fullSquareMeters;
    final String fullSquareNoTerraceMeters;
    final String livingSquareMeters;
    final String roomsAmount;
    final String floor;
    final String flatNumber;
    final String unom;
}
