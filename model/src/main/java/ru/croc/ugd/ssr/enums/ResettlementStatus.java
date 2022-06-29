package ru.croc.ugd.ssr.enums;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.DemolitionData;
import ru.croc.ugd.ssr.HousePreservationData;
import ru.croc.ugd.ssr.RealEstateDataType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ResettlementStatus {

    NOT_STARTED("1", "Переселение не начиналось"),
    IN_PROGRESS("2", "В процессе переселения"),
    IN_PROGRESS_WITHDRAW("3", "Расселен, ведется изъятие нежилых помещений"),
    IN_PROGRESS_DEMOLITION("4", "Расселен, готовится к сносу"),
    IN_PROGRESS_PRESERVED("5", "Расселен, сохраняемый дом"),
    FINISHED("6", "Снесен");

    private final String code;
    private final String description;

    public static Optional<ResettlementStatus> of(final String code) {
        return Arrays.stream(ResettlementStatus.values())
            .filter(status -> Objects.equals(status.getCode(), code))
            .findFirst();
    }

    public static ResettlementStatus of(final RealEstateDataType realEstateData) {
        // в процессе переселения
        final boolean inProgress = ofNullable(realEstateData)
            .map(RealEstateDataType::getResettlementBy)
            .isPresent();

        // внесены сведения об окончании расселения
        final boolean existsResettlementCompletionData = ofNullable(realEstateData)
            .map(RealEstateDataType::getResettlementCompletionData)
            .isPresent();

        // есть нежилые помещения
        final Boolean hasNonResidentialSpaces = ofNullable(realEstateData)
            .map(RealEstateDataType::isHasNonResidentialSpaces)
            .orElse(null);
        // нежилые помещения изъяты
        final boolean withdrawnNonResidentialSpaces = ofNullable(realEstateData)
            .map(RealEstateDataType::isWithdrawnNonResidentialSpaces)
            .orElse(false);
        // все нежилые помещения изъяты или есть сведения о том, что нежилых помещений нет
        final boolean existsWithdrawnOrResidentialSpaces = withdrawnNonResidentialSpaces
            || (nonNull(hasNonResidentialSpaces) && !hasNonResidentialSpaces);
        // есть нежилые помещения и есть не изъятые нежилые помещения
        final boolean existsNonWithdrawnNonResidentialSpaces = !withdrawnNonResidentialSpaces
            && nonNull(hasNonResidentialSpaces) && hasNonResidentialSpaces;

        // дом будет сохранен
        final boolean isPreserved = ofNullable(realEstateData)
            .map(RealEstateDataType::getHousePreservationData)
            .map(HousePreservationData::isIsPreserved)
            .orElse(false);
        // дом будет снесен
        final boolean isDemolished = ofNullable(realEstateData)
            .map(RealEstateDataType::getDemolitionData)
            .map(DemolitionData::isIsDemolished)
            .orElse(false);

        if (isDemolished) {
            return FINISHED;
        } else if (existsResettlementCompletionData && existsWithdrawnOrResidentialSpaces) {
            if (isPreserved) {
                return IN_PROGRESS_PRESERVED;
            } else {
                return IN_PROGRESS_DEMOLITION;
            }
        } else if (existsResettlementCompletionData && existsNonWithdrawnNonResidentialSpaces) {
            return IN_PROGRESS_WITHDRAW;
        } else if (inProgress) {
            return IN_PROGRESS;
        } else {
            return NOT_STARTED;
        }
    }
}
