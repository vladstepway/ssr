package ru.croc.ugd.ssr.controller.flatappointment;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.FlatAppointmentApi;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentTimetableDto;
import ru.croc.ugd.ssr.service.flatappointment.RestExternalFlatAppointmentService;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class ExternalFlatAppointmentController implements FlatAppointmentApi {

    private final RestExternalFlatAppointmentService externalFlatAppointmentService;

    @Override
    public ResponseEntity<ExternalRestFlatAppointmentDto> fetchByEno(
        @NotNull @Valid final String eno
    ) {
        return ResponseEntity.ok(externalFlatAppointmentService.fetchByEno(eno));
    }

    @Override
    public ResponseEntity<ExternalRestFlatAppointmentPersonCheckDto> checkPerson(
        @NotNull @Valid String snils,
        @NotNull @Valid String ssoId
    ) {
        return ResponseEntity.ok(externalFlatAppointmentService.check(snils, ssoId));
    }

    @Override
    public ResponseEntity<ExternalRestFlatAppointmentTimetableDto> fetchTimetable(
        @NotNull @Valid final String cipId,
        @Valid @RequestParam(value = "days", required = false, defaultValue = "7") final Integer days,
        @Valid @RequestParam(value = "startFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startFrom
    ) {
        return ResponseEntity.ok(externalFlatAppointmentService.fetchTimetable(cipId, days, startFrom));
    }

    @Override
    public ResponseEntity<ExternalRestFlatAppointmentPrebookResponseDto> prebookSlot(
        @Valid @RequestBody final ExternalRestFlatAppointmentPrebookRequestDto body
    ) {
        return ResponseEntity.ok(externalFlatAppointmentService.prebookSlot(body));
    }

    @Override
    public ResponseEntity<Void> bookSlot(
        @Valid @RequestBody final ExternalRestFlatAppointmentBookRequestDto body,
        @Valid final String flatAppointmentDocumentId
    ) {
        externalFlatAppointmentService.bookSlot(body, flatAppointmentDocumentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
