package ru.croc.ugd.ssr.controller.contractappointment;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.ContractAppointmentApi;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentBookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookRequestDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPrebookResponseDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentTimetableDto;
import ru.croc.ugd.ssr.service.contractappointment.RestExternalContractAppointmentService;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class ExternalContractAppointmentController implements ContractAppointmentApi {

    private final RestExternalContractAppointmentService externalContractAppointmentService;


    @Override
    public ResponseEntity<ExternalRestContractAppointmentDto> fetchByEno(
        @NotNull @Valid final String eno
    ) {
        return ResponseEntity.ok(externalContractAppointmentService.fetchByEno(eno));
    }

    @Override
    public ResponseEntity<ExternalRestContractAppointmentTimetableDto> fetchTimetable(
        @NotNull @Valid final String cipId,
        @Valid @RequestParam(value = "days", required = false, defaultValue = "7") final Integer days,
        @Valid @RequestParam(value = "startFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startFrom
    ) {
        return ResponseEntity.ok(externalContractAppointmentService.fetchTimetable(cipId, days, startFrom));
    }

    @Override
    public ResponseEntity<ExternalRestContractAppointmentPrebookResponseDto> prebookSlot(
        @Valid @RequestBody final ExternalRestContractAppointmentPrebookRequestDto body
    ) {
        return ResponseEntity.ok(externalContractAppointmentService.prebookSlot(body));
    }

    @Override
    public ResponseEntity<Void> bookSlot(
        @Valid @RequestBody final ExternalRestContractAppointmentBookRequestDto body,
        @Valid final String contractAppointmentDocumentId
    ) {
        externalContractAppointmentService.bookSlot(body, contractAppointmentDocumentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ExternalRestContractAppointmentPersonCheckDto> checkPerson(
        @NotNull @Valid String snils,
        @NotNull @Valid String ssoId) {
        return ResponseEntity.ok(externalContractAppointmentService.check(snils, ssoId));
    }
}
