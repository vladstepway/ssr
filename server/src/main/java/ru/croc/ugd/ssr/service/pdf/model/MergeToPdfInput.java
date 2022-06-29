package ru.croc.ugd.ssr.service.pdf.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MergeToPdfInput {
    private byte[] source;
    private InputFileFormat fileFormat;
}
