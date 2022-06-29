package ru.croc.ugd.ssr.service.compensation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.compensationflat.CompensationFlatType;
import ru.croc.ugd.ssr.dto.compensation.RestCompensationDto;
import ru.croc.ugd.ssr.dto.compensation.RestCreateCompensationDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.CompensationMapper;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.compensation.CompensationDocument;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.document.CompensationDocumentService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultRestCompensationService.
 */
@Service
@AllArgsConstructor
public class DefaultRestCompensationService implements RestCompensationService {

    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final CompensationDocumentService compensationDocumentService;
    private final CompensationMapper compensationMapper;

    @Override
    public RestCompensationDto fetchById(final String id) {
        final CompensationDocument compensationDocument = compensationDocumentService.fetchDocument(id);
        return compensationMapper.toRestCompensationDto(compensationDocument);
    }

    @Override
    public List<RestCompensationDto> fetchAllByUnom(final String unom) {
        final List<CompensationDocument> compensationDocuments = compensationDocumentService
            .findAllByUnom(unom);

        return compensationDocuments.stream()
            .map(compensationMapper::toRestCompensationDto)
            .collect(Collectors.toList());
    }

    private void create(final RestCompensationDto restCompensationDto) {
        final CompensationDocument compensationDocument = compensationMapper
            .toCompensationDocument(new CompensationDocument(), restCompensationDto);
        compensationDocumentService
            .createDocument(compensationDocument, true, "");
    }

    @Override
    public RestCompensationDto update(final String id, final RestCompensationDto restCompensationDto) {
        final CompensationDocument compensationDocument = compensationDocumentService.fetchDocument(id);
        final CompensationDocument compensationToUpdate = compensationMapper
            .toCompensationDocument(compensationDocument, restCompensationDto);
        final CompensationDocument updatedCompensation = compensationDocumentService
            .updateDocument(id, compensationToUpdate, true, true, null);
        return compensationMapper.toRestCompensationDto(updatedCompensation);
    }

    @Override
    public void createForResettlementRequest(
        final RestCreateCompensationDto restCreateCompensationDto
    ) {
        final String resettlementRequestId = Optional.ofNullable(restCreateCompensationDto.getResettlementRequestId())
            .orElseThrow(() -> new SsrException("Отсутствует идентификатор запроса на переселение домов/квартир"));

        final ResettlementRequestDocument document =
            resettlementRequestDocumentService.fetchDocument(resettlementRequestId);
        final List<HouseToResettle> housesToResettle =
            Optional.ofNullable(document.getDocument().getMain())
                .map(ResettlementRequestType::getHousesToSettle)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(HouseToSettle::getHousesToResettle)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        housesToResettle.forEach(houseToResettle -> createOrUpdateCompensation(resettlementRequestId, houseToResettle));
    }

    private void createOrUpdateCompensation(final String resettlementRequestId, final HouseToResettle houseToResettle) {
        final List<CompensationFlatType> compensationFlats =
            Optional.ofNullable(houseToResettle.getCompensationFlats())
                .orElseGet(ArrayList::new);

        final String realEstateId = houseToResettle.getRealEstateId();

        final RestCompensationDto compensationDto = RestCompensationDto.builder()
            .resettlementRequestId(resettlementRequestId)
            .realEstateId(realEstateId)
            .unom(new BigInteger(houseToResettle.getRealEstateUnom()))
            .flats(compensationMapper.toRestCompensationFlatDtoList(compensationFlats))
            .build();

        final CompensationDocument compensationDocument =
            compensationDocumentService.findByResettlementRequestIdAndRealEstateId(
                resettlementRequestId, realEstateId
            ).orElse(null);
        if (compensationDocument == null) {
            if (compensationFlats.size() > 0) {
                create(compensationDto);
            }
        } else {
            update(compensationDocument.getId(), compensationDto);
        }
    }
}
