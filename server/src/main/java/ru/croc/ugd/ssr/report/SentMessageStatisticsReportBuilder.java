package ru.croc.ugd.ssr.report;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * Заполняет отчет.
 */
@Service
@Scope(scopeName = SCOPE_PROTOTYPE)
public class SentMessageStatisticsReportBuilder {

    private String name;
    private long elk;
    private long sended;
    private long handed;
    private int percentage;

    /**
     * builder метод.
     *
     * @param name параметр
     * @return себя
     */
    public SentMessageStatisticsReportBuilder addName(String name) {
        this.name = name;
        return this;
    }

    /**
     * builder метод.
     *
     * @param elk параметр
     * @return себя
     */
    public SentMessageStatisticsReportBuilder addElk(long elk) {
        this.elk = elk;
        return this;
    }

    /**
     * builder метод.
     *
     * @param sended параметр
     * @return себя
     */
    public SentMessageStatisticsReportBuilder addSended(long sended) {
        this.sended = sended;
        return this;
    }

    /**
     * builder метод.
     *
     * @param handed параметр
     * @return себя
     */
    public SentMessageStatisticsReportBuilder addHanded(long handed) {
        this.handed = handed;
        return this;
    }

    /**
     * builder метод.
     *
     * @param percentage параметр
     * @return себя
     */
    public SentMessageStatisticsReportBuilder addPercentage(int percentage) {
        this.percentage = percentage;
        return this;
    }

    /**
     * Заполянет шапку таблицы.
     *
     * @param header строка
     * @return шапку таблицы
     */
    public static Row createHeader(Row header) {
        header.createCell(0, CellType.STRING).setCellValue("Название рассылки");
        header.createCell(1, CellType.STRING).setCellValue("Направлено");
        header.createCell(2, CellType.STRING).setCellValue("Доставлено");
        header.createCell(3, CellType.STRING).setCellValue("Прочитано");
        header.createCell(4, CellType.STRING).setCellValue("Процент прочтений от отправок");

        return header;
    }

    /**
     * Заполнение переданной строки.
     *
     * @param row строка
     * @return заполненная строка
     */
    public Row fillRow(Row row) {
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(elk);
        row.createCell(2).setCellValue(sended);
        row.createCell(3).setCellValue(handed);
        row.createCell(4).setCellValue(percentage);

        return row;
    }
}
