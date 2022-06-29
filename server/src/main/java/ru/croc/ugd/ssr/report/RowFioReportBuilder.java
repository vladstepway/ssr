package ru.croc.ugd.ssr.report;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Заполняет строку отчета по фио.
 */
@Service
@Scope(scopeName = SCOPE_PROTOTYPE)
public class RowFioReportBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String ressettelmentAddresse;

    private String flatNum;

    private List<String> roomNum;

    private String fio;

    private String affairId;

    private PersonType.OfferLetters.OfferLetter offerLetter;

    private int demoCount;

    private String demoDates;

    private String agreementType;

    private LocalDate agreementDate;

    private String act;

    private LocalDateTime contractProject;

    private LocalDate signedContract;

    private String newFlat;

    private LocalDate keysIssue;

    private LocalDate releaseFlat;

    /**
     * Добавление адреса.
     * 
     * @param ressettelmentAddresse
     *            адрес расселяемого дома.
     * @return RowFioReportBuilder.
     */
    public RowFioReportBuilder addRessettelmentAddresse(String ressettelmentAddresse) {
        this.ressettelmentAddresse = ressettelmentAddresse;
        return this;
    }

    /**
     * Добавляет номер квартиры.
     * 
     * @param flatNum
     *            номер
     * @return себя
     */
    public RowFioReportBuilder addFlatNum(String flatNum) {
        this.flatNum = flatNum;
        return this;
    }

    /**
     * Добавляет номер комнаты.
     * 
     * @param roomNum
     *            номер комнаты.
     * @return себя
     */
    public RowFioReportBuilder addRoomNum(List<String> roomNum) {
        this.roomNum = roomNum;
        return this;
    }

    /**
     * Добавляет фио.
     * 
     * @param fio
     *            фио
     * @return себя
     */
    public RowFioReportBuilder addFio(String fio) {
        this.fio = fio;
        return this;
    }

    /**
     * Добавляет аффеир.
     * 
     * @param affairId
     *            аффеир
     * @return себя.
     */
    public RowFioReportBuilder addAffairId(String affairId) {
        this.affairId = affairId;
        return this;
    }

    /**
     * Добавляет письмо с предложением.
     * 
     * @param offerLetter
     *            письмо с предложением.
     * @return себя.
     */
    public RowFioReportBuilder addOfferLetter(PersonType.OfferLetters.OfferLetter offerLetter) {
        this.offerLetter = offerLetter;
        return this;
    }

    /**
     * Количество просмотров.
     * 
     * @param demoCount
     *            количество.
     * @return себя
     */
    public RowFioReportBuilder addDemoCount(int demoCount) {
        this.demoCount = demoCount;
        return this;
    }

    /**
     * Даты просмотра.
     * 
     * @param demoDates
     *            даты просмотра.
     * @return себя.
     */
    public RowFioReportBuilder addDemoDates(String demoDates) {
        this.demoDates = demoDates;
        return this;
    }

    /**
     * Тип решения.
     * 
     * @param agreementType
     *            тип решения.
     * @return себя.
     */
    public RowFioReportBuilder addAgreementType(String agreementType) {
        this.agreementType = agreementType;
        return this;
    }

    /**
     * Дата решения.
     * 
     * @param agreementDate
     *            дата.
     * @return себя
     */
    public RowFioReportBuilder addAgreementDate(LocalDate agreementDate) {
        this.agreementDate = agreementDate;
        return this;
    }

    /**
     * Акт осмотра квартиры.
     *
     * @param act
     *            Акт осмотра квартиры.
     * @return себя
     */
    public RowFioReportBuilder addAct(String act) {
        this.act = act;
        return this;
    }

    /**
     * Проект договора.
     *
     * @param contractProject
     *            Проект договора.
     * @return себя
     */
    public RowFioReportBuilder addContractProject(LocalDateTime contractProject) {
        this.contractProject = contractProject;
        return this;
    }

    /**
     * Подписанный договор.
     *
     * @param signedContract
     *            Подписанный договор.
     * @return себя
     */
    public RowFioReportBuilder addSignedContract(LocalDate signedContract) {
        this.signedContract = signedContract;
        return this;
    }

    /**
     * Заселяемая квартира.
     *
     * @param newFlat
     *            Заселяемая квартира.
     * @return себя
     */
    public RowFioReportBuilder addNewFlat(String newFlat) {
        this.newFlat = newFlat;
        return this;
    }

    /**
     * Выдача ключей.
     *
     * @param keysIssue
     *            Выдача ключей.
     * @return себя
     */
    public RowFioReportBuilder addKeysIssue(LocalDate keysIssue) {
        this.keysIssue = keysIssue;
        return this;
    }

    /**
     * Освобождение квартиры.
     *
     * @param releaseFlat
     *            Освобождение квартиры.
     * @return себя
     */
    public RowFioReportBuilder addReleaseFlat(LocalDate releaseFlat) {
        this.releaseFlat = releaseFlat;
        return this;
    }

    /**
     * Заполнение переданной строки.
     * 
     * @param row
     *            строка.
     * @return заполненная строка.
     */
    public Row fillRow(Row row) {
        row.createCell(0).setCellValue(ressettelmentAddresse);
        row.createCell(1).setCellValue(flatNum);
        row.createCell(2).setCellValue(roomNum.stream().filter(Objects::nonNull).collect(Collectors.joining(", ")));
        row.createCell(3).setCellValue(fio);
        row.createCell(4).setCellValue(affairId);
        row.createCell(5)
                .setCellValue(offerLetter.getLetterId() == null
                        ? ""
                        : offerLetter.getLetterId()
                                + (offerLetter.getDate() == null ? "" : " от " + offerLetter.getDate()));
        row.createCell(6).setCellValue(demoCount);
        row.createCell(7).setCellValue(demoDates);
        row.createCell(8).setCellValue(mapEventId(agreementType));
        row.createCell(9).setCellValue(localDateToString(agreementDate));
        row.createCell(10).setCellValue(act);
        row.createCell(11).setCellValue(localDateTimeToString(contractProject));
        row.createCell(12).setCellValue(localDateToString(signedContract));
        row.createCell(13).setCellValue(newFlat);
        row.createCell(14).setCellValue(localDateToString(keysIssue));
        row.createCell(15).setCellValue(localDateToString(releaseFlat));

        return row;
    }

    /**
     * Заполянет header.
     * 
     * @param header
     *            строка.
     * @return хедер.
     */
    public static Row createHeader(Row header) {
        header.createCell(0, CellType.STRING).setCellValue("Адрес отселяемого дома");
        header.createCell(1, CellType.STRING).setCellValue("Квартира");
        header.createCell(2, CellType.STRING).setCellValue("Комнаты (при наличии)");
        header.createCell(3, CellType.STRING).setCellValue("ФИО");
        header.createCell(4, CellType.STRING).setCellValue("AffairId");
        header.createCell(5, CellType.STRING).setCellValue("Письма с предложением");
        header.createCell(6, CellType.STRING).setCellValue("Количество просмотров");
        header.createCell(7, CellType.STRING).setCellValue("Просмотры квартир");
        header.createCell(8, CellType.STRING).setCellValue("Тип решения");
        header.createCell(9, CellType.STRING).setCellValue("Дата решения");
        header.createCell(10, CellType.STRING).setCellValue("Акт осмотра квартиры от");
        header.createCell(11, CellType.STRING).setCellValue("Проект договора");
        header.createCell(12, CellType.STRING).setCellValue("Подписание договора");
        header.createCell(13, CellType.STRING).setCellValue("Адрес квартиры в заселяемом доме");
        header.createCell(14, CellType.STRING).setCellValue("Выдача ключей");
        header.createCell(15, CellType.STRING).setCellValue("Сдача ключей");

        return header;
    }

    @NotNull
    private String localDateToString(LocalDate date) {
        return date != null ? FORMATTER.format(date) : StringUtils.EMPTY;
    }

    @NotNull
    private String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? FORMATTER.format(dateTime) : StringUtils.EMPTY;
    }

    private String mapEventId(String event) {
        event = event == null ? "" : event;
        switch (event) {
            case "1":
                return "Согласие";
            case "2":
                return "Отказ";
            case "3":
                return "Приняты недостающие документы";
            case "4":
                return "Закрепление площади";
            case "5":
                return "Обращение в Фонд реновации";
            case "6":
                return "Принят полный комплект документов от жителя";
            default:
                return "";
        }
    }

}
