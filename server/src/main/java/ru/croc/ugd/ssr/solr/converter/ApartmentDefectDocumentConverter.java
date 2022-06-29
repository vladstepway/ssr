package ru.croc.ugd.ssr.solr.converter;

import static java.util.Optional.of;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentDefect;
import ru.croc.ugd.ssr.ApartmentDefectType;
import ru.croc.ugd.ssr.model.ApartmentDefectDocument;
import ru.croc.ugd.ssr.solr.UgdSsrDefect;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import javax.validation.constraints.NotNull;

/**
 * ApartmentDefectDocumentConverter.
 */
@Service
public class ApartmentDefectDocumentConverter
    extends SsrDocumentConverter<ApartmentDefectDocument, UgdSsrDefect> {

    @Override
    public DocumentType<ApartmentDefectDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_DEFECT;
    }

    @Override
    public UgdSsrDefect convertDocument(@NotNull ApartmentDefectDocument apartmentDefectDocument) {
        final UgdSsrDefect solrApartmentDefect =
            createDocument(getAnyAccessType(), apartmentDefectDocument.getId());

        final ApartmentDefectType apartmentDefectType = of(apartmentDefectDocument.getDocument())
            .map(ApartmentDefect::getApartmentDefectData)
            .orElseThrow(() -> new SolrDocumentConversionException(apartmentDefectDocument.getId()));

        solrApartmentDefect.setUgdSsrDefectFlatElement(apartmentDefectType.getFlatElement());
        solrApartmentDefect.setUgdSsrDefectDescription(apartmentDefectType.getDescription());
        return solrApartmentDefect;
    }
}
