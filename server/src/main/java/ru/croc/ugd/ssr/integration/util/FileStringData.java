package ru.croc.ugd.ssr.integration.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileStringData {
    private String fileAbsolutePath;
    private String fileContent;
}
