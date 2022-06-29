package ru.croc.ugd.ssr.dto.contractdigitalsign;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ContractDigitalSignMoveDateDto {

    private final LocalDate appointmentDate;
    private final String contractAppointmentDocumentId;
}
