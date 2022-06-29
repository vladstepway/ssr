package ru.croc.ugd.ssr.service.flatappointment;

import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentTimetableDto;

import java.time.LocalDate;

public interface RestExternalFlatAppointmentService {

    ExternalRestFlatAppointmentDto fetchByEno(final String eno);

    ExternalRestFlatAppointmentPersonCheckDto check(final String snils, final String ssoId);

    ExternalRestFlatAppointmentTimetableDto fetchTimetable(
        final String cipId,
        final Integer days,
        final LocalDate startFrom
    );

    ExternalRestFlatAppointmentPrebookResponseDto prebookSlot(final ExternalRestFlatAppointmentPrebookRequestDto body);

    void bookSlot(
        final ExternalRestFlatAppointmentBookRequestDto body, final String flatAppointmentDocumentId
    );
}
