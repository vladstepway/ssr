package ru.croc.ugd.ssr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Информация о бизнес-процессе и задаче для Activiti BPM.
 */
@Data
public class ProcessInfo {

    /**
     * Идентификатор бизнес-процесса.
     */
    @NotNull
    @ApiModelProperty(value = "Идентификатор бизнес-процесса.", required = true)
    private String processInstanceId;

    /**
     * Идентификатор выполняемой задачи.
     */
    @NotNull
    @ApiModelProperty(value = "Идентификатор выполняемой задачи.", required = true)
    private String taskId;

}
