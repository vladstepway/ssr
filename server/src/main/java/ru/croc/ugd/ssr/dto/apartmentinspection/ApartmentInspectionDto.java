package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.RestWorkConfirmationFile;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ApartmentInspectionDto {

    private final String id;
    private final String actNum;
    private final String addressTo;
    private final String flatNumTo;
    private final String filingDate;
    private final boolean hasDefects;
    private final LocalDateTime firstVisitDateTime;
    private final LocalDateTime secondVisitDateTime;
    private final List<LocalDate> delayReasonDates;
    private final LocalDate defectsEliminatedNotificationDate;
    private final String signedActFileId;
    private final LocalDate acceptedDefectsDate;
    private final String acceptedDefectsActFileId;
    private final Boolean hasConsent;
    private final LocalDate flatRefusalDate;
    private final List<DefectDto> defects;
    private final List<DefectDto> excludedDefects;
    private final String defectExclusionReason;
    private final List<String> generalContractors;
    private final List<String> developers;
    private final List<RestWorkConfirmationFile> workConfirmationFiles;
}
