package ru.croc.ugd.ssr.service.resettlementrequest;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.dto.CcoInfo;
import ru.croc.ugd.ssr.dto.resettlementrequest.RestCcoDto;
import ru.croc.ugd.ssr.dto.resettlementrequest.RestFullResettlementDto;
import ru.croc.ugd.ssr.dto.resettlementrequest.RestPartResettlementDto;
import ru.croc.ugd.ssr.dto.resettlementrequest.RestResettlementDto;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultResettlementRequestService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRestResettlementRequestService implements RestResettlementRequestService {

    private final ResettlementRequestDocumentService resettlementRequestDocumentService;
    private final CapitalConstructionObjectService ccoService;

    @Override
    public RestResettlementDto fetchByRealEstateUnom(final String realEstateUnom) {
        final List<RestResettlementDto> resettlementDtos = ofNullable(realEstateUnom)
            .map(resettlementRequestDocumentService::fetchAllByRealEstateUnom)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(document -> extractResettlementInfo(document, realEstateUnom))
            .flatMap(List::stream)
            .collect(Collectors.toList());

        return RestResettlementDto.builder()
            .realEstateUnom(realEstateUnom)
            .fullResettlements(
                resettlementDtos.stream()
                    .map(RestResettlementDto::getFullResettlements)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList())
            )
            .partResettlements(
                resettlementDtos.stream()
                    .map(RestResettlementDto::getPartResettlements)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList())
            )
            .build();
    }

    private List<RestResettlementDto> extractResettlementInfo(
        final ResettlementRequestDocument document, final String realEstateUnom
    ) {
        final LocalDate startResettlementDate = ofNullable(document)
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getStartResettlementDate)
            .orElse(null);

        return ofNullable(document)
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getHousesToSettle)
            .map(List::stream)
            .orElse(Stream.empty())
            .flatMap(houseToSettle -> extractResettlementInfo(startResettlementDate, houseToSettle, realEstateUnom))
            .collect(Collectors.toList());
    }

    private Stream<RestResettlementDto> extractResettlementInfo(
        final LocalDate startResettlementDate, final HouseToSettle houseToSettle, final String realEstateUnom
    ) {
        final String settleUnom = houseToSettle.getCapitalConstructionObjectUnom();

        final CcoInfo ccoInfo = ccoService.getCcoInfoByUnom(settleUnom);
        final String address = ofNullable(ccoInfo)
            .map(CcoInfo::getAddress)
            .orElse(null);
        final String cadNumber = ofNullable(ccoInfo)
            .map(CcoInfo::getCadNumber)
            .orElse(null);

        final RestCcoDto restCcoDto = RestCcoDto.builder()
            .settleUnom(settleUnom)
            .settleAddress(address)
            .settleCadastralNumber(cadNumber)
            .build();

        return ofNullable(houseToSettle.getHousesToResettle())
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(houseToResettle -> realEstateUnom.equals(houseToResettle.getRealEstateUnom()))
            .map(houseToResettle -> {
                if ("part".equals(houseToResettle.getResettlementBy())) {
                    final RestPartResettlementDto partResettlementDto = RestPartResettlementDto.builder()
                        .flatIds(houseToResettle.getFlats())
                        .startResettlementDate(startResettlementDate)
                        .ccoInfo(restCcoDto)
                        .build();
                    return RestResettlementDto.builder()
                        .partResettlements(Collections.singletonList(partResettlementDto))
                        .build();
                } else {
                    final RestFullResettlementDto fullResettlementDto = RestFullResettlementDto.builder()
                        .startResettlementDate(startResettlementDate)
                        .ccoInfo(restCcoDto)
                        .build();
                    return RestResettlementDto.builder()
                        .fullResettlements(Collections.singletonList(fullResettlementDto))
                        .build();
                }
            });
    }
}
