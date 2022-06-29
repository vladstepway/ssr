package ru.croc.ugd.ssr.service.flowerrorreport;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.affaircollation.RestAffairCollationDto;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.flowreportederror.PersonInfoType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.affaircollation.AffairCollationService;
import ru.croc.ugd.ssr.service.document.FlowReportedErrorDocumentService;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class FlowErrorReportService {
    public static final String FOUND_SEVERAL_ERROR_TYPE = "Найдены два одинаковых жителя";
    public static final String NOT_FOUND_ERROR_TYPE = "Житель отсутствует";
    public static final String WRONG_COMMUNAL_LIVER_ERROR_TYPE =
        "По жителю не указан номер комнаты/нет признака коммунальной квартиры";

    private final SystemProperties systemProperties;
    private final FlowErrorReportMapper flowErrorReportMapper;
    private final FlowReportedErrorDocumentService flowReportedErrorDocumentService;
    private final FlatService flatService;
    private final AffairCollationService affairCollationService;

    public void reportPersonNotFoundError(
        final FlowType flowType,
        final String personId,
        final String affairId,
        final String flowMessage
    ) {
        final FlowReportedErrorData flowReportedErrorData = new FlowReportedErrorData();

        final PersonInfoType personInfoType = new PersonInfoType();
        personInfoType.setPersonId(personId);
        personInfoType.setAffairId(affairId);
        flowReportedErrorData.setPersonFirst(personInfoType);
        flowReportedErrorData.setOriginalFlowMessage(flowMessage);
        flowReportedErrorData.setFlowType(flowType.getFullName());
        flowReportedErrorData.setErrorType(NOT_FOUND_ERROR_TYPE);
        flowReportedErrorData.setReportedDate(LocalDate.now());

        flowReportedErrorDocumentService.createDocument(flowReportedErrorData);

        affairCollationService.create(
            RestAffairCollationDto
                .builder()
                .affairId(affairId)
                .build(),
            false
        );
    }

    public void reportPersonDuplicateFoundError(
        final FlowType flowType,
        final PersonDocument firstPersonDocument,
        final PersonDocument secondPersonDocument,
        final String flowMessage
    ) {
        final FlowReportedErrorData flowReportedErrorData = new FlowReportedErrorData();

        flowReportedErrorData
            .setPersonFirst(flowErrorReportMapper.toPersonInfoType(
                firstPersonDocument,
                getRealEstateDataAndFlatInfoDto(firstPersonDocument),
                LinkProperties.builder()
                    .personId(firstPersonDocument.getId())
                    .domain(systemProperties.getDomain())
                    .build()
            ));
        flowReportedErrorData
            .setPersonSecond(flowErrorReportMapper.toPersonInfoType(
                secondPersonDocument,
                getRealEstateDataAndFlatInfoDto(secondPersonDocument),
                LinkProperties.builder()
                    .personId(secondPersonDocument.getId())
                    .domain(systemProperties.getDomain())
                    .build()
            ));
        flowReportedErrorData.setOriginalFlowMessage(flowMessage);
        flowReportedErrorData.setFlowType(flowType.getFullName());
        flowReportedErrorData.setErrorType(FOUND_SEVERAL_ERROR_TYPE);
        flowReportedErrorData.setReportedDate(LocalDate.now());

        flowReportedErrorDocumentService.createDocument(flowReportedErrorData);
    }

    public void reportPersonNotValidCommunalLiverError(
        final FlowType flowType,
        final PersonDocument personDocument,
        final boolean isCommunalFlat,
        final String flowMessage
    ) {
        final FlowReportedErrorData flowReportedErrorData = new FlowReportedErrorData();

        flowReportedErrorData
            .setPersonFirst(flowErrorReportMapper.toPersonInfoType(
                personDocument,
                getRealEstateDataAndFlatInfoDto(personDocument),
                LinkProperties.builder()
                    .personId(personDocument.getId())
                    .domain(systemProperties.getDomain())
                    .build()
            ));
        flowReportedErrorData.getPersonFirst().setIsCommunal(isCommunalFlat);
        flowReportedErrorData.setOriginalFlowMessage(flowMessage);
        flowReportedErrorData.setFlowType(flowType.getFullName());
        flowReportedErrorData.setErrorType(WRONG_COMMUNAL_LIVER_ERROR_TYPE);
        flowReportedErrorData.setReportedDate(LocalDate.now());

        flowReportedErrorDocumentService.createDocument(flowReportedErrorData);
    }

    private RealEstateDataAndFlatInfoDto getRealEstateDataAndFlatInfoDto(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .orElse(null);
    }
}
