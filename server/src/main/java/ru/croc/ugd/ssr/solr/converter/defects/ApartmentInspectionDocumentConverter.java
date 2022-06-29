package ru.croc.ugd.ssr.solr.converter.defects;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentInspection;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.config.ApartmentInspectionProperties;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrApartmentInspection;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.ssrcco.SsrCco;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * ApartmentInspectionDocumentConverter.
 */
@Service
@Slf4j
public class ApartmentInspectionDocumentConverter
    extends SsrDocumentConverter<ApartmentInspectionDocument, UgdSsrApartmentInspection> {

    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonDocumentService personDocumentService;
    private final SolrApartmentInspectionMapper solrApartmentInspectionMapper;
    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final ApartmentInspectionProperties apartmentInspectionProperties;
    private final UserService userService;

    public ApartmentInspectionDocumentConverter(
        @Lazy final RealEstateDocumentService realEstateDocumentService,
        @Lazy final PersonDocumentService personDocumentService,
        final SolrApartmentInspectionMapper solrApartmentInspectionMapper,
        @Lazy final SsrCcoDocumentService ssrCcoDocumentService,
        final ApartmentInspectionProperties apartmentInspectionProperties,
        final UserService userService
    ) {
        this.realEstateDocumentService = realEstateDocumentService;
        this.personDocumentService = personDocumentService;
        this.solrApartmentInspectionMapper = solrApartmentInspectionMapper;
        this.ssrCcoDocumentService = ssrCcoDocumentService;
        this.apartmentInspectionProperties = apartmentInspectionProperties;
        this.userService = userService;
    }

    @Override
    public DocumentType<ApartmentInspectionDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_INSPECTION;
    }

    @Nonnull
    @Override
    public UgdSsrApartmentInspection convertDocument(@Nonnull ApartmentInspectionDocument apartmentInspectionDocument) {
        final UgdSsrApartmentInspection solrApartmentInspection =
            createDocument(getAnyAccessType(), apartmentInspectionDocument.getId());

        final ApartmentInspectionType apartmentInspection = of(apartmentInspectionDocument.getDocument())
            .map(ApartmentInspection::getApartmentInspectionData)
            .orElseThrow(() -> new SolrDocumentConversionException(apartmentInspectionDocument.getId()));

        final PersonType person = ofNullable(apartmentInspection.getPersonID())
            .map(this::fetchPersonDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);

        final RealEstateDataType realEstate = ofNullable(person)
            .map(PersonType::getUNOM)
            .map(String::valueOf)
            .map(this::fetchRealEstateByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .orElse(null);

        final FlatType flat = findFlat(person, realEstate);

        final SsrCcoData ssrCco = ssrCcoDocumentService
            .fetchByUnom(apartmentInspection.getUnom())
            .map(SsrCcoDocument::getDocument)
            .map(SsrCco::getSsrCcoData)
            .orElse(null);

        if (solrApartmentInspection.getUgdSsrApartmentInspectionIsDeleted() == null) {
            solrApartmentInspection.setUgdSsrApartmentInspectionIsDeleted(false);
        }

        final UserBean closingInitiator = ofNullable(apartmentInspection.getClosingInitiatorLogin())
            .map(userService::getUserBeanByLogin)
            .orElse(null);

        return solrApartmentInspectionMapper.toUgdSsrApartmentInspection(
            solrApartmentInspection,
            apartmentInspection,
            realEstate,
            flat,
            person,
            ssrCco,
            closingInitiator,
            apartmentInspectionProperties.getKpUgsInn()
        );
    }

    private PersonDocument fetchPersonDocument(final String documentId) {
        try {
            return personDocumentService.fetchDocument(documentId);
        } catch (Exception e) {
            log.warn("Unable to fetchPersonDocument: {}", e.getMessage(), e);
            return null;
        }
    }

    private RealEstateDocument fetchRealEstateByUnom(final String unom) {
        try {
            return realEstateDocumentService.fetchDocumentByUnom(unom);
        } catch (Exception e) {
            log.warn("Unable to fetchRealEstateByUnom: {}", e.getMessage(), e);
            return null;
        }
    }

    private FlatType findFlat(final PersonType person, final RealEstateDataType realEstate) {
        if (person == null || StringUtils.isBlank(person.getFlatID())
            || realEstate == null || realEstate.getFlats() == null
        ) {
            return null;
        }

        Optional<FlatType> optFlatType = realEstate.getFlats().getFlat()
            .stream()
            .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
            .filter(f -> f.getFlatID().equals(person.getFlatID()))
            .findAny();
        return optFlatType.orElse(null);
    }
}
