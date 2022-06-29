package ru.croc.ugd.ssr.model.api.chess;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PeopleResponse {

    private String fio;
    private String liveStatus;
    private String link;

}
