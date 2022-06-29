package ru.croc.ugd.ssr.controller.shipping;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.BookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.ShippingBookingTotalDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateResponseDto;
import ru.croc.ugd.ssr.generated.api.ShippingApi;
import ru.croc.ugd.ssr.generated.dto.RestBookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestBookingSlotListResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestCopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestDayOrIntervalValidationResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestDeclineShippingApplicationRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalBookingSlotListRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalPrebookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestMoveShippingDateRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestPrebookingResponseDto;
import ru.croc.ugd.ssr.mapper.BookingSlotMapper;
import ru.croc.ugd.ssr.mapper.ShippingMapper;
import ru.croc.ugd.ssr.service.BookingSlotService;
import ru.croc.ugd.ssr.service.shipping.ShippingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;

/**
 * Контроллер помощи в переселении.
 */
@RestController
@ResponseBody
@AllArgsConstructor
@Slf4j
public class ShippingController implements ShippingApi {

    private final ShippingService shippingService;
    private final ShippingMapper shippingMapper;
    private final BookingSlotService bookingSlotService;
    private final BookingSlotMapper bookingSlotMapper;

    @Override
    public ResponseEntity<RestCheckResponseDto> check(@Valid String snils, @Valid String ssoId) {
        final BookingInformation bookingInformation = shippingService
            .fetchBookingInformationIfValidForPerson(snils, ssoId);

        return new ResponseEntity<>(shippingMapper.toRestCheckResponseDto(
            bookingInformation), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCheckResponseDto> checkProd(@Valid String snils, @Valid String ssoId) {
        final BookingInformation bookingInformation = shippingService.checkProd(snils, ssoId);

        return new ResponseEntity<>(
            shippingMapper.toRestCheckResponseDto(bookingInformation),
            HttpStatus.OK
        );
    }

    @GetMapping(value = "/internal/shipping/check")
    public RestCheckResponseDto internalCheck(
        @RequestParam final String personDocumentId
    ) {
        final BookingInformation bookingInformation = shippingService.internalCheck(personDocumentId);
        return shippingMapper.toRestCheckResponseDto(bookingInformation);
    }

    @Override
    public ResponseEntity<RestBookingSlotListResponseDto> timetable(
        @RequestBody @Valid RestBookingSlotListRequestDto body) {
        final BookingSlotListDto bookingSlotListDto =
            bookingSlotService.getAll(bookingSlotMapper.toBookingSlotListRequestDto(body));

        return new ResponseEntity<>(bookingSlotMapper.toRestBookingSlotListResponseDto(bookingSlotListDto),
            HttpStatus.OK);
    }

    /**
     * Получить расписание, пропуская проверку snils.
     *
     * @param body RestBookingSlotListRequestDto
     * @return RestBookingSlotListResponseDto
     */
    @PostMapping(value = "/internal/shipping/timetable")
    public RestBookingSlotListResponseDto internalTimetable(
        @RequestBody @Valid RestInternalBookingSlotListRequestDto body
    ) {
        final BookingSlotListDto bookingSlotListDto = bookingSlotService.getAllInternal(body);
        return bookingSlotMapper.toRestBookingSlotListResponseDto(bookingSlotListDto);
    }

    @Override
    public ResponseEntity<RestPrebookingResponseDto> prebooking(
        @RequestBody @Valid RestPrebookingRequestDto body) {
        final PreBookingResultDto preBookingResultDto =
            bookingSlotService.preBookSlot(bookingSlotMapper.toPreBookingRequestDto(body));

        return new ResponseEntity<>(bookingSlotMapper.toRestPrebookingResponseDto(preBookingResultDto),
            HttpStatus.OK);
    }

    @PostMapping(value = "/internal/shipping/prebooking")
    public ResponseEntity<RestPrebookingResponseDto> internalPrebooking(
        @RequestBody @Valid RestInternalPrebookingRequestDto body
    ) {
        final PreBookingResultDto preBookingResultDto = bookingSlotService.preBookSlotInternal(body);
        return new ResponseEntity<>(bookingSlotMapper.toRestPrebookingResponseDto(preBookingResultDto),
            HttpStatus.OK);
    }

    /**
     * Удаление рабочего дня.
     *
     * @param body Дата или интервал удаления.
     * @param hasConfirm Подтверждение удаления дней по которым нет записи.
     * @return Заблокированные для удаления рабочие дни.
     */
    @DeleteMapping(value = "/shipping/day-schedules")
    public List<ShippingBookingTotalDto> deleteDaySchedules(
        @RequestBody @Valid final RestCopingDaySchedulesRequestDto body,
        @RequestParam(required = false, defaultValue = "false") @Valid final boolean hasConfirm
    ) {
        return bookingSlotService.deleteDaySchedules(bookingSlotMapper
            .toDeletingDaySchedulesRequestDto(body), hasConfirm);
    }

    @Override
    public ResponseEntity<RestBookingResponseDto> booking(
        @RequestBody @Valid RestBookingRequestDto body) {
        final BookingResultDto bookingResultDto =
            bookingSlotService.bookSlot(bookingSlotMapper.toBookingRequestDto(body));

        return new ResponseEntity<>(bookingSlotMapper.toRestBookingResponseDto(bookingResultDto),
            HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> copingDaySchedules(
        @PathVariable("id") final String id, @RequestBody @Valid final RestCopingDaySchedulesRequestDto body,
        @RequestParam @Valid final Boolean confirm) {
        bookingSlotService
            .copyDaySchedules(id, confirm, bookingSlotMapper.toCopingDaySchedulesRequestDto(body));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> declineApplicationById(
        @PathVariable("id") final String applicationId,
        @RequestBody @Valid RestDeclineShippingApplicationRequestDto body
    ) {
        shippingService.declineApplicationById(applicationId, body.getDeclineReason(), body.getDeclineDateTime());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> moveShippingDate(
        @PathVariable("id") final String applicationId,
        @RequestBody @Valid RestMoveShippingDateRequestDto body
    ) {
        shippingService.moveShippingDate(applicationId, shippingMapper.toMoveShippingDateRequestDto(body));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestDayOrIntervalValidationResponseDto> validatesDayOrDayIntervalsSchedules(
        @RequestBody @Valid RestCopingDaySchedulesRequestDto body) {
        final ValidateShippingDateResponseDto validateShippingDateResponseDto = bookingSlotService
            .validateShippingDateOrInterval(shippingMapper.toValidateShippingDateRequestDto(body));
        return new ResponseEntity<>(shippingMapper
            .toRestDayOrIntervalValidationResponseDto(validateShippingDateResponseDto), HttpStatus.OK);
    }

    @PostMapping("/shipping/clean-empty-bookings")
    public void cleanEmptyBookings() {
        bookingSlotService.cleanEmptyBookings();
    }

    @PostMapping("/shipping/recreate-booking-slots")
    public void recreateBookingSlots() {
        bookingSlotService.recreateBookingSlots();
    }

    @GetMapping(value = "/shipping/day-schedules/total")
    public List<ShippingBookingTotalDto> fetchShippingBookingTotal(
        @RequestParam final String startDate,
        @RequestParam final String endDate,
        @RequestParam final String district
    ) {
        return bookingSlotService.fetchShippingBookingTotal(
            LocalDate.parse(startDate), LocalDate.parse(endDate), district
        );
    }

    @GetMapping(value = "/shipping-application/{documentId}/pdf")
    public ResponseEntity<byte[]> generateShippingApplicationPdfReport(
        @PathVariable("documentId") final String documentId
    ) {
        final byte[] content = shippingService.generateShippingApplicationPdfReport(documentId);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        final String filename = "Заявление.pdf";
        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
            .filename(filename, StandardCharsets.UTF_8)
            .build();
        headers.setContentDisposition(contentDisposition);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
