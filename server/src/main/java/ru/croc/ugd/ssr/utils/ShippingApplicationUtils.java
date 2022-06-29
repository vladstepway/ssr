package ru.croc.ugd.ssr.utils;

import static ru.croc.ugd.ssr.utils.LocaleUtils.ru_RU;

import lombok.experimental.UtilityClass;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ShippingApplicationUtils.
 */
@UtilityClass
public class ShippingApplicationUtils {

    private static final String DATE_TIME_INFO_PATTERN = "%s, с %s по %s";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
        .ofPattern("d MMMM yyyy, EEEE")
        .withLocale(ru_RU);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
        .ofPattern("HH:mm");

    /**
     * Creates shipping information from shipping application.
     * @param shippingApplication shippingApplication
     * @return shipping information
     */
    public static String toShippingDateTimeInfo(final ShippingApplicationType shippingApplication) {
        return toShippingDateTimeInfo(
            shippingApplication.getShippingDateStart(), shippingApplication.getShippingDateEnd()
        );
    }

    /**
     * Creates shipping information.
     * @param shippingDateStart shippingDateStart
     * @param shippingDateEnd shippingDateEnd
     * @return shipping information
     */
    public static String toShippingDateTimeInfo(
        final LocalDateTime shippingDateStart, final LocalDateTime shippingDateEnd
    ) {
        final String dateInfo = DATE_FORMATTER.format(shippingDateStart);
        final String startTimeInfo = TIME_FORMATTER.format(shippingDateStart);
        final String endTimeInfo = TIME_FORMATTER.format(shippingDateEnd);

        return String.format(DATE_TIME_INFO_PATTERN, dateInfo, startTimeInfo, endTimeInfo);
    }
}
