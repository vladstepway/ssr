package ru.croc.ugd.ssr.service.excel.model.process;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * Processor parameters.
 */
@Data
@Builder
public class ProcessParameters {
    private @Singular Map<String, String> processingParams;
}
