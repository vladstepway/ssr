package ru.croc.ugd.ssr.utils;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.LocaleUtils.ru_RU;

import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для обработки дат и времени.
 */
public class DateTimeUtils {

    private static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("dd MMMM yyyy, EEEE, в HH:mm", ru_RU);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
        .ofPattern("dd.MM.yyyy");
    private static final List<String> DATE_SHEET_FORMATS = Arrays.asList("M/d/yy", "dd.MM.yyyy", "M/d/yyyy");

    private static final String[] DAY_WORDS = {
        "Первое",
        "Второе",
        "Третье",
        "Четвёртое",
        "Пятое",
        "Шестое",
        "Седьмое",
        "Восьмое",
        "Девятое",
        "Десятое",
        "Одиннадцатое",
        "Двенадцатое",
        "Тринадцатое",
        "Четырнадцатое",
        "Пятнадцатое",
        "Шестнадцатое",
        "Семнадцатое",
        "Восемнадцатое",
        "Девятнадцатое",
        "Двадцатое",
        "Двадцать первое",
        "Двадцать второе",
        "Двадцать третье",
        "Двадцать четвёртое",
        "Двадцать пятое",
        "Двадцать шестое",
        "Двадцать седьмое",
        "Двадцать восьмое",
        "Двадцать девятое",
        "Тридцатое",
        "Тридцать первое",
    };

    private static final String[] MONTH_WORDS = {
        "января",
        "февраля",
        "марта",
        "апреля",
        "мая",
        "июня",
        "июля",
        "августа",
        "сентября",
        "октября",
        "ноября",
        "декабря"
    };

    private static final String[] ONLY_THOUSAND_WORDS = {
        "тысячного",
        "двухтысячного"
    };

    private static final String[] THOUSAND_WORDS = {
        "тысяча",
        "две тысячи"
    };

    private static final String[] ONLY_HUNDRED_WORDS = {
        "сотого",
        "двухсотого",
        "трёхсотого",
        "четырёхсотого",
        "пятисотого",
        "шестисотого",
        "семисотого",
        "восьмисотого",
        "девятисотого"
    };

    private static final String[] HUNDRED_WORDS = {
        "сто",
        "двести",
        "триста",
        "четыреста",
        "пятьсот",
        "шестьсот",
        "семьсот",
        "восемьсот",
        "девятьсот"
    };

    private static final String[] ONLY_TEN_WORDS = {
        "десятого",
        "двадцатого",
        "тридцатого",
        "сорокового",
        "пятидесятого",
        "шестидесятого",
        "семидесятого",
        "восьмидесятого",
        "девяностого"
    };

    private static final String[] TEN_WORDS = {
        "двадцать",
        "тридцать",
        "сорок",
        "пятьдесят",
        "шестьдесят",
        "семьдесят",
        "восемьдесят",
        "девяносто"
    };

    private static final String[] ONE_WORDS = {
        "первого",
        "второго",
        "третьего",
        "четвёртого",
        "пятого",
        "шестого",
        "седьмого",
        "восьмого",
        "девятого"
    };

    private static final String[] BEFORE_TWENTY_WORDS = {
        "одиннадцатого",
        "двенадцатого",
        "тринадцатого",
        "четырнадцатого",
        "пятнадцатого",
        "шестнадцатого",
        "семнадцатого",
        "восемнадцатого",
        "девятнадцатого"
    };

    /**
     * Форматирование даты и времени.
     *
     * @param dateTime дата и время
     * @return форматированные дата и время
     */
    public static String getFullDateWithTime(final LocalDateTime dateTime) {
        return ofNullable(dateTime)
            .map(dt -> dt.format(FULL_DATE_TIME_FORMATTER))
            .orElse(null);
    }

    /**
     * Форматирование даты.
     *
     * @param date date
     * @return форматированная дата
     */
    public static String getFormattedDate(final LocalDate date) {
        return ofNullable(date)
            .map(d -> d.format(DATE_FORMATTER))
            .orElse(null);
    }

    /**
     * Переводит дату в дату словами.
     * Пример: 27.10.2021 = Двадцать седьмое октября две тысячи двадцать первого года
     *
     * @param date дата
     */
    public static String getDateByWords(final LocalDate date) {
        final int year = date.getYear();
        final int thousands = year / 1000 - 1;
        final int hundreds = (year % 1000) / 100 - 1;
        final int tens = (year % 100) / 10 - 1;
        final int ones = year % 10 - 1;

        final StringBuilder sb = new StringBuilder();
        sb.append(DAY_WORDS[date.getDayOfMonth() - 1]).append(' ');
        sb.append(MONTH_WORDS[date.getMonthValue() - 1]).append(' ');

        if (year < 1000 || year >= 3000) {
            sb.append(year);
        } else if (year % 1000 == 0) {
            sb.append(ONLY_THOUSAND_WORDS[thousands]); // 1000,2000
        } else if (year % 100 == 0) {
            sb.append(THOUSAND_WORDS[thousands]).append(' ').append(ONLY_HUNDRED_WORDS[hundreds]); // 1100,1200,...
        } else {
            sb.append(THOUSAND_WORDS[thousands]).append(' ');
            if (hundreds >= 0) {
                sb.append(HUNDRED_WORDS[hundreds]).append(' ');
            }
            if (year % 10 == 0) {
                sb.append(ONLY_TEN_WORDS[tens]);
            } else if (year % 100 < 10) {
                sb.append(ONE_WORDS[ones]);
            } else if (year % 100 < 20) {
                sb.append(BEFORE_TWENTY_WORDS[ones]);
            } else {
                sb.append(TEN_WORDS[tens - 1]).append(' ').append(ONE_WORDS[ones]);
            }
        }

        sb.append(" года");
        return sb.toString();
    }

    public static LocalDate getDateFromString(final String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        for (String currentFormat : DATE_SHEET_FORMATS) {
            try {
                return LocalDate.parse(line, DateTimeFormatter.ofPattern(currentFormat));
            } catch (DateTimeParseException ex) {
                // Ignore.
            }
        }
        return null;
    }
}
