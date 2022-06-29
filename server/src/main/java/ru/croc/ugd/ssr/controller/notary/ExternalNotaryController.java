package ru.croc.ugd.ssr.controller.notary;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.generated.api.NotaryApi;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApplicationDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryBookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryListDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryTimetableDto;
import ru.croc.ugd.ssr.service.notary.RestExternalNotaryService;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
public class ExternalNotaryController implements NotaryApi {

    private final RestExternalNotaryService externalNotaryService;

    @Override
    public ResponseEntity<RestNotaryPersonCheckDto> check(
        @NotNull @Valid final String snils,
        @NotNull @Valid final String ssoId
    ) {
        return ResponseEntity.ok(externalNotaryService.check(snils, ssoId));
    }

    @Override
    public ResponseEntity<RestNotaryListDto> fetchAll() {
        return ResponseEntity.ok(externalNotaryService.fetchAll());
    }

    @Override
    public ResponseEntity<RestNotaryApplicationDto> fetchNotaryApplication(
        @NotNull @Valid final String eno
    ) {
        return ResponseEntity.ok(externalNotaryService.fetchNotaryApplication(eno));
    }

    @Override
    public ResponseEntity<RestNotaryTimetableDto> fetchTimetable(
        @NotNull @Valid @RequestParam(value = "notaryId") final String notaryId,
        @Valid @RequestParam(value = "days", required = false, defaultValue = "7") final Integer days,
        @Valid @RequestParam(value = "startFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startFrom
    ) {
        return ResponseEntity.ok(externalNotaryService.fetchTimetable(notaryId, startFrom, days));
    }

    @Override
    public ResponseEntity<RestNotaryPrebookSlotResponseDto> prebookSlot(
        @Valid @RequestBody final RestNotaryPrebookSlotRequestDto body
    ) {
        return ResponseEntity.ok(externalNotaryService.prebookSlot(body));
    }

    @Override
    public ResponseEntity<Void> bookSlot(
        @Valid @RequestBody RestNotaryBookSlotRequestDto body,
        @Valid @RequestParam(value = "isSsrBooking", required = false, defaultValue = "false") Boolean isSsrBooking
    ) {
        externalNotaryService.bookSlot(body, isSsrBooking);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
