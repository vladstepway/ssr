package ru.croc.ugd.ssr.dto.flatdemo;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatDemoParticipantDto;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class RestFlatDemoDto {

    private final RestFlatAppointmentDto flatAppointment;
    private final LocalDate date;
    private final String letterId;
    private final String annotation;
    private final List<RestFlatDemoParticipantDto> participants;
}
