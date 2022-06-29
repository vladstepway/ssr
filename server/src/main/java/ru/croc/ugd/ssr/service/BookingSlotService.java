package ru.croc.ugd.ssr.service;

import ru.croc.ugd.ssr.dto.shipping.BookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.BookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListDto;
import ru.croc.ugd.ssr.dto.shipping.BookingSlotListRequestDto;
import ru.croc.ugd.ssr.dto.shipping.CopingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.DeletingDaySchedulesRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingRequestDto;
import ru.croc.ugd.ssr.dto.shipping.PreBookingResultDto;
import ru.croc.ugd.ssr.dto.shipping.ShippingBookingTotalDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateRequestDto;
import ru.croc.ugd.ssr.dto.shipping.ValidateShippingDateResponseDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalBookingSlotListRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestInternalPrebookingRequestDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Booking slot service.
 */
public interface BookingSlotService {

    /**
     * Retrieve available booking slot list.
     *
     * @param bookingSlotListRequestDto booking slot list request
     * @return result
     */
    BookingSlotListDto getAll(final BookingSlotListRequestDto bookingSlotListRequestDto);

    BookingSlotListDto getAllInternal(final RestInternalBookingSlotListRequestDto bookingSlotListRequestDto);

    /**
     * Pre-book slot for shipping.
     *
     * @param preBookingRequestDto pre-booking request
     * @return result
     */
    PreBookingResultDto preBookSlot(final PreBookingRequestDto preBookingRequestDto);

    PreBookingResultDto preBookSlotInternal(final RestInternalPrebookingRequestDto preBookingRequestDto);

    /**
     * Book slot for shipping.
     *
     * @param bookingRequestDto booking request
     * @return result
     */
    BookingResultDto bookSlot(final BookingRequestDto bookingRequestDto);

    void copyDaySchedules(
        final String id, final Boolean isConfirm, final CopingDaySchedulesRequestDto copingDaySchedulesRequestDto
    );

    /**
     * Определяет забронирован или существует ли расписание на день или интервал.
     *
     * @param validateShippingDateRequestDto validateShippingDateRequestDto
     * @return ValidateShippingDateResponseDto
     */
    ValidateShippingDateResponseDto validateShippingDateOrInterval(
        final ValidateShippingDateRequestDto validateShippingDateRequestDto);


    /**
     * Remove booking by bookingId.
     *
     * @param bookingId bookingId
     */
    void removeBooking(final String bookingId);

    /**
     * Check exists booking by bookingId.
     *
     * @param bookingId bookingId
     * @return booking exists
     */
    boolean checkExistsBooking(final String bookingId);

    /**
     * Remove booking and book preBooking by bookingId.
     *
     * @param bookingId bookingId
     */
    void removeBookedAndBookPreBooked(final String bookingId);

    /**
     * Получение информации о бронированиях для помощи переезде.
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @param district  district
     * @return информация о бронированиях для помощи переезде.
     */
    List<ShippingBookingTotalDto> fetchShippingBookingTotal(
        final LocalDate startDate, final LocalDate endDate, final String district
    );

    /**
     * Delete day schedules.
     *
     * @param requestDto Deleting request
     * @param hasConfirm Подтверждение удаления дней по которым нет записи.
     * @return Заблокированные для удаления рабочие дни.
     */
    List<ShippingBookingTotalDto> deleteDaySchedules(DeletingDaySchedulesRequestDto requestDto, boolean hasConfirm);

    /**
     * Clean empty bookings.
     */
    void cleanEmptyBookings();

    /**
     * Recreate booking slots.
     */
    void recreateBookingSlots();
}
