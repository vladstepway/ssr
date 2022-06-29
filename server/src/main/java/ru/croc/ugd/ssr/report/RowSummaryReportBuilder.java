package ru.croc.ugd.ssr.report;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Заполняет строку отчета по сводному.
 */
@Service
@Scope(scopeName = SCOPE_PROTOTYPE)
public class RowSummaryReportBuilder {

    // для отселяемых домов
    private String resettelmentAddress;
    private int flatCount;
    private int personCount;
    private int offerLetterCount;
    private int demoCount;
    private int refusedCount;
    private int agreementCount;
    private int contractProjectCount;
    private int signedContractCount;
    private int keysIssueCount;
    private int releaseFlatCount;
    private int defectCount;
    private int agreeDefectCount;

    // для заселяемых домов
    private String settlementAddress;
    private int settlementFlatCount;
    private int settlementDefectCount;
    private int settlementAgreeDefectCount;
    private int settlementSignedContractCount;
    private int settlementKeysIssueCount;

    /**
     * Адрес расселяемого дома.
     * 
     * @param resettelmentAddress адрес расселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addResettelmentAddress(String resettelmentAddress) {
        this.resettelmentAddress = resettelmentAddress;
        return this;
    }

    /**
     * Добавляет количество квартир.
     * 
     * @param flatCount количество квартир
     * @return себя
     */
    public RowSummaryReportBuilder addFlatCount(int flatCount) {
        this.flatCount = flatCount;
        return this;
    }

    /**
     * Количество жителей.
     * 
     * @param personCount количество жителей
     * @return себя
     */
    public RowSummaryReportBuilder addPersonCount(int personCount) {
        this.personCount = personCount;
        return this;
    }

    /**
     * Количество писем с предложениями.
     * 
     * @param offerLetterCount письма с предложениями
     * @return себя
     */
    public RowSummaryReportBuilder addOfferLetterCount(int offerLetterCount) {
        this.offerLetterCount = offerLetterCount;
        return this;
    }

    /**
     * Количество писем с предложениями.
     * 
     * @param demoCount количество показов
     * @return себя
     */
    public RowSummaryReportBuilder addDemoCount(int demoCount) {
        this.demoCount = demoCount;
        return this;
    }

    /**
     * Количество отказов.
     * 
     * @param refusedCount отказы
     * @return себя
     */
    public RowSummaryReportBuilder addRefusedCount(int refusedCount) {
        this.refusedCount = refusedCount;
        return this;
    }

    /**
     * Количество согласий.
     * 
     * @param agreementCount согласия
     * @return себя
     */
    public RowSummaryReportBuilder addAgreementCount(int agreementCount) {
        this.agreementCount = agreementCount;
        return this;
    }

    /**
     * Количество проектов договора.
     *
     * @param contractProjectCount проекты договора
     * @return себя
     */
    public RowSummaryReportBuilder addContractProjectCount(int contractProjectCount) {
        this.contractProjectCount = contractProjectCount;
        return this;
    }

    /**
     * Количество подписанных договоров.
     *
     * @param signedContractCount подписанные договоры
     * @return себя
     */
    public RowSummaryReportBuilder addSignedContractCount(int signedContractCount) {
        this.signedContractCount = signedContractCount;
        return this;
    }

    /**
     * Количество выданных ключей.
     *
     * @param keysIssueCount выданные ключи
     * @return себя
     */
    public RowSummaryReportBuilder addKeysIssueCount(int keysIssueCount) {
        this.keysIssueCount = keysIssueCount;
        return this;
    }

    /**
     * Количество освобожденных квартир.
     *
     * @param releaseFlatCount освобожденные квартиры
     * @return себя
     */
    public RowSummaryReportBuilder addReleaseFlatCount(int releaseFlatCount) {
        this.releaseFlatCount = releaseFlatCount;
        return this;
    }

    /**
     * Количество дефектовок.
     *
     * @param defectCount дефектовки
     * @return себя
     */
    public RowSummaryReportBuilder addDefectCount(int defectCount) {
        this.defectCount = defectCount;
        return this;
    }

    /**
     * Количество согласий после устранений дефектов.
     *
     * @param agreeDefectCount согласия после устранений дефектов
     * @return себя
     */
    public RowSummaryReportBuilder addAgreeDefectCount(int agreeDefectCount) {
        this.agreeDefectCount = agreeDefectCount;
        return this;
    }

