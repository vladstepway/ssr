package ru.croc.ugd.ssr.solr.converter.flatappointment;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.flatappointment.Applicant;
import ru.croc.ugd.ssr.flatappointment.FlatAppointment;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.solr.UgdSsrFlatAppointment;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Optional;

/**
 * FlatAppointmentDocumentConverter.
 */
@Service
public class FlatAppointmentDocumentConverter
    extends SsrDocumentConverter<FlatAppointmentDocument, UgdSsrFlatAppointment> {

    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final SolrFlatAppointmentMapper solrFlatAppointmentMapper;
    private final RealEstateDocumentService realEstateDocumentService;

    /**
     * Creates FlatAppointmentDocumentConverter.
     *
     * @param personDocumentService     personDocumentService
     * @param cipService                cipService
     * @param solrFlatAppointmentMapper solrFlatAppointmentMapper
     */
    public FlatAppointmentDocumentConverter(
        @Lazy final PersonDocumentService personDocumentService,
        @Lazy final CipService cipService,
        @Lazy final RealEstateDocumentService realEstateDocumentService,
        final SolrFlatAppointmentMapper solrFlatAppointmentMapper
    ) {
        this.personDocumentService = personDocumentService;
        this.cipService = cipService;
        this.solrFlatAppointmentMapper = solrFlatAppointmentMapper;
        this.realEstateDocumentService = realEstateDocumentService;
    }

    @NotNull
    @Override
    public DocumentType<FlatAppointmentDocument> getDocumentType() {
        return SsrDocumentTypes.FLAT_APPOINTMENT;
    }

    @NotNull
    @Override
    public UgdSsrFlatAppointment convertDocument(
        @NotNull final FlatAppointmentDocument flatAppointmentDocument
    ) {
        final UgdSsrFlatAppointment ugdSsrFlatAppointment
            = createDocument(getAnyAccessType(), flatAppointmentDocument.getId());

        final FlatAppointmentData flatAppointmentData = of(flatAppointmentDocument.getDocument())
            .map(FlatAppointment::getFlatAppointmentData)
            .orElseThrow(() -> new SolrDocumentConversionException(flatAppointmentDocument.getId()));

        final Optional<PersonDocument> personDocumentOptional = ofNullable(flatAppointmentData)
            .map(FlatAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById);

        final String applicantFullName = personDocumentOptional
            .map(PersonUtils::getFullName)
            .orElse(null);

        final RealEstateDataType realEstateDataType = personDocumentOptional
            .map(PersonUtils::getUnom)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .orElse(null);

        final Optional<CipType> optionalCip = ofNullable(flatAppointmentData)
            .map(FlatAppointmentData::getCipId)
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData);

        final String cipAddress = optionalCip
            .map(CipUtils::getCipAddress)
            .orElse(null);

        final String area = optionalCip
            .map(CipType::getArea)
            .map(CipType.Area::getName)
            .orElseGet(() -> ofNullable(realEstateDataType)
                .map(RealEstateDataType::getDISTRICT)
                .map(NsiType::getName)
                .orElse(null)
            );

        final String districtCode = optionalCip
            .map(CipType::getDistrict)
            .map(CipType.District::getCode)
            .orElseGet(() -> ofNullable(realEstateDataType)
                .map(RealEstateDataType::getMunOkrugP5)
                .map(NsiType::getName)
                .flatMap(DistrictCode::ofName)
                .map(DistrictCode::getCode)
                .orElse(null)
            );

        return solrFlatAppointmentMapper.toUgdSsrFlatAppointment(
            ugdSsrFlatAppointment,
            flatAppointmentData,
            applicantFullName,
            cipAddress,
            area,
            districtCode
        );
    }
}
