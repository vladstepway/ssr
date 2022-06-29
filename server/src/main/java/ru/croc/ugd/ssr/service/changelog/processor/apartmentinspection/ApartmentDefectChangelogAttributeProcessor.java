package ru.croc.ugd.ssr.service.changelog.processor.apartmentinspection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.croc.ugd.ssr.utils.JsonMapperUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Обработчик изменений по дефектам в актах по дефектам.
 */
@Component("apartmentDefectChangelogAttributeProcessor")
public class ApartmentDefectChangelogAttributeProcessor implements ChangelogAttributeDescriptionProcessor {

    @Override
    public List<String> processDescription(final JSONObject oldJson, final JSONObject newJson) {
        final List<String> descriptionMessages = new ArrayList<>();
        final List<ApartmentEliminationDefectType> oldDefects = retrieveApartmentDefects(oldJson);
        final List<ApartmentEliminationDefectType> newDefects = retrieveApartmentDefects(newJson);
        final List<ApartmentEliminationDefectType> oldExcludedDefects = retrieveExcludedApartmentDefects(oldJson);
        final List<ApartmentEliminationDefectType> newExcludedDefects = retrieveExcludedApartmentDefects(newJson);

        final List<ApartmentEliminationDefectType> excludedDefects = retrieveDifference(
            newExcludedDefects, oldExcludedDefects
        );

        if (!excludedDefects.isEmpty()) {
            retrieveExclusionReason(newJson).ifPresent(
                descriptionMessages::add
            );
        }

        excludedDefects.stream()
            .map(defect -> retrieveInformation("Исключен дефект:", defect))
            .forEach(descriptionMessages::add);

        retrieveDifference(oldExcludedDefects, newExcludedDefects).stream()
            .map(defect -> retrieveInformation("Удален исключенный дефект:", defect))
            .forEach(descriptionMessages::add);

        retrieveDifference(newDefects, oldDefects).stream()
            .map(defect -> retrieveInformation("Добавлен дефект:", defect))
            .forEach(descriptionMessages::add);

        final List<ApartmentEliminationDefectType> deletedDefects = retrieveDifference(oldDefects, newDefects);
        retrieveDifference(deletedDefects, newExcludedDefects).stream()
            .map(defect -> retrieveInformation("Удален дефект:", defect))
            .forEach(descriptionMessages::add);

        newDefects.stream()
            .filter(defect -> nonNull(defect.getId()))
            .filter(defect -> nonNull(defect.getEliminationData())
                && nonNull(defect.getEliminationData().getEliminationDate()))
            .filter(defect -> existsEliminationDateChanging(
                defect.getId(), defect.getEliminationData().getEliminationDate(), oldDefects
            ))
            .map(this::retrieveEliminationInformation)
            .forEach(descriptionMessages::add);

        return descriptionMessages;
    }

    private boolean existsEliminationDateChanging(
        final String defectId, final LocalDate defectDate, final List<ApartmentEliminationDefectType> oldDefects
    ) {
        return oldDefects.stream()
            .filter(oldDefect -> Objects.equals(defectId, oldDefect.getId()))
            .map(ApartmentEliminationDefectType::getEliminationData)
            .anyMatch(eliminationData -> isNull(eliminationData)
                || isNull(eliminationData.getEliminationDate())
                || !eliminationData.getEliminationDate().equals(defectDate));

    }

    private List<ApartmentEliminationDefectType> retrieveApartmentDefects(final JSONObject jsonObject) {
        try {
            final String json = getNestedJsonValue(
                jsonObject, "/ApartmentInspection/ApartmentInspectionData/apartmentDefects"
            )
                .toString()
                .replaceAll("\"ApartmentDefectData\"", "\"apartmentDefectData\"");
            final ApartmentInspectionType.ApartmentDefects[] apartmentDefects = JsonMapperUtil.getMapper()
                .readValue(json, ApartmentInspectionType.ApartmentDefects[].class);
            return Arrays.stream(apartmentDefects)
                .map(ApartmentInspectionType.ApartmentDefects::getApartmentDefectData)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    protected List<ApartmentEliminationDefectType> retrieveExcludedApartmentDefects(final JSONObject jsonObject) {
        try {
            final String json = getNestedJsonValue(
                jsonObject, "/ApartmentInspection/ApartmentInspectionData/excludedApartmentDefects"
            )
                .toString()
                .replaceAll("\"ApartmentDefectData\"", "\"apartmentDefectData\"");
            final ApartmentInspectionType.ExcludedApartmentDefects[] excludedApartmentDefects =
                JsonMapperUtil.getMapper().readValue(json, ApartmentInspectionType.ExcludedApartmentDefects[].class);
            return Arrays.stream(excludedApartmentDefects)
                .map(ApartmentInspectionType.ExcludedApartmentDefects::getApartmentDefectData)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Optional<String> retrieveExclusionReason(final JSONObject newJson) {
        try {
            final String reason = (String) getNestedJsonValue(
                newJson, "/ApartmentInspection/ApartmentInspectionData/defectExclusionReason"
            );
            return ofNullable(reason)
                .map("Причина исключения: "::concat);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<ApartmentEliminationDefectType> retrieveDifference(
        final List<ApartmentEliminationDefectType> toDefects,
        final List<ApartmentEliminationDefectType> fromDefects
    ) {
        return toDefects.stream()
            .filter(defect -> notExistsDefect(defect, fromDefects))
            .collect(Collectors.toList());
    }

    private boolean notExistsDefect(
        final ApartmentEliminationDefectType defect, final List<ApartmentEliminationDefectType> defects
    ) {
        return defects.stream()
            .noneMatch(d -> Objects.equals(d.getDescription(), defect.getDescription())
                && Objects.equals(d.getFlatElement(), defect.getFlatElement()));
    }

    private String retrieveInformation(final String message, final ApartmentEliminationDefectType defect) {
        return message + " " + defect.getFlatElement() + " - " + defect.getDescription();
    }

    private String retrieveEliminationInformation(final ApartmentEliminationDefectType defect) {
        final String defectInformation = retrieveInformation("Установлен плановый срок устранения для дефекта", defect);
        final String eliminationDateValue = DateTimeUtils.getFormattedDate(
            defect.getEliminationData().getEliminationDate()
        );
        final String eliminationInformation = ofNullable(defect.getEliminationData().getEliminationDateComment())
            .map(comment -> eliminationDateValue + " (" + comment + ")")
            .orElse(eliminationDateValue);
        return defectInformation + ": " + eliminationInformation;
    }
}
