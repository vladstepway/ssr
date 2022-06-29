package ru.croc.ugd.ssr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Мета данные о загрузке жителей.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonUploadMetadata {

    /**
     * Количество загруженных жителей.
     */
    private int count;

    /**
     * Ошибки при загрузке жителей.
     */
    private List<String> exceptions;
}
