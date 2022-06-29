package ru.croc.ugd.ssr.builder;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.db.dao.PersonDocumentDao;
import ru.croc.ugd.ssr.db.projection.BuildingData;
import ru.croc.ugd.ssr.db.projection.PersonProjection;
import ru.croc.ugd.ssr.dto.HeaderPropDto;
import ru.croc.ugd.ssr.exception.WorkbookNotCreatedException;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Используется для создания справочника адресов.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DictionaryUploadDataBuilder {

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final String ADDRESS_FROM_DICTIONARY_TITLE = "Справочник расселяемых домов";
    private static final String ADDRESS_TO_DICTIONARY_TITLE = "Справочник заселяемых домов";
    private static final String PEOPLE_DICTIONARY_TITLE = "Справочник жителей";
    private static final String CREATION_TITLE = "Сформирован ";
    private static final String EMPTY_LINE = "";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    private static CellStyle titleCellStyle;
    private static CellStyle headerCellStyle;
    private static CellStyle commonCellStyle;

    private final PersonDocumentDao personDocumentDao;

    /**
     * Создать справочник адресов.
     *
     * @param <T> тип buildings
     * @param isAddressFromDictionary isAddressFromDictionary
     * @param buildings buildings
     * @return справочник расселяемых домов
     */
    public <T extends BuildingData> byte[] buildAddressDictionary(
        final boolean isAddressFromDictionary,
        final List<T> buildings
    ) {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final Workbook book = new XSSFWorkbook()) {

            final String titleName;
            final List<HeaderPropDto> headers;
            if (isAddressFromDictionary) {
                titleName = ADDRESS_FROM_DICTIONARY_TITLE;
                headers = getHeaderAddressFromDictionaryProperty();
            } else {
                titleName = ADDRESS_TO_DICTIONARY_TITLE;
                headers = getHeaderAddressToDictionaryProperty();
            }

            final Sheet sheet = book.createSheet(titleName);
            final Map<Integer, Integer> columnWithMaxSize = new HashMap<>();

            final AtomicInteger rowNumber = new AtomicInteger(0);

            setSheetStyles(book);

            setTitle(sheet, rowNumber, titleName);
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:B1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:B2"));

            setHeader(sheet, rowNumber, columnWithMaxSize, headers);

            buildings.forEach(address -> {
                final Row row = sheet.createRow(rowNumber.getAndIncrement());
                printRow(row, columnWithMaxSize, address);
            });

            setColumnWidth(sheet, columnWithMaxSize);

            book.write(result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new WorkbookNotCreatedException();
        }
    }

    /**
     * Создать справочник жителей.
     *
     * @return справочник жителей
     */
    @Transactional
    public byte[] buildPeopleDictionary() {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final Workbook book = new XSSFWorkbook()) {

            final Sheet sheet = book.createSheet(PEOPLE_DICTIONARY_TITLE);
            final Map<Integer, Integer> columnWithMaxSize = new HashMap<>();

            final AtomicInteger rowNumber = new AtomicInteger(0);

            setSheetStyles(book);

            setTitle(sheet, rowNumber, PEOPLE_DICTIONARY_TITLE);
            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:R1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:R2"));

            final List<HeaderPropDto> headers = getHeaderPeopleDictionaryProperty();
            setHeader(sheet, rowNumber, columnWithMaxSize, headers);

            try (final Stream<PersonProjection> personDocumentStream = personDocumentDao.streamAll()) {
                personDocumentStream.forEach(personDocument -> {
                    final Row row = sheet.createRow(rowNumber.getAndIncrement());
                    printRow(row, columnWithMaxSize, personDocument);
                });
            }
            setColumnWidth(sheet, columnWithMaxSize);

            book.write(result);
            return result.toByteArray();
        } catch (IOException e) {
            throw new WorkbookNotCreatedException();
        }
    }

    private static void setTitle(final Sheet sheet, final AtomicInteger rowNumber, final String titleName) {
        final Row titleRow = sheet.createRow(rowNumber.getAndIncrement());
        printTitleRow(titleRow, titleName);

        final String creationDate = CREATION_TITLE + DATE_FORMATTER.format(LocalDate.now());
        final Row dateRow = sheet.createRow(rowNumber.getAndIncrement());
        printTitleRow(dateRow, creationDate);
    }

    private static void setHeader(
        final Sheet sheet,
        final AtomicInteger rowNumber,
        final Map<Integer, Integer> columnWithMaxSize,
        final List<HeaderPropDto> headers
    ) {
        final Row headerRow = sheet.createRow(rowNumber.getAndIncrement());
        printHeaderRow(headerRow, headers, columnWithMaxSize);
    }

    private static void printTitleRow(
        final Row row,
        final String value
    ) {
        final CellStyle style = titleCellStyle;
        final Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private static void printHeaderRow(
        final Row row,
        final List<HeaderPropDto> headers,
        final Map<Integer, Integer> columnWithMaxSize
    ) {
        int columnNumber = 0;

        for (final HeaderPropDto propDto : headers) {
            final CellStyle style = headerCellStyle;

            final Cell cell = row.createCell(columnNumber);
            cell.setCellStyle(style);
            columnWithMaxSize.put(columnNumber, propDto.getColWith() + 2);
            cell.setCellValue(propDto.getHeaderCellText());
            columnNumber++;
        }
    }

    private void printRow(
        final Row row,
        final Map<Integer, Integer> columnWithMaxSize,
        final BuildingData data
    ) {
        final String address = ofNullable(data.getAddress()).orElse(EMPTY_LINE);
        addCellToRow(row, address, 0, columnWithMaxSize);

        final String unomToPrint = ofNullable(data.getUnom()).orElse(EMPTY_LINE);
        addCellToRow(row, unomToPrint, 1, columnWithMaxSize);
    }

    private void printRow(
        final Row row,
        final Map<Integer, Integer> columnWithMaxSize,
        final PersonProjection data
    ) {
        final String personId = ofNullable(data.getPersonId()).orElse(EMPTY_LINE);
        addCellToRow(row, personId, 0, columnWithMaxSize);

        final String affairId = ofNullable(data.getAffairId()).orElse(EMPTY_LINE);
        addCellToRow(row, affairId, 1, columnWithMaxSize);

        final String snils = ofNullable(data.getSnils()).orElse(EMPTY_LINE);
        addCellToRow(row, snils, 2, columnWithMaxSize);

        final String fio = ofNullable(data.getFio()).map(DictionaryUploadDataBuilder::trimPersonFio).orElse(EMPTY_LINE);
        addCellToRow(row, fio, 3, columnWithMaxSize);

        final String gender = ofNullable(data.getGender()).orElse(EMPTY_LINE);
        addCellToRow(row, gender, 4, columnWithMaxSize);

        final String birthdate = data.getBirthDate() != null
            ? data.getBirthDate() : EMPTY_LINE;
        addCellToRow(row, birthdate, 5, columnWithMaxSize);

        final String statusLiving = ofNullable(data.getStatusLiving()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getStatusLiving(statusLiving), 6, columnWithMaxSize);

        final String encumbrances = ofNullable(data.getEncumbrances()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getEncumbrances(encumbrances), 7, columnWithMaxSize);

        final String unom = data.getUnom() != null ? data.getUnom() : EMPTY_LINE;
        addCellToRow(row, unom, 8, columnWithMaxSize);

        final String cadNum = ofNullable(data.getCadNum()).orElse(EMPTY_LINE);
        addCellToRow(row, cadNum, 9, columnWithMaxSize);

        final String flatNum = ofNullable(data.getFlatNum()).orElse(EMPTY_LINE);
        addCellToRow(row, flatNum, 10, columnWithMaxSize);

        final String roomNum = ofNullable(data.getRoomNum()).orElse(EMPTY_LINE);
        addCellToRow(row, roomNum, 11, columnWithMaxSize);

        final String isQueue = ofNullable(data.getWaiter()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getValueFromCode(isQueue), 12, columnWithMaxSize);

        final String isDead = ofNullable(data.getIsDead()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getValueFromCode(isDead), 13, columnWithMaxSize);

        final String delReason = ofNullable(data.getDelReason()).orElse(EMPTY_LINE);
        addCellToRow(row, delReason, 14, columnWithMaxSize);

        final String notFlat = ofNullable(data.getNoFlat()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getValueFromCode(notFlat), 15, columnWithMaxSize);

        final String ownFederal = ofNullable(data.getOwnFederal()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getValueFromCode(ownFederal), 16, columnWithMaxSize);

        final String inCourt = ofNullable(data.getInCourt()).orElse(EMPTY_LINE);
        addCellToRow(row, PersonUtils.getValueFromCode(inCourt), 17, columnWithMaxSize);
    }

    private static void addCellToRow(
        final Row row,
        final String value,
        final int number,
        final Map<Integer, Integer> columnWithMaxSize
    ) {
        final Cell cell = row.createCell(number);

        final CellStyle style = commonCellStyle;
        cell.setCellStyle(style);

        if (StringUtils.isNoneBlank(value)) {
            final String trimmedValue = value.trim();
            cell.setCellValue(trimmedValue);
            if ((!columnWithMaxSize.containsKey(number)
                || columnWithMaxSize.get(number) < trimmedValue.length())) {
                columnWithMaxSize.put(number, trimmedValue.length());
            }
        }
    }

    private static CellStyle getTitleCellStyle(final Workbook book) {
        final CellStyle style = book.createCellStyle();

        final Font font = book.createFont();
        font.setBold(true);

        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor((short) 1);

        return style;
    }

    private static CellStyle getHeaderCellStyle(final Workbook book) {
        final CellStyle style = book.createCellStyle();

        setCellBordersStyle(style);

        final Font font = book.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor((short) 50);

        return style;
    }

    private static CellStyle getCommonCellStyle(final Workbook book) {
        final CellStyle style = book.createCellStyle();

        setCellBordersStyle(style);
        style.setWrapText(true);

        return style;
    }

    private static void setCellBordersStyle(final CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    }

    private static void setColumnWidth(
        final Sheet sheet,
        final Map<Integer, Integer> columnWithMaxSize) {
        int width = (int) (50 * 259.9);
        columnWithMaxSize.forEach((column, size) -> {
            size = (int) ((size + 2) * 259.9);
            if (size > width) {
                sheet.setColumnWidth(column, width);
            } else {
                sheet.setColumnWidth(column, size);
            }
        });
    }

    private static void setSheetStyles(final Workbook book) {
        commonCellStyle = getCommonCellStyle(book);
        titleCellStyle = getTitleCellStyle(book);
        headerCellStyle = getHeaderCellStyle(book);
    }

    private static List<HeaderPropDto> getHeaderAddressFromDictionaryProperty() {
        final List<HeaderPropDto> headerList = new ArrayList<>();
        headerList.add(new HeaderPropDto("Адрес расселяемого дома", 25, (short) 50));
        headerList.add(new HeaderPropDto("Уном", 10, (short) 50));
        return headerList;
    }

    private static List<HeaderPropDto> getHeaderAddressToDictionaryProperty() {
        final List<HeaderPropDto> headerList = new ArrayList<>();
        headerList.add(new HeaderPropDto("Адрес заселяемого дома", 25, (short) 50));
        headerList.add(new HeaderPropDto("Уном", 10, (short) 50));
        return headerList;
    }

    private static List<HeaderPropDto> getHeaderPeopleDictionaryProperty() {
        final List<HeaderPropDto> headerList = new ArrayList<>();
        headerList.add(new HeaderPropDto("Person id", 15, (short) 50));
        headerList.add(new HeaderPropDto("Affair id", 15, (short) 50));
        headerList.add(new HeaderPropDto("Snils", 15, (short) 50));
        headerList.add(new HeaderPropDto("FIO", 10, (short) 50));
        headerList.add(new HeaderPropDto("Gender", 10, (short) 50));
        headerList.add(new HeaderPropDto("Birthdate", 10, (short) 50));
        headerList.add(new HeaderPropDto("Status living", 10, (short) 50));
        headerList.add(new HeaderPropDto("Encumbrances", 15, (short) 50));
        headerList.add(new HeaderPropDto("Unom", 10, (short) 50));
        headerList.add(new HeaderPropDto("Cadastral num", 15, (short) 50));
        headerList.add(new HeaderPropDto("Flat num", 10, (short) 50));
        headerList.add(new HeaderPropDto("Room num", 10, (short) 50));
        headerList.add(new HeaderPropDto("Is queue", 5, (short) 50));
        headerList.add(new HeaderPropDto("Is dead", 5, (short) 50));
        headerList.add(new HeaderPropDto("Delete reason", 25, (short) 50));
        headerList.add(new HeaderPropDto("Not flat", 5, (short) 50));
        headerList.add(new HeaderPropDto("Own federal", 5, (short) 50));
        headerList.add(new HeaderPropDto("In court", 5, (short) 50));
        return headerList;
    }

    private static String trimPersonFio(final String fio) {
        return Arrays.stream(fio.split(" "))
            .map(StringUtils::trim)
            .filter(StringUtils::isNoneBlank)
            .collect(Collectors.joining(" "));
    }

}
