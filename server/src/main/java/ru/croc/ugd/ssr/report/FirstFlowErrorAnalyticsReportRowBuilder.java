package ru.croc.ugd.ssr.report;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Заполняет отчет.
 */
@Service
@Scope(scopeName = SCOPE_PROTOTYPE)
public class FirstFlowErrorAnalyticsReportRowBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String unom;
    private String flatNumberDgi;
    private String flatNumberEvd;
    private String flatNumberRes;
    private String roomsDgi;
    private String roomsEvd;
    private String roomsRes;
    private String personIdDgi;
    private String personIdEvd;
    private String personIdRes;
    private String affairIdDgi;
    private String affairIdEvd;
    private String affairIdRes;
    private String snilsDgi;
    private String snilsEvd;
    private String snilsRes;
    private String lastNameDgi;
    private String lastNameEvd;
    private String lastNameRes;
    private String firstNameDgi;
    private String firstNameEvd;
    private String firstNameRes;
    private String middleNameDgi;
    private String middleNameEvd;
    private String middleNameRes;
    private LocalDate birthDateDgi;
    private LocalDate birthDateEvd;
    private LocalDate birthDateRes;
    private String genderDgi;
    private String genderEvd;
    private String genderRes;
    private String statusLivingDgi;
    private String statusLivingEvd;
    private String statusLivingRes;
    private String waiterDgi;
    private String waiterEvd;
    private String waiterRes;
    private String deadDgi;
    private String deadEvd;
    private String deadRes;
    private String delFlagDgi;
    private String delFlagRes;
    private String delReasonDgi;
    private String delReasonRes;
    private String cadNumDgi;
    private String cadNumEvd;
    private String cadNumRes;
    private String communDgi;
    private String communEvd;
    private String communRes;
    private String encumbrancesDgi;
    private String encumbrancesEvd;
    private String encumbrancesRes;
    private String noFlatDgi;
    private String noFlatEvd;
    private String noFlatRes;
    private String ownFederalDgi;
    private String ownFederalEvd;
    private String ownFederalRes;
    private String inCourtDgi;
    private String inCourtEvd;
    private String inCourtRes;

    /**
     * builder метод.
     *
     * @param unom параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addUnom(String unom) {
        this.unom = unom;
        return this;
    }

    /**
     * builder метод.
     *
     * @param flatNumberDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFlatNumberDgi(String flatNumberDgi) {
        this.flatNumberDgi = flatNumberDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param flatNumberEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFlatNumberEvd(String flatNumberEvd) {
        this.flatNumberEvd = flatNumberEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param flatNumberRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFlatNumberRes(String flatNumberRes) {
        this.flatNumberRes = flatNumberRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param roomsDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addRoomsDgi(String roomsDgi) {
        this.roomsDgi = roomsDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param roomsEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addRoomsEvd(String roomsEvd) {
        this.roomsEvd = roomsEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param roomsRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addRoomsRes(String roomsRes) {
        this.roomsRes = roomsRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param personIdDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addPersonIdDgi(String personIdDgi) {
        this.personIdDgi = personIdDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param personIdEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addPersonIdEvd(String personIdEvd) {
        this.personIdEvd = personIdEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param personIdRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addPersonIdRes(String personIdRes) {
        this.personIdRes = personIdRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param affairIdDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addAffairIdDgi(String affairIdDgi) {
        this.affairIdDgi = affairIdDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param affairIdEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addAffairIdEvd(String affairIdEvd) {
        this.affairIdEvd = affairIdEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param affairIdRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addAffairIdRes(String affairIdRes) {
        this.affairIdRes = affairIdRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param snilsDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addSnilsDgi(String snilsDgi) {
        this.snilsDgi = snilsDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param snilsEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addSnilsEvd(String snilsEvd) {
        this.snilsEvd = snilsEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param snilsRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addSnilsRes(String snilsRes) {
        this.snilsRes = snilsRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param lastNameDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addLastNameDgi(String lastNameDgi) {
        this.lastNameDgi = lastNameDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param lastNameEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addLastNameEvd(String lastNameEvd) {
        this.lastNameEvd = lastNameEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param lastNameRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addLastNameRes(String lastNameRes) {
        this.lastNameRes = lastNameRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param firstNameDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFirstNameDgi(String firstNameDgi) {
        this.firstNameDgi = firstNameDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param firstNameEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFirstNameEvd(String firstNameEvd) {
        this.firstNameEvd = firstNameEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param firstNameRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addFirstNameRes(String firstNameRes) {
        this.firstNameRes = firstNameRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param middleNameDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addMiddleNameDgi(String middleNameDgi) {
        this.middleNameDgi = middleNameDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param middleNameEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addMiddleNameEvd(String middleNameEvd) {
        this.middleNameEvd = middleNameEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param middleNameRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addMiddleNameRes(String middleNameRes) {
        this.middleNameRes = middleNameRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param birthDateDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addBirthDateDgi(LocalDate birthDateDgi) {
        this.birthDateDgi = birthDateDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param birthDateEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addBirthDateEvd(LocalDate birthDateEvd) {
        this.birthDateEvd = birthDateEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param birthDateRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addBirthDateRes(LocalDate birthDateRes) {
        this.birthDateRes = birthDateRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param genderDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addGenderDgi(String genderDgi) {
        this.genderDgi = genderDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param genderEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addGenderEvd(String genderEvd) {
        this.genderEvd = genderEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param genderRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addGenderRes(String genderRes) {
        this.genderRes = genderRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param statusLivingDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addStatusLivingDgi(String statusLivingDgi) {
        this.statusLivingDgi = statusLivingDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param statusLivingEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addStatusLivingEvd(String statusLivingEvd) {
        this.statusLivingEvd = statusLivingEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param statusLivingRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addStatusLivingRes(String statusLivingRes) {
        this.statusLivingRes = statusLivingRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param waiterDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addWaiterDgi(String waiterDgi) {
        this.waiterDgi = waiterDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param waiterEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addWaiterEvd(String waiterEvd) {
        this.waiterEvd = waiterEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param waiterRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addWaiterRes(String waiterRes) {
        this.waiterRes = waiterRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param deadDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDeadDgi(String deadDgi) {
        this.deadDgi = deadDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param deadEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDeadEvd(String deadEvd) {
        this.deadEvd = deadEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param deadRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDeadRes(String deadRes) {
        this.deadRes = deadRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param delFlagDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDelFlagDgi(String delFlagDgi) {
        this.delFlagDgi = delFlagDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param delFlagRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDelFlagRes(String delFlagRes) {
        this.delFlagRes = delFlagRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param delReasonDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDelReasonDgi(String delReasonDgi) {
        this.delReasonDgi = delReasonDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param delReasonRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addDelReasonRes(String delReasonRes) {
        this.delReasonRes = delReasonRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param cadNumDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCadNumDgi(String cadNumDgi) {
        this.cadNumDgi = cadNumDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param cadNumEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCadNumEvd(String cadNumEvd) {
        this.cadNumEvd = cadNumEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param cadNumRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCadNumRes(String cadNumRes) {
        this.cadNumRes = cadNumRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param communDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCommunDgi(String communDgi) {
        this.communDgi = communDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param communEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCommunEvd(String communEvd) {
        this.communEvd = communEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param communRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addCommunRes(String communRes) {
        this.communRes = communRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param encumbrancesDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addEncumbrancesDgi(String encumbrancesDgi) {
        this.encumbrancesDgi = encumbrancesDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param encumbrancesEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addEncumbrancesEvd(String encumbrancesEvd) {
        this.encumbrancesEvd = encumbrancesEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param encumbrancesRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addEncumbrancesRes(String encumbrancesRes) {
        this.encumbrancesRes = encumbrancesRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param noFlatDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addNoFlatDgi(String noFlatDgi) {
        this.noFlatDgi = noFlatDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param noFlatEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addNoFlatEvd(String noFlatEvd) {
        this.noFlatEvd = noFlatEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param noFlatRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addNoFlatRes(String noFlatRes) {
        this.noFlatRes = noFlatRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param ownFederalDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addOwnFederalDgi(String ownFederalDgi) {
        this.ownFederalDgi = ownFederalDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param ownFederalEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addOwnFederalEvd(String ownFederalEvd) {
        this.ownFederalEvd = ownFederalEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param ownFederalRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addOwnFederalRes(String ownFederalRes) {
        this.ownFederalRes = ownFederalRes;
        return this;
    }

    /**
     * builder метод.
     *
     * @param inCourtDgi параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addInCourtDgi(String inCourtDgi) {
        this.inCourtDgi = inCourtDgi;
        return this;
    }

    /**
     * builder метод.
     *
     * @param inCourtEvd параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addInCourtEvd(String inCourtEvd) {
        this.inCourtEvd = inCourtEvd;
        return this;
    }

    /**
     * builder метод.
     *
     * @param inCourtRes параметр
     * @return себя
     */
    public FirstFlowErrorAnalyticsReportRowBuilder addInCourtRes(String inCourtRes) {
        this.inCourtRes = inCourtRes;
        return this;
    }

    /**
     * Заполнение переданной строки.
     *
     * @param row строка
     * @param cellStyle стиль ячейки с несовпадающими данными
     * @return заполненная строка
     */
    public Row fillRow(Row row, CellStyle cellStyle) {
        row.createCell(0).setCellValue(unom);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(flatNumberDgi);
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(flatNumberEvd);
        Cell cell3 = row.createCell(3);
        cell3.setCellValue(flatNumberRes);
        if (hasDifferencesWithNull(flatNumberDgi, flatNumberEvd, flatNumberRes)) {
            styleCells(cellStyle, cell1, cell2, cell3);
        }

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(roomsDgi);
        Cell cell5 = row.createCell(5);
        cell5.setCellValue(roomsEvd);
        Cell cell6 = row.createCell(6);
        cell6.setCellValue(roomsRes);
        if (hasDifferencesWithNull(roomsDgi, roomsEvd, roomsRes)) {
            styleCells(cellStyle, cell4, cell5, cell6);
        }

        Cell cell7 = row.createCell(7);
        cell7.setCellValue(personIdDgi);
        Cell cell8 = row.createCell(8);
        cell8.setCellValue(personIdEvd);
        Cell cell9 = row.createCell(9);
        cell9.setCellValue(personIdRes);
        if (hasDifferencesWithNull(personIdDgi, personIdEvd, personIdRes)) {
            styleCells(cellStyle, cell7, cell8, cell9);
        }

        Cell cell10 = row.createCell(10);
        cell10.setCellValue(affairIdDgi);
        Cell cell11 = row.createCell(11);
        cell11.setCellValue(affairIdEvd);
        Cell cell12 = row.createCell(12);
        cell12.setCellValue(affairIdRes);
        if (hasDifferencesWithNull(affairIdDgi, affairIdEvd, affairIdRes)) {
            styleCells(cellStyle, cell10, cell11, cell12);
        }

        Cell cell13 = row.createCell(13);
        cell13.setCellValue(snilsDgi);
        Cell cell14 = row.createCell(14);
        cell14.setCellValue(snilsEvd);
        Cell cell15 = row.createCell(15);
        cell15.setCellValue(snilsRes);
        if (hasDifferencesWithNull(snilsDgi, snilsEvd, snilsRes)) {
            styleCells(cellStyle, cell13, cell14, cell15);
        }

        Cell cell16 = row.createCell(16);
        cell16.setCellValue(lastNameDgi);
        Cell cell17 = row.createCell(17);
        cell17.setCellValue(lastNameEvd);
        Cell cell18 = row.createCell(18);
        cell18.setCellValue(lastNameRes);
        if (hasDifferencesWithNull(lastNameDgi, lastNameEvd, lastNameRes)) {
            styleCells(cellStyle, cell16, cell17, cell18);
        }

        Cell cell19 = row.createCell(19);
        cell19.setCellValue(firstNameDgi);
        Cell cell20 = row.createCell(20);
        cell20.setCellValue(firstNameEvd);
        Cell cell21 = row.createCell(21);
        cell21.setCellValue(firstNameRes);
        if (hasDifferencesWithNull(firstNameDgi, firstNameEvd, firstNameRes)) {
            styleCells(cellStyle, cell19, cell20, cell21);
        }

        Cell cell22 = row.createCell(22);
        cell22.setCellValue(middleNameDgi);
        Cell cell23 = row.createCell(23);
        cell23.setCellValue(middleNameEvd);
        Cell cell24 = row.createCell(24);
        cell24.setCellValue(middleNameRes);
        if (hasDifferencesWithNull(middleNameDgi, middleNameEvd, middleNameRes)) {
            styleCells(cellStyle, cell22, cell23, cell24);
        }

        Cell cell25 = row.createCell(25);
        cell25.setCellValue(localDateToString(birthDateDgi));
        Cell cell26 = row.createCell(26);
        cell26.setCellValue(localDateToString(birthDateEvd));
        Cell cell27 = row.createCell(27);
        cell27.setCellValue(localDateToString(birthDateRes));
        if (hasDifferencesWithNull(
                localDateToString(birthDateDgi),
                localDateToString(birthDateEvd),
                localDateToString(birthDateRes)
        )) {
            styleCells(cellStyle, cell25, cell26, cell27);
        }

        Cell cell28 = row.createCell(28);
        cell28.setCellValue(genderDgi);
        Cell cell29 = row.createCell(29);
        cell29.setCellValue(genderEvd);
        Cell cell30 = row.createCell(30);
        cell30.setCellValue(genderRes);
        if (hasDifferencesWithNull(genderDgi, genderEvd, genderRes)) {
            styleCells(cellStyle, cell28, cell29, cell30);
        }

        Cell cell31 = row.createCell(31);
        cell31.setCellValue(statusLivingCodeToName(statusLivingDgi));
        Cell cell32 = row.createCell(32);
        cell32.setCellValue(statusLivingCodeToName(statusLivingEvd));
        Cell cell33 = row.createCell(33);
        cell33.setCellValue(statusLivingCodeToName(statusLivingRes));
        if (hasDifferencesWithNull(statusLivingDgi, statusLivingEvd, statusLivingRes)) {
            styleCells(cellStyle, cell31, cell32, cell33);
        }

        Cell cell34 = row.createCell(34);
        cell34.setCellValue(binaryToString(waiterDgi));
        Cell cell35 = row.createCell(35);
        cell35.setCellValue(binaryToString(waiterEvd));
        Cell cell36 = row.createCell(36);
        cell36.setCellValue(binaryToString(waiterRes));
        if (hasDifferencesWithNull(binaryToString(waiterDgi), binaryToString(waiterEvd), binaryToString(waiterRes))) {
            styleCells(cellStyle, cell34, cell35, cell36);
        }

        Cell cell37 = row.createCell(37);
        cell37.setCellValue(binaryToString(deadDgi));
        Cell cell38 = row.createCell(38);
        cell38.setCellValue(binaryToString(deadEvd));
        Cell cell39 = row.createCell(39);
        cell39.setCellValue(binaryToString(deadRes));
        if (hasDifferencesWithNull(binaryToString(deadDgi), binaryToString(deadEvd), binaryToString(deadRes))) {
            styleCells(cellStyle, cell37, cell38, cell39);
        }

        Cell cell40 = row.createCell(40);
        cell40.setCellValue(binaryToString(delFlagDgi));
        Cell cell41 = row.createCell(41);
        cell41.setCellValue(binaryToString(delFlagRes));
        if (hasDifferencesWithNull(binaryToString(delFlagDgi), binaryToString(delFlagRes))) {
            styleCells(cellStyle, cell40, cell41);
        }

        Cell cell42 = row.createCell(42);
        cell42.setCellValue(delReasonDgi);
        Cell cell43 = row.createCell(43);
        cell43.setCellValue(delReasonRes);
        if (hasDifferencesWithNull(delReasonDgi, delReasonRes)) {
            styleCells(cellStyle, cell42, cell43);
        }

        Cell cell44 = row.createCell(44);
        cell44.setCellValue(cadNumDgi);
        Cell cell45 = row.createCell(45);
        cell45.setCellValue(cadNumEvd);
        Cell cell46 = row.createCell(46);
        cell46.setCellValue(cadNumRes);
        if (hasDifferencesWithNull(cadNumDgi, cadNumEvd, cadNumRes)) {
            styleCells(cellStyle, cell44, cell45, cell46);
        }

        Cell cell47 = row.createCell(47);
        cell47.setCellValue(binaryToString(communDgi));
        Cell cell48 = row.createCell(48);
        cell48.setCellValue(binaryToString(communEvd));
        Cell cell49 = row.createCell(49);
        cell49.setCellValue(binaryToString(communRes));
        if (hasDifferencesWithNull(binaryToString(communDgi), binaryToString(communEvd), binaryToString(communRes))) {
            styleCells(cellStyle, cell47, cell48, cell49);
        }

        Cell cell50 = row.createCell(50);
        cell50.setCellValue(encumbrancesDgi);
        Cell cell51 = row.createCell(51);
        cell51.setCellValue(encumbrancesEvd);
        Cell cell52 = row.createCell(52);
        cell52.setCellValue(encumbrancesRes);
        if (hasDifferencesWithNull(encumbrancesDgi, encumbrancesEvd, encumbrancesRes)) {
            styleCells(cellStyle, cell50, cell51, cell52);
        }

        Cell cell53 = row.createCell(53);
        cell53.setCellValue(binaryToString(noFlatDgi));
        Cell cell54 = row.createCell(54);
        cell54.setCellValue(binaryToString(noFlatEvd));
        Cell cell55 = row.createCell(55);
        cell55.setCellValue(binaryToString(noFlatRes));
        if (hasDifferencesWithNull(binaryToString(noFlatDgi), binaryToString(noFlatEvd), binaryToString(noFlatRes))) {
            styleCells(cellStyle, cell53, cell54, cell55);
        }

        Cell cell56 = row.createCell(56);
        cell56.setCellValue(binaryToString(ownFederalDgi));
        Cell cell57 = row.createCell(57);
        cell57.setCellValue(binaryToString(ownFederalEvd));
        Cell cell58 = row.createCell(58);
        cell58.setCellValue(binaryToString(ownFederalRes));
        if (hasDifferencesWithNull(
                binaryToString(ownFederalDgi),
                binaryToString(ownFederalEvd),
                binaryToString(ownFederalRes)
        )) {
            styleCells(cellStyle, cell56, cell57, cell58);
        }

        Cell cell59 = row.createCell(59);
        cell59.setCellValue(binaryToString(inCourtDgi));
        Cell cell60 = row.createCell(60);
        cell60.setCellValue(binaryToString(inCourtEvd));
        Cell cell61 = row.createCell(61);
        cell61.setCellValue(binaryToString(inCourtRes));
        if (hasDifferencesWithNull(
                binaryToString(inCourtDgi),
                binaryToString(inCourtEvd),
                binaryToString(inCourtRes)
        )) {
            styleCells(cellStyle, cell59, cell60, cell61);
        }

        return row;
    }

    private void styleCells(CellStyle cellStyle, Cell... cells) {
        List<Cell> list = Arrays.asList(cells);
        list.forEach(c -> c.setCellStyle(cellStyle));
    }

    private boolean hasDifferencesWithNull(String... strings) {
        List<String> list = Arrays.asList(strings);
        List<String> notNullList = list.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        return !notNullList.isEmpty() && Collections.frequency(list, list.get(0)) != list.size();
    }

    /**
     * Заполянет шапку таблицы.
     *
     * @param header строка
     * @return шапку таблицы
     */
    public static Row createHeader(Row header) {
        header.createCell(0, CellType.STRING).setCellValue("УНОМ дома");
        header.createCell(1, CellType.STRING).setCellValue("Номер квартиры ДГИ");
        header.createCell(2, CellType.STRING).setCellValue("Номер квартиры ЕВД");
        header.createCell(3, CellType.STRING).setCellValue("Номер квартиры ИТОГ");
        header.createCell(4, CellType.STRING).setCellValue("Комнаты ДГИ");
        header.createCell(5, CellType.STRING).setCellValue("Комнаты ЕВД");
        header.createCell(6, CellType.STRING).setCellValue("Комнаты ИТОГ");
        header.createCell(7, CellType.STRING).setCellValue("PersonId ДГИ");
        header.createCell(8, CellType.STRING).setCellValue("PersonId ЕВД");
        header.createCell(9, CellType.STRING).setCellValue("PersonId ИТОГ");
        header.createCell(10, CellType.STRING).setCellValue("AffairId ДГИ");
        header.createCell(11, CellType.STRING).setCellValue("AffairId ЕВД");
        header.createCell(12, CellType.STRING).setCellValue("AffairId ИТОГ");
        header.createCell(13, CellType.STRING).setCellValue("СНИЛС ДГИ");
        header.createCell(14, CellType.STRING).setCellValue("СНИЛС ЕВД");
        header.createCell(15, CellType.STRING).setCellValue("СНИЛС ИТОГ");
        header.createCell(16, CellType.STRING).setCellValue("Фамилия ДГИ");
        header.createCell(17, CellType.STRING).setCellValue("Фамилия ЕВД");
        header.createCell(18, CellType.STRING).setCellValue("Фамилия ИТОГ");
        header.createCell(19, CellType.STRING).setCellValue("Имя ДГИ");
        header.createCell(20, CellType.STRING).setCellValue("Имя ЕВД");
        header.createCell(21, CellType.STRING).setCellValue("Имя ИТОГ");
        header.createCell(22, CellType.STRING).setCellValue("Отчество ДГИ");
        header.createCell(23, CellType.STRING).setCellValue("Отчество ЕВД");
        header.createCell(24, CellType.STRING).setCellValue("Отчество ИТОГ");
        header.createCell(25, CellType.STRING).setCellValue("Дата рождения ДГИ");
        header.createCell(26, CellType.STRING).setCellValue("Дата рождения ЕВД");
        header.createCell(27, CellType.STRING).setCellValue("Дата рождения ИТОГ");
        header.createCell(28, CellType.STRING).setCellValue("Пол ДГИ");
        header.createCell(29, CellType.STRING).setCellValue("Пол ЕВД");
        header.createCell(30, CellType.STRING).setCellValue("Пол ИТОГ");
        header.createCell(31, CellType.STRING).setCellValue("Статус проживания ДГИ");
        header.createCell(32, CellType.STRING).setCellValue("Статус проживания ЕВД");
        header.createCell(33, CellType.STRING).setCellValue("Статус проживания ИТОГ");
        header.createCell(34, CellType.STRING).setCellValue("Очередник ДГИ");
        header.createCell(35, CellType.STRING).setCellValue("Очередник ЕВД");
        header.createCell(36, CellType.STRING).setCellValue("Очередник ИТОГ");
        header.createCell(37, CellType.STRING).setCellValue("Умерший собственник ДГИ");
        header.createCell(38, CellType.STRING).setCellValue("Умерший собственник ЕВД");
        header.createCell(39, CellType.STRING).setCellValue("Умерший собственник ИТОГ");
        header.createCell(40, CellType.STRING).setCellValue("Признак удаления ДГИ");
        header.createCell(41, CellType.STRING).setCellValue("Признак удаления ИТОГ");
        header.createCell(42, CellType.STRING).setCellValue("Причина удаления ДГИ");
        header.createCell(43, CellType.STRING).setCellValue("Причина удаления ИТОГ");
        header.createCell(44, CellType.STRING).setCellValue("Кадастровый номер дома ДГИ");
        header.createCell(45, CellType.STRING).setCellValue("Кадастровый номер дома ЕВД");
        header.createCell(46, CellType.STRING).setCellValue("Кадастровый номер дома ИТОГ");
        header.createCell(47, CellType.STRING).setCellValue("Коммунальная квартира ДГИ");
        header.createCell(48, CellType.STRING).setCellValue("Коммунальная квартира ЕВД");
        header.createCell(49, CellType.STRING).setCellValue("Коммунальная квартира ИТОГ");
        header.createCell(50, CellType.STRING).setCellValue("Обременения ДГИ");
        header.createCell(51, CellType.STRING).setCellValue("Обременения ЕВД");
        header.createCell(52, CellType.STRING).setCellValue("Обременения ИТОГ");
        header.createCell(53, CellType.STRING).setCellValue("Квартира без предоставления ДГИ");
        header.createCell(54, CellType.STRING).setCellValue("Квартира без предоставления ЕВД");
        header.createCell(55, CellType.STRING).setCellValue("Квартира без предоставления ИТОГ");
        header.createCell(56, CellType.STRING).setCellValue("Федеральная собственность ДГИ");
        header.createCell(57, CellType.STRING).setCellValue("Федеральная собственность ЕВД");
        header.createCell(58, CellType.STRING).setCellValue("Федеральная собственность ИТОГ");
        header.createCell(59, CellType.STRING).setCellValue("В суде ДГИ");
        header.createCell(60, CellType.STRING).setCellValue("В суде ЕВД");
        header.createCell(61, CellType.STRING).setCellValue("В суде ИТОГ");

        return header;
    }

    @NotNull
    private String localDateToString(LocalDate date) {
        return date != null ? FORMATTER.format(date) : StringUtils.EMPTY;
    }

    @NotNull
    private String binaryToString(String binary) {
        if (binary == null) {
            return StringUtils.EMPTY;
        }
        return "1".equals(binary) ? "Да" : "Нет";
    }

    @NotNull
    private String statusLivingCodeToName(String statusLivingCode) {
        if (statusLivingCode == null) {
            return StringUtils.EMPTY;
        }
        switch (statusLivingCode) {
            case "1":
                return "Собственник (частная собственность)";
            case "2":
                return "Пользователь (частная собственность)";
            case "3":
                return "Наниматель (найм/пользование)";
            case "4":
                return "Проживающий (найм/пользователь)";
            case "5":
                return "Свободная";
            default:
                return "Отсутствует";
        }
    }


}
