package ru.croc.ugd.ssr.model.api.chess;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CcoSolrResponse {

    private String id;
    private String address;
    private String area;
    private String district;
    private LocalDate completionDate;
}
