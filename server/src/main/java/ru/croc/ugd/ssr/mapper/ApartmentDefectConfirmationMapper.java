package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.ApartmentDefectElimination;
import ru.croc.ugd.ssr.ApartmentEliminationDefectType;
import ru.croc.ugd.ssr.CcoData;
import ru.croc.ugd.ssr.DefectData;
import ru.croc.ugd.ssr.EliminationData;
import ru.croc.ugd.ssr.FlatData;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.WorkConfirmationFile;
import ru.croc.ugd.ssr.db.projection.ApartmentDefectProjection;
import ru.croc.ugd.ssr.dto.RestWorkConfirmationFile;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectConfirmationRequestDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.DefectFlatDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestCcoDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectConfirmationDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestEliminationDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestPersonDto;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ApartmentDefectConfirmationMapper {

    @Mapping(target = "document.apartmentDefectConfirmationData.requestDateTime", source = "requestDateTime")
    @Mapping(
        target = "document.apartmentDefectConfirmationData.generalContractorLogin",
        source = "restDefectConfirmationDto.generalContractorLogin"
    )
    @Mapping(target = "document.apartmentDefectConfirmationData.ccoData", source = "restDefectConfirmationDto.ccoData")
    @Mapping(
        target = "document.apartmentDefectConfirmationData.workConfirmationFiles",
        source = "restDefectConfirmationDto.workConfirmationFiles"
    )
    @Mapping(
        target = "document.apartmentDefectConfirmationData.defects.defect", source = "restDefectConfirmationDto.defects"
    )
    @Mapping(target = "document.apartmentDefectConfirmationData.pending", source = "pending")
    ApartmentDefectConfirmationDocument toApartmentDefectConfirmationDocument(
        @MappingTarget final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument,
        final RestDefectConfirmationDto restDefectConfirmationDto,
        final LocalDateTime requestDateTime,
        final Boolean pending,
        @Context final Function<String, String> retrieveAffairId
    );

    @Mapping(target = "document.apartmentDefectConfirmationData.requestDateTime", source = "requestDateTime")
    @Mapping(
        target = "document.apartmentDefectConfirmationData.generalContractorLogin", source = "generalContractorLogin"
    )
    @Mapping(target = "document.apartmentDefectConfirmationData.ccoData", source = "ssrCcoDocument")
    @Mapping(
        target = "document.apartmentDefectConfirmationData.defects.defect", source = "defectConfirmationRequestDtos"
    )
    ApartmentDefectConfirmationDocument toApartmentDefectConfirmationDocument(
        final LocalDateTime requestDateTime,
        final String generalContractorLogin,
        final List<DefectConfirmationRequestDto> defectConfirmationRequestDtos,
        final SsrCcoDocument ssrCcoDocument
    );

    @Mapping(target = "ccoDocumentId", source = "ccoDocumentId")
    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "address", source = "address")
    CcoData toCcoData(final RestCcoDto restCcoDto);

    @Mapping(target = "ccoDocumentId", source = "id")
    @Mapping(target = "unom", source = "document.ssrCcoData.unom")
    @Mapping(target = "address", source = "document.ssrCcoData.address")
    CcoData toCcoData(final SsrCcoDocument ssrCcoDocument);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "apartmentInspectionId", source = "apartmentInspectionId")
    @Mapping(target = "flatElement", source = "flatElement")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "flatData", source = "flatData")
    @Mapping(target = "eliminationData", source = "eliminationData")
    @Mapping(target = "isApproved", ignore = true)
    @Mapping(target = "isExcluded", ignore = true)
    @Mapping(target = "isEliminated", source = "eliminated")
    @Mapping(target = "affairId", source = "apartmentInspectionId", qualifiedByName = "toAffairId")
    DefectData toDefectData(
        final RestDefectDto restDefectDto, @Context final Function<String, String> retrieveAffairId
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "apartmentInspectionId", source = "apartmentInspectionId")
    @Mapping(target = "flatElement", source = "flatElement")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "flatData", source = "flatData")
    @Mapping(target = "eliminationData", source = "defectConfirmationRequestDtos")
    @Mapping(target = "isApproved", ignore = true)
    @Mapping(target = "isExcluded", ignore = true)
    @Mapping(target = "isEliminated", source = "eliminated")
    @Mapping(target = "affairId", source = "affairId")
    DefectData toDefectData(final DefectConfirmationRequestDto defectConfirmationRequestDtos);

    @Named("toAffairId")
    default String toAffairId(
        final String apartmentInspectionId, @Context final Function<String, String> retrieveAffairId
    ) {
        return retrieveAffairId.apply(apartmentInspectionId);
    }

    @Mapping(target = "flat", source = "flat")
    @Mapping(target = "floor", source = "floor")
    @Mapping(target = "entrance", source = "entrance")
    FlatData toFlatData(final DefectFlatDto defectFlatDto);

    @Mapping(target = "oldEliminationDate", source = "oldEliminationDate")
    @Mapping(target = "eliminationDate", source = "eliminationDate")
    @Mapping(target = "eliminationDateComment", source = "eliminationDateComment")
    @Mapping(target = "itemRequired", source = "itemRequired")
    @Mapping(target = "itemRequiredComment", source = "itemRequiredComment")
    @Mapping(target = "isNotDefect", source = "notDefect")
    @Mapping(target = "notDefectComment", source = "notDefectComment")
    EliminationData toEliminationData(final RestEliminationDto restEliminationDto);

    @Mapping(target = "oldEliminationDate", source = "oldEliminationDate")
    @Mapping(target = "eliminationDate", source = "eliminationDate")
    @Mapping(
        target = "eliminationDateComment",
        source = "defectConfirmationRequestDto",
        qualifiedByName = "toEliminationDateComment"
    )
    @Mapping(target = "itemRequired", ignore = true)
    @Mapping(target = "itemRequiredComment", ignore = true)
    @Mapping(target = "isNotDefect", ignore = true)
    @Mapping(target = "notDefectComment", ignore = true)
    EliminationData toEliminationData(final DefectConfirmationRequestDto defectConfirmationRequestDto);

    @Named("toEliminationDateComment")
    default String toEliminationDateComment(final DefectConfirmationRequestDto defectConfirmationRequestDto) {
        return ofNullable(defectConfirmationRequestDto)
            .map(d -> nonNull(d.getOldEliminationDate()) ? d.getEliminationDateComment() : null)
            .orElse(null);
    }

    @Mapping(target = "id", source = "apartmentDefectProjection.id")
    @Mapping(target = "apartmentInspectionId", source = "apartmentDefectProjection.apartmentInspectionId")
    @Mapping(target = "flatElement", source = "apartmentDefectProjection.flatElement")
    @Mapping(target = "description", source = "apartmentDefectProjection.description")
    @Mapping(target = "eliminated", source = "apartmentDefectProjection.isEliminated")
    @Mapping(target = "flatData.flat", source = "apartmentDefectProjection.flat")
    @Mapping(target = "flatData.floor", source = "apartmentDefectProjection.floor")
    @Mapping(target = "flatData.entrance", source = "apartmentDefectProjection.entrance")
    @Mapping(target = "eliminationData.oldEliminationDate", source = "apartmentDefectProjection.oldEliminationDate")
    @Mapping(target = "eliminationData.eliminationDate", source = "apartmentDefectProjection.eliminationDate")
    @Mapping(
        target = "eliminationData.eliminationDateComment", source = "apartmentDefectProjection.eliminationDateComment"
    )
    @Mapping(target = "eliminationData.itemRequired", source = "apartmentDefectProjection.itemRequired")
    @Mapping(target = "eliminationData.itemRequiredComment", source = "apartmentDefectProjection.itemRequiredComment")
    @Mapping(target = "eliminationData.notDefect", source = "apartmentDefectProjection.isNotDefect")
    @Mapping(target = "eliminationData.notDefectComment", source = "apartmentDefectProjection.notDefectComment")
    @Mapping(target = "persons", source = "personDocuments")
    @Mapping(target = "blocked", ignore = true)
    RestDefectDto toRestDefectDto(
        final ApartmentDefectProjection apartmentDefectProjection, final List<PersonDocument> personDocuments
    );

    @Mapping(target = "id", source = "id")
    @Mapping(target = "apartmentInspectionId", source = "apartmentInspectionId")
    @Mapping(target = "flatElement", source = "flatElement")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "flatData", source = "flatData")
    @Mapping(target = "eliminated", source = "isEliminated")
    @Mapping(target = "eliminationData", source = "eliminationData")
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "persons", ignore = true)
    RestDefectDto toRestDefectDto(final DefectData defectData);

    @Mapping(target = "fullName", source = "document.personData", qualifiedByName = "toFullName")
    @Mapping(target = "phone", source = "document.personData.phones", qualifiedByName = "toPhone")
    RestPersonDto toRestPersonDto(final PersonDocument personDocument);

    @Named("toFullName")
    default String toFullName(final PersonType personData) {
        return PersonUtils.getFullName(personData);
    }

    @Named("toPhone")
    default String toPhone(final PersonType.Phones phones) {
        return PersonUtils.getBasePhoneNumber(phones);
    }

    @Mapping(target = "isBlocked", source = "isBlocked")
    @Mapping(target = "isEliminated", source = "defectData.isEliminated")
    @Mapping(target = "flatElement", source = "defectData.flatElement")
    @Mapping(target = "description", source = "defectData.description")
    @Mapping(target = "eliminationData", source = "defectData.eliminationData")
    ApartmentEliminationDefectType toApartmentDefectData(
        @MappingTarget final ApartmentEliminationDefectType apartmentDefectData,
        final DefectData defectData,
        final boolean isBlocked
    );

    ApartmentDefectElimination toApartmentDefectElimination(final EliminationData eliminationData);

    @Mapping(target = "document.apartmentDefectConfirmationData.requestDateTime", source = "currentDateTime")
    @Mapping(target = "document.apartmentDefectConfirmationData.reviewDateTime", source = "currentDateTime")
    @Mapping(target = "document.apartmentDefectConfirmationData.ccoData", source = "restDefectConfirmationDto.ccoData")
    @Mapping(
        target = "document.apartmentDefectConfirmationData.workConfirmationFiles",
        source = "restDefectConfirmationDto.workConfirmationFiles"
    )
    @Mapping(
        target = "document.apartmentDefectConfirmationData.defects.defect",
        source = "restDefectConfirmationDto.defects",
        qualifiedByName = "toApprovedDefectDataWithResult"
    )
    ApartmentDefectConfirmationDocument toApprovedApartmentDefectConfirmationDocument(
        @MappingTarget final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument,
        final RestDefectConfirmationDto restDefectConfirmationDto,
        final LocalDateTime currentDateTime,
        final Boolean pending,
        @Context final Function<String, String> retrieveAffairId
    );

    @Named("toApprovedDefectDataWithResult")
    default DefectData toApprovedDefectDataWithResult(
        final RestDefectDto restDefectDto, @Context final Function<String, String> retrieveAffairId
    ) {
        final DefectData defectData = toDefectData(restDefectDto, retrieveAffairId);
        if (nonNull(defectData)) {
            defectData.setIsApproved(true);
        }
        return defectData;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "folderId", source = "folderId")
    @Mapping(
        target = "generalContractorLogin", source = "document.apartmentDefectConfirmationData.generalContractorLogin"
    )
    @Mapping(target = "ccoData", source = "document.apartmentDefectConfirmationData.ccoData")
    @Mapping(
        target = "workConfirmationFiles", source = "document.apartmentDefectConfirmationData.workConfirmationFiles"
    )
    @Mapping(
        target = "defects", source = "document.apartmentDefectConfirmationData.defects.defect"
    )
    @Mapping(target = "rejectionReason", source = "document.apartmentDefectConfirmationData.rejectionReason")
    RestDefectConfirmationDto toApartmentDefectConfirmationDto(
        final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    );

    @Mapping(target = "oldEliminationDate", source = "oldEliminationDate")
    @Mapping(target = "eliminationDate", source = "eliminationDate")
    @Mapping(target = "eliminationDateComment", source = "eliminationDateComment")
    @Mapping(target = "itemRequired", source = "itemRequired")
    @Mapping(target = "itemRequiredComment", source = "itemRequiredComment")
    @Mapping(target = "notDefect", source = "isNotDefect")
    @Mapping(target = "notDefectComment", source = "notDefectComment")
    RestEliminationDto toRestEliminationDto(final EliminationData eliminationData);

    @Mapping(target = "document.apartmentDefectConfirmationData.pending", source = "pending")
    ApartmentDefectConfirmationDocument toEmptyApartmentDefectConfirmationDocument(final Boolean pending);

    List<WorkConfirmationFile> toWorkConfirmationFiles(final List<RestWorkConfirmationFile> workConfirmationFiles);
}
