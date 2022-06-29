package ru.croc.ugd.ssr.solr.converter.contractappointment;

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
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.solr.UgdSsrContractAppointment;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Optional;

/**
 * ContractAppointmentDocumentConverter.
 */
@Service
public class ContractAppointmentDocumentConverter
    extends SsrDocumentConverter<ContractAppointmentDocument, UgdSsrContractAppointment> {

    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final SolrContractAppointmentMapper solrContractAppointmentMapper;
    private final RealEstateDocumentService realEstateDocumentService;

    /**
     * Creates ContractAppointmentDocumentConverter.
     *
     * @param personDocumentService personDocumentService
     * @param cipService cipService
     * @param solrContractAppointmentMapper solrContractAppointmentMapper
     */
    public ContractAppointmentDocumentConverter(
        @Lazy final PersonDocumentService personDocumentService,
        @Lazy final CipService cipService,
        final SolrContractAppointmentMapper solrContractAppointmentMapper,
        @Lazy final RealEstateDocumentService realEstateDocumentService
    ) {
        this.personDocumentService = personDocumentService;
        this.cipService = cipService;
        this.solrContractAppointmentMapper = solrContractAppointmentMapper;
        this.realEstateDocumentService = realEstateDocumentService;
    }

    @NotNull
    @Override
    public DocumentType<ContractAppointmentDocument> getDocumentType() {
        return SsrDocumentTypes.CONTRACT_APPOINTMENT;
    }

    @NotNull
    @Override
    public UgdSsrContractAppointment convertDocument(
        @NotNull final ContractAppointmentDocument contractAppointmentDocument) {
        final UgdSsrContractAppointment ugdSsrContractAppointment
            = createDocument(getAnyAccessType(), contractAppointmentDocument.getId());

        final ContractAppointmentData contractAppointmentData = of(contractAppointmentDocument.getDocument())
            .map(ContractAppointment::getContractAppointmentData)
            .orElseThrow(() -> new SolrDocumentConversionException(contractAppointmentDocument.getId()));

        final PersonDocument person = ofNullable(contractAppointmentData)
            .map(ContractAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElse(null);
        final String applicantFullName = PersonUtils.getFullName(person);

        final RealEstateDataType realEstateDataType = ofNullable(person)
            .map(PersonUtils::getUnom)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .orElse(null);

        final Optional<CipType> optionalCip = ofNullable(contractAppointmentData)
            .map(ContractAppointmentData::getCipId)
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

        return solrContractAppointmentMapper.toUgdSsrContractAppointment(
            ugdSsrContractAppointment,
            contractAppointmentData,
            applicantFullName,
            cipAddress,
            area,
            districtCode
        );
    }

}
