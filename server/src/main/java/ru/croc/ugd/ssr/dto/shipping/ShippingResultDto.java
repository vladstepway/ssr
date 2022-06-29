package ru.croc.ugd.ssr.dto.shipping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Фиксация результатов переезда.
 */
@Data
@ApiModel(description = "Фиксация результатов переезда.")
public class ShippingResultDto {

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

    /**
     * Результат переезда.
     */
    @ApiModelProperty(value = "Результат переезда.", required = true)
    private boolean result;

    /**
     * Комментарий. Должен быть заполнен, если результат неуспешный.
     */
    @ApiModelProperty(value = "Комментарий. Должен быть заполнен, если результат неуспешный.")
    private String comment;

}
