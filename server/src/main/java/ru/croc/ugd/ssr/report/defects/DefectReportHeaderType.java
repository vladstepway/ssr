package ru.croc.ugd.ssr.report.defects;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectFlatDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestEliminationDto;
import ru.croc.ugd.ssr.utils.DateTimeUtils;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum DefectReportHeaderType {

    FLAT(0, "Квартира", 2000, DefectReportHeaderType::retrieveFlat),
    FLOOR(1, "Этаж", 1500, DefectReportHeaderType::retrieveFloor),
    ENTRANCE(2, "Подъезд", 1500, DefectReportHeaderType::retrieveEntrance),
    FLAT_ELEMENT(3, "Помещение", 6000, DefectReportHeaderType::retrieveFlatElement),
    DESCRIPTION(4, "Дефект", 15000,  DefectReportHeaderType::retrieveDescription),
    ELIMINATION_DATE(5, "Плановый срок устранения", 3000, DefectReportHeaderType::retrieveEliminationDate),
    ELIMINATION_DATE_COMMENT(6, "Комментарий", 15000, DefectReportHeaderType::retrieveEliminationDateComment),
    ITEM_REQUIRED_COMMENT(7, "Требуется заказная позиция", 15000, DefectReportHeaderType::retrieveItemRequiredComment),
    ELIMINATION_MARK(8, "Отметка об устранении", 4000, DefectReportHeaderType::retrieveEliminationMark),
    NOT_DEFECT_COMMENT(9, "Не дефект", 15000, DefectReportHeaderType::retrieveNotDefectComment);

    private final Integer columnIndex;
    private final String columnName;
    private final int columnWidth;
    private final Function<RestDefectDto, String> dataExtractor;

    private static String retrieveFlat(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getFlatData())
            .map(DefectFlatDto::getFlat)
            .orElse(null);
    }

    private static String retrieveFloor(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getFlatData())
            .map(DefectFlatDto::getFloor)
            .map(String::valueOf)
            .orElse(null);
    }

    private static String retrieveEntrance(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getFlatData())
            .map(DefectFlatDto::getEntrance)
            .orElse(null);
    }

    private static String retrieveFlatElement(final RestDefectDto restDefectDto) {
        return restDefectDto.getFlatElement();
    }

    private static String retrieveDescription(final RestDefectDto restDefectDto) {
        return restDefectDto.getDescription();
    }

    private static String retrieveEliminationDate(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getEliminationData())
            .map(RestEliminationDto::getEliminationDate)
            .map(DateTimeUtils::getFormattedDate)
            .orElse(null);
    }

    private static String retrieveEliminationDateComment(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getEliminationData())
            .map(RestEliminationDto::getEliminationDateComment)
            .orElse(null);
    }

    private static String retrieveItemRequiredComment(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getEliminationData())
            .map(RestEliminationDto::getItemRequiredComment)
            .orElse(null);
    }

    private static String retrieveEliminationMark(final RestDefectDto restDefectDto) {
        return restDefectDto.isEliminated() ? "Устранено" : "Не устранено";
    }

    private static String retrieveNotDefectComment(final RestDefectDto restDefectDto) {
        return ofNullable(restDefectDto.getEliminationData())
            .map(RestEliminationDto::getNotDefectComment)
            .orElse(null);
    }

}