    /**
     * Заселяемый адрес.
     *
     * @param settlementAddress Заселяемый адрес
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementAddress(String settlementAddress) {
        this.settlementAddress = settlementAddress;
        return this;
    }

    /**
     * Количество квартир заселяемого дома.
     *
     * @param settlementFlatCount Количество квартир заселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementFlatCount(int settlementFlatCount) {
        this.settlementFlatCount = settlementFlatCount;
        return this;
    }

    /**
     * Количество дефектов заселяемого дома.
     *
     * @param settlementDefectCount Количество дефектов заселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementDefectCount(int settlementDefectCount) {
        this.settlementDefectCount = settlementDefectCount;
        return this;
    }

    /**
     * Количество согласий после обработки дефектов заселяемого дома.
     *
     * @param settlementAgreeDefectCount Количество согласий после обработки дефектов заселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementAgreeDefectCount(int settlementAgreeDefectCount) {
        this.settlementAgreeDefectCount = settlementAgreeDefectCount;
        return this;
    }


    /**
     * Количество подписанных договоров заселяемого дома.
     *
     * @param settlementSignedContractCount Количество подписанных договоров заселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementSignedContractCount(int settlementSignedContractCount) {
        this.settlementSignedContractCount = settlementSignedContractCount;
        return this;
    }

    /**
     * Количество выдачи ключей заселяемого дома.
     *
     * @param settlementKeysIssueCount Количество выдачи ключей заселяемого дома
     * @return себя
     */
    public RowSummaryReportBuilder addSettlementKeysIssueCount(int settlementKeysIssueCount) {
        this.settlementKeysIssueCount = settlementKeysIssueCount;
        return this;
    }


    /**
     * Заполняет строку.
     * 
     * @param row строка
     * @return строка
     */
    public Row fillRowFirstTable(Row row) {
        row.createCell(0).setCellValue(resettelmentAddress);
        row.createCell(1).setCellValue(flatCount);
        row.createCell(2).setCellValue(personCount);
        row.createCell(3).setCellValue(offerLetterCount);
        row.createCell(4).setCellValue(demoCount);
        row.createCell(5).setCellValue(refusedCount);
        row.createCell(6).setCellValue(agreementCount);
        row.createCell(7).setCellValue(contractProjectCount);
        row.createCell(8).setCellValue(signedContractCount);
        row.createCell(9).setCellValue(keysIssueCount);
        row.createCell(10).setCellValue(releaseFlatCount);
        row.createCell(11).setCellValue(defectCount);
        row.createCell(12).setCellValue(agreeDefectCount);

        return row;
    }

    /**
     * Заполняет строку.
     *
     * @param row строка
     * @return строка
     */
    public static Row fillHeaderRowFirstTable(Row row) {
        row.createCell(0).setCellValue("Адрес отселяемого дома");
        row.createCell(1).setCellValue("Количество квартир");
        row.createCell(2).setCellValue("Количество жителей");
        row.createCell(3).setCellValue("Количество писем с предложениями");
        row.createCell(4).setCellValue("Количество просмотров");
        row.createCell(5).setCellValue("Количество отказов");
        row.createCell(6).setCellValue("Количество согласий");
        row.createCell(7).setCellValue("Выслано проектов договоров");
        row.createCell(8).setCellValue("Подписано договоров");
        row.createCell(9).setCellValue("Получено ключей от новых квартир");
        row.createCell(10).setCellValue("Сдано ключей от расселяемых квартир");
        row.createCell(11).setCellValue("Количество составленных актов на дефекты");
        row.createCell(12).setCellValue("Количество согласий с устраненными дефектами");

        return row;
    }

    /**
     * Заполняет строку.
     *
     * @param row строка
     * @return строка
     */
    public Row fillRowSecondTable(Row row) {
        row.createCell(0).setCellValue(settlementAddress);
        row.createCell(1).setCellValue(settlementFlatCount);
        row.createCell(2).setCellValue(settlementDefectCount);
        row.createCell(3).setCellValue(settlementAgreeDefectCount);
        row.createCell(4).setCellValue(settlementSignedContractCount);
        row.createCell(5).setCellValue(settlementKeysIssueCount);

        return row;
    }

    /**
     * Заполняет строку.
     *
     * @param row строка
     * @return строка
     */
    public static Row fillHeaderRowSecondTable(Row row) {
        row.createCell(0).setCellValue("Адрес заселяемого дома");
        row.createCell(1).setCellValue("Количество квартир");
        row.createCell(2).setCellValue("Количество составленных актов на дефекты");
        row.createCell(3).setCellValue("Количество согласий с устраненными дефектами");
        row.createCell(4).setCellValue("Подписано договоров");
        row.createCell(5).setCellValue("Выданы ключи");

        return row;
    }
}
