package ru.croc.ugd.ssr.dto.flatdemo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatDemoParticipantDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Данные для создания осмотра квартиры.
 */
@Value
@Builder
public class RestCreateFlatDemoDto {

    @JsonProperty("isPerformed")
    private final boolean isPerformed;
    private final LocalDate date;
    private final List<RestFlatDemoParticipantDto> participants;
    private final String letterId;
    private final String annotation;
    private final String flatAppointmentId;
}
