package ru.croc.ugd.ssr.service.document;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.croc.ugd.ssr.dayschedule.BookingSlotType;
import ru.croc.ugd.ssr.dayschedule.TimeIntervalType;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class ShippingDayScheduleDocumentServiceTest {

    @InjectMocks
    ShippingDayScheduleDocumentService shippingDayScheduleDocumentService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBookingSlotsByIntervals1() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(createTimeIntervalType(LocalTime.of(8, 00), LocalTime.of(20, 00)));
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(2, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(8, 00), LocalTime.of(14, 00));
        assertStartAndEndTime(bookingSlotTypes, 1, LocalTime.of(14, 00), LocalTime.of(20, 00));
    }

    @Test
    public void getBookingSlotsByIntervals2() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(createTimeIntervalType(LocalTime.of(7, 30), LocalTime.of(19, 30)));
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(2, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(7, 30), LocalTime.of(13, 30));
        assertStartAndEndTime(bookingSlotTypes, 1, LocalTime.of(13, 30), LocalTime.of(19, 30));
    }

    @Test
    public void getBookingSlotsByIntervals3() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(createTimeIntervalType(LocalTime.of(7, 30), LocalTime.of(8, 30)));
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(1, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(7, 30), LocalTime.of(8, 30));
    }

    @Test
    public void getBookingSlotsByIntervals4() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(
            createTimeIntervalType(LocalTime.of(8, 00), LocalTime.of(13, 30)),
            createTimeIntervalType(LocalTime.of(14, 00), LocalTime.of(18, 00))
        );
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(2, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(8, 00), LocalTime.of(13, 30));
        assertStartAndEndTime(bookingSlotTypes, 1, LocalTime.of(14, 00), LocalTime.of(18, 00));
    }

    @Test
    public void getBookingSlotsByIntervals5() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(
            createTimeIntervalType(LocalTime.of(8, 00), LocalTime.of(13, 00)),
            createTimeIntervalType(LocalTime.of(15, 00), LocalTime.of(23, 00))
        );
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(3, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(8, 00), LocalTime.of(13, 00));
        assertStartAndEndTime(bookingSlotTypes, 1, LocalTime.of(15, 00), LocalTime.of(21, 00));
        assertStartAndEndTime(bookingSlotTypes, 2, LocalTime.of(21, 00), LocalTime.of(23, 00));
    }

    @Test
    public void getBookingSlotsByIntervals6() {
        final List<TimeIntervalType> timeIntervalTypes = Arrays.asList(
            createTimeIntervalType(LocalTime.of(8, 00), LocalTime.of(9, 00)),
            createTimeIntervalType(LocalTime.of(10, 00), LocalTime.of(12, 00)),
            createTimeIntervalType(LocalTime.of(13, 00), LocalTime.of(20, 00))
        );
        final List<BookingSlotType> bookingSlotTypes = shippingDayScheduleDocumentService.getBookingSlotsByIntervals(timeIntervalTypes);
        assertEquals(4, bookingSlotTypes.size());
        assertStartAndEndTime(bookingSlotTypes, 0, LocalTime.of(8, 00), LocalTime.of(9, 00));
        assertStartAndEndTime(bookingSlotTypes, 1, LocalTime.of(10, 00), LocalTime.of(12, 00));
        assertStartAndEndTime(bookingSlotTypes, 2, LocalTime.of(13, 00), LocalTime.of(19, 00));
        assertStartAndEndTime(bookingSlotTypes, 3, LocalTime.of(19, 00), LocalTime.of(20, 00));
    }


    private void assertStartAndEndTime(final List<BookingSlotType> bookingSlotTypes, final int slotIndex,
                                       final LocalTime expectedStart,
                                       final LocalTime expectedEnd) {
        assertEquals(bookingSlotTypes.get(slotIndex).getStart(), expectedStart);
        assertEquals(bookingSlotTypes.get(slotIndex).getEnd(), expectedEnd);

    }


    private TimeIntervalType createTimeIntervalType(final LocalTime start, final LocalTime end) {
        final TimeIntervalType timeIntervalType = new TimeIntervalType();
        timeIntervalType.setStart(start);
        timeIntervalType.setEnd(end);
        return timeIntervalType;
    }
}
