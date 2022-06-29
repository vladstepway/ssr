package ru.croc.ugd.ssr.service.courtinfo;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.courtinfo.RestCourtInfoDto;
import ru.croc.ugd.ssr.mapper.CourtInfoMapper;
import ru.croc.ugd.ssr.model.courtinfo.CourtInfoDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPCourtInfoType;
import ru.croc.ugd.ssr.service.document.CourtInfoDocumentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DefaultCourtInfoService implements CourtInfoService {

    private final CourtInfoDocumentService courtInfoDocumentService;
    private final CourtInfoMapper courtInfoMapper;

    @Override
    public List<RestCourtInfoDto> fetchAllByAffairId(final String affairId) {
        return courtInfoDocumentService.fetchAllByAffairId(affairId)
            .stream()
            .map(courtInfoMapper::toRestCourtInfoDto)
            .collect(Collectors.toList());
    }

    @Override
    public void createOrUpdateCourtInfo(final SuperServiceDGPCourtInfoType courtInfoType) {
        final CourtInfoDocument courtInfoDocument = ofNullable(courtInfoType)
            .flatMap(courtInfo -> courtInfoDocumentService.fetchByCaseIdAndAffairId(
                courtInfoType.getCaseId(), courtInfo.getAffairId()
            ))
            .map(document -> courtInfoMapper.toCourtInfoDocument(document, courtInfoType))
            .orElseGet(() -> courtInfoMapper.toCourtInfoDocument(new CourtInfoDocument(), courtInfoType));

        courtInfoDocumentService.createOrUpdateDocument(courtInfoDocument, "createOrUpdateCourtInfo");
    }
}
