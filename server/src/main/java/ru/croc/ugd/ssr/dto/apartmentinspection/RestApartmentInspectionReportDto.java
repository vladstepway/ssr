package ru.croc.ugd.ssr.dto.apartmentinspection;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.DefectDto;

import java.util.List;

@Value
@Builder
public class RestApartmentInspectionReportDto {
    private final String folderId;
    private final String actNumber;
    private final String personDocumentId;
    private final String address;
    private final List<DefectDto> defects;
}
