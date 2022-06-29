package ru.croc.ugd.ssr.solr.converter.commissioninspection;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.application.Applicant;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisation;
import ru.croc.ugd.ssr.commissionorganisation.CommissionInspectionOrganisationData;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionOrganisationDocumentService;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrCommissionInspection;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * CommissionInspectionDocumentConverter.
 */
@Slf4j
@Service
public class CommissionInspectionDocumentConverter
    extends SsrDocumentConverter<CommissionInspectionDocument, UgdSsrCommissionInspection> {

    private final PersonDocumentService personDocumentService;
    private final SolrCommissionInspectionMapper solrCommissionInspectionMapper;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final CipService cipService;
    private final CommissionInspectionOrganisationDocumentService commissionInspectionOrganisationDocumentService;
    private final BpmService bpmService;

    /**
     * Creates CommissionInspectionDocumentConverter.
     *
     * @param personDocumentService            personDocumentService
     * @param solrCommissionInspectionMapper   solrCommissionInspectionMapper
     * @param capitalConstructionObjectService capitalConstructionObjectService
     * @param realEstateDocumentService        realEstateDocumentService
     * @param cipService                       cipService
     */
    public CommissionInspectionDocumentConverter(
        @Lazy final PersonDocumentService personDocumentService,
        final SolrCommissionInspectionMapper solrCommissionInspectionMapper,
        @Lazy final CapitalConstructionObjectService capitalConstructionObjectService,
        @Lazy final RealEstateDocumentService realEstateDocumentService,
        @Lazy final CipService cipService,
        @Lazy final CommissionInspectionOrganisationDocumentService commissionInspectionOrganisationDocumentService,
        final BpmService bpmService
    ) {
        this.personDocumentService = personDocumentService;
        this.solrCommissionInspectionMapper = solrCommissionInspectionMapper;
        this.capitalConstructionObjectService = capitalConstructionObjectService;
        this.realEstateDocumentService = realEstateDocumentService;
        this.cipService = cipService;
        this.commissionInspectionOrganisationDocumentService = commissionInspectionOrganisationDocumentService;
        this.bpmService = bpmService;
    }

    @NotNull
    @Override
    public DocumentType<CommissionInspectionDocument> getDocumentType() {
        return SsrDocumentTypes.COMMISSION_INSPECTION;
    }

    @NotNull
    @Override
    public UgdSsrCommissionInspection convertDocument(
        @NotNull final CommissionInspectionDocument commissionInspectionDocument) {
        final UgdSsrCommissionInspection ugdSsrCommissionInspection
            = createDocument(getAnyAccessType(), commissionInspectionDocument.getId());

        final CommissionInspectionData commissionInspectionData =
            of(commissionInspectionDocument.getDocument())
                .map(CommissionInspection::getCommissionInspectionData)
                .orElseThrow(() -> new SolrDocumentConversionException(commissionInspectionDocument.getId()));

        final String personId = ofNullable(commissionInspectionData)
            .map(CommissionInspectionData::getApplicant)
            .map(Applicant::getPersonId)
            .orElse(null);

        final Optional<PersonDocument> personDocumentOptional = personDocumentService.fetchById(personId);
        final String personFio = personDocumentOptional
            .map(PersonUtils::getFullName)
            .orElse(null);

        final Optional<CcoSolrResponse> optionalCco = ofNullable(commissionInspectionData)
            .map(CommissionInspectionData::getCcoUnom)
            .map(this::retrieveCcoSolrResponse);

        final Optional<CipType> optionalCip = ofNullable(commissionInspectionData)
            .map(CommissionInspectionData::getCipId)
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData);

        final Optional<RealEstateDataType> optionalRealEstate = personDocumentOptional
            .map(PersonUtils::getUnom)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData);

        final String area = optionalCco
            .map(CcoSolrResponse::getArea)
            .orElseGet(() -> optionalCip
                .map(CipType::getArea)
                .map(CipType.Area::getName)
                .orElseGet(() -> optionalRealEstate
                    .map(RealEstateDataType::getDISTRICT)
                    .map(NsiType::getName)
                    .orElse(null)
                ));

        final String district = optionalCco
            .map(CcoSolrResponse::getDistrict)
            .orElseGet(() -> optionalCip
                .map(CipType::getDistrict)
                .map(CipType.District::getName)
                .orElseGet(() -> optionalRealEstate
                    .map(RealEstateDataType::getMunOkrugP5)
                    .map(NsiType::getName)
                    .orElse(null)
                ));

        final String districtCode = optionalCco
            .map(CcoSolrResponse::getDistrict)
            .flatMap(DistrictCode::ofCcoName)
            .map(DistrictCode::getCode)
            .orElseGet(() -> optionalCip
                .map(CipType::getDistrict)
                .map(CipType.District::getCode)
                .orElseGet(() -> optionalRealEstate
                    .map(RealEstateDataType::getMunOkrugP5)
                    .map(NsiType::getName)
                    .flatMap(DistrictCode::ofName)
                    .map(DistrictCode::getCode)
                    .orElse(null)
                ));

        final Integer organisationType =
            ofNullable(commissionInspectionData)
                .map(CommissionInspectionData::getOrganisationId)
                .map(commissionInspectionOrganisationDocumentService::fetchDocument)
                .map(CommissionInspectionOrganisationDocument::getDocument)
                .map(CommissionInspectionOrganisation::getCommissionInspectionOrganisationData)
                .map(CommissionInspectionOrganisationData::getType)
                .orElse(null);

        final String taskId = ofNullable(commissionInspectionData)
            .map(CommissionInspectionData::getProcessInstanceId)
            .map(bpmService::getTasksByProcessId)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(Task::getId)
            .orElse(null);

        return solrCommissionInspectionMapper.toUgdSsrCommissionInspection(
            ugdSsrCommissionInspection,
            commissionInspectionData,
            personFio,
            area,
            district,
            districtCode,
            organisationType,
            taskId
        );
    }

    private CcoSolrResponse retrieveCcoSolrResponse(final String unom) {
        try {
            return capitalConstructionObjectService.getCcoSolrResponseByUnom(unom);
        } catch (Exception e) {
            log.warn("Unable to retrieveCcoSolrResponse: {}", e.getMessage(), e);
            return null;
        }
    }

}
