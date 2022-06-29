package ru.croc.ugd.ssr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderPropDto {

    private String headerCellText;
    private int colWith;
    private short cellColorIndexValue;
}
