package ru.croc.ugd.ssr.dto.mqetpmv.flow;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateFile;

import java.util.List;

@Value
@Builder
public class ImportFlowMessageDto {
    private final List<ImportCoordinateTask> importCoordinateTasks;
    private final List<CoordinateFile> coordinateFiles;
}
