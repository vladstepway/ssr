package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.DelayReasonData;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectFlatDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.ApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.DelayReasonDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.IntegrationLogDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestPersonDto;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;
import ru.croc.ugd.ssr.enums.SsrTradeType;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationActType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationAgreementType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDefectEliminationTransferType;
import ru.croc.ugd.ssr.service.utils.ApartmentUtils;
import ru.croc.ugd.ssr.utils.ApartmentInspectionUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ApartmentInspectionMapper {

    @Mapping(target = "id", source = "apartmentInspection.documentID")
    @Mapping(
        target = "actNum",
        source = "apartmentInspectionData.actNum",
        defaultValue = "б/н"
    )
    @Mapping(
        target = "addressTo",
        source = "apartmentInspectionData",
        qualifiedByName = "toAddressTo"
    )
    @Mapping(
        target = "flatNumTo",
        source = "apartmentInspectionData.flat"
    )
    @Mapping(
        target = "filingDate",
        source = "apartmentInspectionData.filingDate",
        qualifiedByName = "toDate"
    )
    @Mapping(
        target = "delayReasonDates",
        source = "apartmentInspectionData.delayReason",
        qualifiedByName = "toDelayReasonDates"
    )
    @Mapping(
        target = "defectsEliminatedNotificationDate",
        source = "apartmentInspectionData.defectsEliminatedNotificationDate",
        qualifiedByName = "toDate"
    )
    @Mapping(
        target = "signedActFileId",
        source = "apartmentInspectionData.signedActFileId"
    )
    @Mapping(
        target = "acceptedDefectsActFileId",
        source = "apartmentInspectionData.acceptedDefectsActFileId"
    )
    @Mapping(
        target = "acceptedDefectsDate",
        source = "apartmentInspectionData.acceptedDefectsDate",
        qualifiedByName = "toDate"
    )
    @Mapping(target = "hasConsent", source = "apartmentInspectionData.hasConsent")
    @Mapping(
        target = "hasDefects",
        source = "apartmentInspectionData.hasDefects",
        defaultValue = "true"
    )
    @Mapping(
        target = "flatRefusalDate",
        source = "apartmentInspectionData.flatRefusalDate"
    )
    @Mapping(
        target = "firstVisitDateTime",
        source = "apartmentInspectionData.firstVisitDateTime"
    )
    @Mapping(
        target = "secondVisitDateTime",
        source = "apartmentInspectionData.secondVisitDateTime"
    )
    @Mapping(
        target = "defects",
        source = "apartmentInspectionData.apartmentDefects"
    )
    @Mapping(
        target = "excludedDefects",
        source = "apartmentInspectionData.excludedApartmentDefects"
    )
    @Mapping(
        target = "defectExclusionReason",
        source = "apartmentInspectionData.defectExclusionReason"
    )
    @Mapping(
        target = "generalContractors",
        source = "apartmentInspectionData.generalContractors",
        qualifiedByName = "toOrganisations"
    )
    @Mapping(
        target = "developers",
        source = "apartmentInspectionData.developers",
        qualifiedByName = "toOrganisations"
    )
    @Mapping(target = "workConfirmationFiles", source = "apartmentInspectionData.workConfirmationFiles")
    ApartmentInspectionDto toApartmentInspectionDto(final ApartmentInspection apartmentInspection);

    @Named("toAddressTo")
    default String toAddressTo(final ApartmentInspectionType apartmentInspectionType) {
        return ofNullable(apartmentInspectionType)
            .map(apartmentInspection -> {
                ApartmentUtils.removeRoomNumber(apartmentInspection);
                return apartmentInspection;
            })
            .map(ApartmentInspectionType::getAddress)
            .orElse(null);
    }

    @Named("toDate")
    default LocalDate toDate(final LocalDateTime dateTime) {
        return ofNullable(dateTime)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Mapping(
        target = "delayDate",
        source = "delayReasonDto.delayDate",
        qualifiedByName = "toLocalDateTime"
    )
    @Mapping(
        target = "delayReasonText",
        source = "delayReasonDto.delayReason"
    )
    DelayReasonData toDelayReasonData(final DelayReasonDto delayReasonDto);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(final LocalDate delayDate) {
        return delayDate.atTime(0, 0);
    }

    @Named("toDelayReasonDates")
    default List<LocalDate> toDelayReasonDates(final List<DelayReasonData> delayReasonDataList) {
        return ofNullable(delayReasonDataList)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(DelayReasonData::getDelayDate)
            .filter(Objects::nonNull)
            .map(LocalDateTime::toLocalDate)
            .collect(Collectors.toList());
    }

    @Named("toOrganisations")
    default List<String> toOrganisations(final List<OrganizationInformation> organizationInformationList) {
        return ApartmentInspectionUtils.getDistinctOrgInfoFullName(organizationInformationList);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flatElement", source = "defect.apartmentDefectData.flatElement")
    @Mapping(target = "description", source = "defect.apartmentDefectData.description")
    @Mapping(target = "eliminated", source = "defect.apartmentDefectData.isEliminated")
    DefectDto toDefectDto(final ApartmentInspectionType.ApartmentDefects defect);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flatElement", source = "excludedDefect.apartmentDefectData.flatElement")
    @Mapping(target = "description", source = "excludedDefect.apartmentDefectData.description")
    @Mapping(target = "eliminated", source = "excludedDefect.apartmentDefectData.isEliminated")
    DefectDto toDefectDto(final ApartmentInspectionType.ExcludedApartmentDefects excludedDefect);

    @Mapping(target = "document.apartmentInspectionData.commissionInspectionId", source = "commissionInspectionId")
    @Mapping(target = "document.apartmentInspectionData.address", source = "address")
    @Mapping(target = "document.apartmentInspectionData.unom", source = "unom")
    @Mapping(target = "document.apartmentInspectionData.flat", source = "flat")
    @Mapping(target = "document.apartmentInspectionData.letterId", source = "letterId")
    @Mapping(target = "document.apartmentInspectionData.cipId", source = "cipId")
    @Mapping(target = "document.apartmentInspectionData.personID", source = "personID")
    @Mapping(target = "document.apartmentInspectionData.resettlementType", source = "resettlementType")
    @Mapping(target = "document.apartmentInspectionData.firstVisitDateTime", source = "secondVisitDateTime")
    @Mapping(target = "document.apartmentInspectionData.apartmentDefects", source = "apartmentDefects")
    @Mapping(target = "document.apartmentInspectionData.hasDefects", constant = "true")
    @Mapping(target = "document.apartmentInspectionData.pending", constant = "true")
    @Mapping(target = "document.apartmentInspectionData.isRemoved", constant = "false")
    ApartmentInspectionDocument createDocumentFromPrevious(final ApartmentInspectionType apartmentInspection);

    @Mapping(target = "document.apartmentInspectionData.commissionInspectionId", source = "commissionInspectionId")
    @Mapping(
        target = "document.apartmentInspectionData.firstVisitDateTime",
        source = "confirmedDateTime"
    )
    @Mapping(target = "document.apartmentInspectionData.address", source = "commissionInspection.address")
    @Mapping(target = "document.apartmentInspectionData.flat", source = "commissionInspection.flatNum")
    @Mapping(target = "document.apartmentInspectionData.unom", source = "commissionInspection.ccoUnom")
    @Mapping(target = "document.apartmentInspectionData.letterId", source = "commissionInspection.letter.id")
    @Mapping(target = "document.apartmentInspectionData.personID", source = "commissionInspection.applicant.personId")
    @Mapping(
        target = "document.apartmentInspectionData.resettlementType",
        source = "commissionInspection.tradeType",
        qualifiedByName = "toResettlementType"
    )
    @Mapping(target = "document.apartmentInspectionData.pending", constant = "true")
    @Mapping(target = "document.apartmentInspectionData.isRemoved", constant = "false")
    ApartmentInspectionDocument createPendingApartmentInspectionDocument(
        final String commissionInspectionId,
        final CommissionInspectionData commissionInspection,
        final LocalDateTime confirmedDateTime
    );

    @Named("toResettlementType")
    default String toResettlementType(final String tradeType) {
        return ofNullable(tradeType)
            .map(SsrTradeType::ofCommissionInspectionTypeName)
            .map(SsrTradeType::getApartmentInspectionTypeName)
            .orElse(null);
    }

    @Mapping(target = "flatNumber", source = "apartmentInspectionDocument.document.apartmentInspectionData.flat")
    @Mapping(target = "newUnom", source = "apartmentInspectionDocument.document.apartmentInspectionData.unom")
    @Mapping(
        target = "defects.defect",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.apartmentDefects"
    )
    @Mapping(
        target = "signedActChedFileId",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.signedActChedFileId"
    )
    @Mapping(target = "personId", source = "personDocument.document.personData.personID")
    @Mapping(target = "affairId", source = "personDocument.document.personData.affairId")
    @Mapping(target = "letterId", source = "apartmentInspectionDocument.document.apartmentInspectionData.letterId")
    @Mapping(target = "actNumber", source = "apartmentInspectionDocument.document.apartmentInspectionData.actNum")
    @Mapping(target = "filingDate", source = "apartmentInspectionDocument.document.apartmentInspectionData.filingDate")
    @Mapping(target = "actId", source = "apartmentInspectionDocument.id")
    SuperServiceDGPDefectEliminationActType toSuperServiceDgpDefectEliminationActType(
        final ApartmentInspectionDocument apartmentInspectionDocument, final PersonDocument personDocument
    );

    @Mapping(target = "flatElement", source = "defect.apartmentDefectData.flatElement")
    @Mapping(target = "description", source = "defect.apartmentDefectData.description")
    SuperServiceDGPDefectEliminationActType.Defects.Defect toSuperServiceDgpDefectEliminationActTypeDefects(
        final ApartmentInspectionType.ApartmentDefects defect
    );

    @Mapping(
        target = "oldPlannedDate",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.delayReason",
        qualifiedByName = "toOldPlannedDate"
    )
    @Mapping(
        target = "newPlannedDate",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.delayReason",
        qualifiedByName = "toNewPlannedDate"
    )
    @Mapping(target = "personId", source = "personDocument.document.personData.personID")
    @Mapping(target = "affairId", source = "personDocument.document.personData.affairId")
    @Mapping(target = "letterId", source = "apartmentInspectionDocument.document.apartmentInspectionData.letterId")
    @Mapping(target = "actNumber", source = "apartmentInspectionDocument.document.apartmentInspectionData.actNum")
    @Mapping(target = "actId", source = "apartmentInspectionDocument.id")
    SuperServiceDGPDefectEliminationTransferType toSuperServiceDgpDefectEliminationTransferType(
        final ApartmentInspectionDocument apartmentInspectionDocument, final PersonDocument personDocument
    );

    @Named("toOldPlannedDate")
    default LocalDate toOldPlannedDate(final List<DelayReasonData> delayReasonDataList) {
        if (CollectionUtils.isEmpty(delayReasonDataList) || delayReasonDataList.size() == 1) {
            return null;
        }
        final DelayReasonData delayReasonData = delayReasonDataList.get(delayReasonDataList.size() - 1);
        return ofNullable(delayReasonData)
            .map(DelayReasonData::getDelayDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Named("toNewPlannedDate")
    default LocalDate toNewPlannedDate(final List<DelayReasonData> delayReasonDataList) {
        return ofNullable(delayReasonDataList)
            .map(List::stream)
            .orElse(Stream.empty())
            .reduce((first, second) -> second)
            .map(DelayReasonData::getDelayDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Mapping(
        target = "defectsEliminatedNotificationDate",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.defectsEliminatedNotificationDate"
    )
    @Mapping(
        target = "acceptedDefectsDate",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.acceptedDefectsDate"
    )
    @Mapping(
        target = "acceptedDefectsActChedFileId",
        source = "apartmentInspectionDocument.document.apartmentInspectionData.acceptedDefectsActChedFileId"
    )
    @Mapping(target = "personId", source = "personDocument.document.personData.personID")
    @Mapping(target = "affairId", source = "personDocument.document.personData.affairId")
    @Mapping(target = "letterId", source = "apartmentInspectionDocument.document.apartmentInspectionData.letterId")
    @Mapping(target = "actNumber", source = "apartmentInspectionDocument.document.apartmentInspectionData.actNum")
    @Mapping(target = "actId", source = "apartmentInspectionDocument.id")
    @Mapping(
        target = "consentStatusCode",
        source = "apartmentInspectionDocument.document.apartmentInspectionData",
        qualifiedByName = "toConsentStatusCode"
    )
    @Mapping(
        target = "defectsEliminationStatusCode",
        source = "apartmentInspectionDocument.document.apartmentInspectionData",
        qualifiedByName = "toDefectsEliminationStatusCode"
    )
    SuperServiceDGPDefectEliminationAgreementType toSuperServiceDgpDefectEliminationAgreementType(
        final ApartmentInspectionDocument apartmentInspectionDocument, final PersonDocument personDocument
    );

    @Named("toConsentStatusCode")
    default String toConsentStatusCode(final ApartmentInspectionType apartmentInspection) {
        final Boolean hasConsent = ofNullable(apartmentInspection)
            .map(ApartmentInspectionType::isHasConsent)
            .orElse(null);

        return isNull(hasConsent)
            ? "3" // Нет сведений
            : (hasConsent ? "1" : "2"); // Согласен / Не согласен
    }

    @Named("toDefectsEliminationStatusCode")
    default String toDefectsEliminationStatusCode(final ApartmentInspectionType apartmentInspection) {
        final boolean hasDefects = ofNullable(apartmentInspection)
            .map(ApartmentInspectionType::isHasDefects)
            .orElse(false);
        if (!hasDefects) {
            return null;
        }

        final boolean isReady = of(apartmentInspection)
            .map(ApartmentInspectionType::getDefectsEliminatedNotificationDate)
            .isPresent();
        final boolean isDefectsAccepted = of(apartmentInspection)
            .map(ApartmentInspectionType::getAcceptedDefectsDate)
            .isPresent();

        return isReady || isDefectsAccepted
            ? "1" // Устранено
            : "2"; // Не устранено
    }

    @Mapping(target = "messageId", source = "messageID")
    @Mapping(target = "eventDateTime", source = "eventDateTime")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "eno", source = "eno")
    @Mapping(target = "fileStoreId", source = "fileId")
    IntegrationLogDto toIntegrationLogDto(IntegrationLog.Item item);

    default List<IntegrationLogDto> toIntegrationLogDtos(
        final ApartmentInspectionDocument apartmentInspectionDocument
    ) {
        return ofNullable(apartmentInspectionDocument)
            .map(ApartmentInspectionDocument::getDocument)
            .map(ApartmentInspection::getApartmentInspectionData)
            .map(ApartmentInspectionType::getIntegrationLog)
            .map(IntegrationLog::getItem)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(this::toIntegrationLogDto)
            .collect(Collectors.toList());
    }

    List<ApartmentInspectionType.ApartmentDefects> toApartmentDefects(
        final List<DefectDto> defectDtos,
        @Context final Map<String, ApartmentEliminationDefectType> existedApartmentDefects
    );

    List<ApartmentInspectionType.ExcludedApartmentDefects> toExcludedApartmentDefects(
        final List<DefectDto> defectDtos,
        @Context final Map<String, ApartmentEliminationDefectType> existedApartmentDefects
    );

    @Mapping(target = "apartmentDefectData", source = "defectDto", qualifiedByName = "toApartmentEliminationDefectType")
    ApartmentInspectionType.ApartmentDefects toApartmentDefect(
        final DefectDto defectDto,
        @Context final Map<String, ApartmentEliminationDefectType> existedApartmentDefects
    );

    @Mapping(target = "apartmentDefectData", source = "defectDto", qualifiedByName = "toApartmentEliminationDefectType")
    ApartmentInspectionType.ExcludedApartmentDefects toExcludedApartmentDefect(
        final DefectDto defectDto,
        @Context final Map<String, ApartmentEliminationDefectType> existedApartmentDefects
    );

    @Named("toApartmentEliminationDefectType")
    default ApartmentEliminationDefectType toApartmentEliminationDefectType(
        final DefectDto defectDto,
        @Context final Map<String, ApartmentEliminationDefectType> existedApartmentDefects
    ) {
        final ApartmentEliminationDefectType existedApartmentDefect = ofNullable(defectDto.getId())
            .map(existedApartmentDefects::get)
            .orElseGet(ApartmentEliminationDefectType::new);
        return toApartmentEliminationDefectType(existedApartmentDefect, defectDto);
    }

    @Mapping(target = "flatElement", source = "flatElement")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isEliminated", source = "eliminated")
    @Mapping(target = "eliminationData", ignore = true)
    @Mapping(target = "isBlocked", ignore = true)
    ApartmentEliminationDefectType toApartmentEliminationDefectType(
        @MappingTarget final ApartmentEliminationDefectType existedApartmentDefect, final DefectDto defectDto
    );

    @Mapping(target = "id", source = "apartmentDefectProjection.id")
    @Mapping(target = "apartmentInspectionId", source = "apartmentDefectProjection.apartmentInspectionId")
    @Mapping(target = "flatElement", source = "apartmentDefectProjection.flatElement")
    @Mapping(target = "description", source = "apartmentDefectProjection.description")
    @Mapping(target = "blocked", source = "apartmentDefectProjection.isBlocked")
    @Mapping(target = "eliminated", source = "apartmentDefectProjection.isEliminated")
    @Mapping(target = "flatData", source = "defectFlatDto")
    @Mapping(target = "eliminationData.eliminationDate", source = "apartmentDefectProjection.eliminationDate")
    @Mapping(
        target = "eliminationData.eliminationDateComment", source = "apartmentDefectProjection.eliminationDateComment"
    )
    @Mapping(target = "eliminationData.itemRequired", source = "apartmentDefectProjection.itemRequired")
    @Mapping(target = "eliminationData.itemRequiredComment", source = "apartmentDefectProjection.itemRequiredComment")
    @Mapping(target = "eliminationData.notDefect", source = "apartmentDefectProjection.isNotDefect")
    @Mapping(target = "eliminationData.notDefectComment", source = "apartmentDefectProjection.notDefectComment")
    @Mapping(target = "persons", ignore = true)
    RestDefectDto toRestDefectDto(
        final ApartmentDefectProjection apartmentDefectProjection, final DefectFlatDto defectFlatDto
    );

    @Mapping(target = "actNum", source = "apartmentInspectionData.actNum")
    @Mapping(target = "actFileStoreId", source = "apartmentInspectionData.signedActFileId")
    @Mapping(target = "person", source = "person")
    @Mapping(
        target = "plannedFixDate",
        source = "apartmentInspectionData.delayReason",
        qualifiedByName = "toPlannedFixedDate"
    )
    @Mapping(target = "hasConsent", source = "apartmentInspectionData.hasConsent")
    @Mapping(target = "hasDefects", source = "apartmentInspectionData.hasDefects")
    @Mapping(
        target = "defectsEliminatedNotificationDate",
        source = "apartmentInspectionData.defectsEliminatedNotificationDate",
        qualifiedByName = "toDate"
    )
    @Mapping(
        target = "acceptedDefectsDate",
        source = "apartmentInspectionData.acceptedDefectsDate",
        qualifiedByName = "toDate"
    )
    @Mapping(
        target = "actCreationDate",
        source = "apartmentInspectionData.filingDate",
        qualifiedByName = "toActCreationDate"
    )
    RestApartmentInspectionDto toRestApartmentInspection(
        final ApartmentInspectionType apartmentInspectionData,
        final Person person
    );

    @Named("toPlannedFixedDate")
    default LocalDate toPlannedFixedDate(final List<DelayReasonData> delayReason) {
        if (delayReason.isEmpty()) {
            return null;
        }

        return ofNullable(delayReason.get(delayReason.size() - 1))
            .map(DelayReasonData::getDelayDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Mapping(target = "personDocumentId", source = "person.documentID")
    @Mapping(target = "fullName", source = "person.personData", qualifiedByName = "toPersonFullName")
    @Mapping(target = "birthDate", source = "person.personData.birthDate")
    RestPersonDto toRestPersonDto(final Person person);

    @Named("toPersonFullName")
    default String toPersonFullName(final PersonType person) {
        return PersonUtils.getFullName(person);
    }

    @Named("toActCreationDate")
    default LocalDate toActCreationDate(final LocalDateTime filingDate) {
        return ofNullable(filingDate)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }
}
