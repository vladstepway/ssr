package ru.croc.ugd.ssr.model.api.chess;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HousesResponse {

    private String unom;
    private String address;
    private Integer inProgress;
    private Integer done;
    private Integer court;
    private Double inProgressPrc;
    private Double donePrc;
    private String link;
    private Integer flatCount;

}
