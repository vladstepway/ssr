package ru.croc.ugd.ssr.service.contractappointment;

import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentTimetableDto;

import java.time.LocalDate;

public interface RestExternalContractAppointmentService {

    ExternalRestContractAppointmentDto fetchByEno(final String eno);

    ExternalRestContractAppointmentTimetableDto fetchTimetable(
        final String cipId,
        final Integer days,
        final LocalDate startFrom
    );

    ExternalRestContractAppointmentPrebookResponseDto prebookSlot(
        final ExternalRestContractAppointmentPrebookRequestDto body
    );

    void bookSlot(final ExternalRestContractAppointmentBookRequestDto body, final String contractAppointmentDocumentId);

    ExternalRestContractAppointmentPersonCheckDto check(final String snils, final String ssoId);
}
