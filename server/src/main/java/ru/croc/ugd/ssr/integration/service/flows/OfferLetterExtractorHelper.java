package ru.croc.ugd.ssr.integration.service.flows;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class OfferLetterExtractorHelper {
    private static final String ADDRESS_REGEXP = "(?s)по адресу:(.{0,200}?)площадью";
    private static final String FLAT_NUMBER_REGEXP = "(?s)(кв\\..*?([0-9.]+))|( квартира .*?([0-9.]+))";
    private static final String FROM_FLAT_NUMBER_REGEXP = "(?s)(кв\\..*?([0-9.]+).*)|( квартира .*?([0-9.]+).*)";
    private static final String CAD_NUMBER_REGEXP = "кадастровый номер(.*)\\.";
    private static final String FULL_SQUARE_REGEXP = "(?s)площадью жилого помещения.*?([0-9.]+)";
    private static final String FULL_SQUARE_NO_TERRACE_REGEXP = "(?s)общей площадью \\(без летних\\).*?([0-9.]+)";
    private static final String LIVING_SQUARE_REGEXP = "(?s)жилой площадью.*?([0-9.]+)";
    private static final String ROOMS_AMOUNT_REGEXP = "(?s)количество комнат.*?([0-9.]+)";
    private static final String FLOOR_REGEXP = "(?s)этаж.*?([0-9.]+)";

    public static String extractAddress(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, ADDRESS_REGEXP, 1));
    }

    public static String extractAddressWithoutFlat(final String pdfContent) {
        final String address = cleanAddress(findMatchedGroup(pdfContent, ADDRESS_REGEXP, 1)
            .replaceAll(FROM_FLAT_NUMBER_REGEXP, ""));
        log.info("extractAddressWithoutFlat: '{}'", address);
        return address.replaceFirst("[.,;]$", "");
    }

    public static String extractFlatNumber(final String pdfContent) {
        final String address = StringUtils.trim(findMatchedGroup(pdfContent, ADDRESS_REGEXP, 1));
        return StringUtils.trim(findMatchedGroup(address, FLAT_NUMBER_REGEXP, 2));
    }

    public static String extractCadNumber(final String pdfContent) {
        return StringUtils
            .trimToEmpty(findMatchedGroup(pdfContent, CAD_NUMBER_REGEXP, 1))
            .replaceAll("[^0-9:]", "");
    }

    public static String extractFullSquareMeters(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, FULL_SQUARE_REGEXP, 1));
    }

    public static String extractFullSquareNoTerraceMeters(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, FULL_SQUARE_NO_TERRACE_REGEXP, 1));
    }

    public static String extractLivingSquareMeters(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, LIVING_SQUARE_REGEXP, 1));
    }

    public static String extractRoomsAmount(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, ROOMS_AMOUNT_REGEXP, 1));
    }

    public static String extractFloor(final String pdfContent) {
        return StringUtils.trim(findMatchedGroup(pdfContent, FLOOR_REGEXP, 1));
    }

    private static String cleanAddress(final String address) {
        return String.join(" ", address
            .replaceAll("\\\\n", " ")
            .replaceAll("\n", " ")
            .replaceAll("\t", " ")
            .trim()
            .split(" +")
        );
    }

    private static String findMatchedGroup(
        final String content,
        final String regexp,
        final int groupIndex
    ) {
        final Pattern pattern = Pattern.compile(regexp, Pattern.UNICODE_CHARACTER_CLASS);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return StringUtils.EMPTY;
    }
}
