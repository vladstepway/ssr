package ru.croc.ugd.ssr.solr.converter.apartmentdefectconfirmation;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentDefectConfirmation;
import ru.croc.ugd.ssr.ApartmentDefectConfirmationData;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.solr.UgdSsrApartmentDefectConfirmation;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * ApartmentDefectConfirmationDocumentConverter.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ApartmentDefectConfirmationDocumentConverter
    extends SsrDocumentConverter<ApartmentDefectConfirmationDocument, UgdSsrApartmentDefectConfirmation> {

    private final SolrApartmentDefectConfirmationMapper solrApartmentDefectConfirmationMapper;

    @NotNull
    @Override
    public DocumentType<ApartmentDefectConfirmationDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_DEFECT_CONFIRMATION;
    }

    @NotNull
    @Override
    public UgdSsrApartmentDefectConfirmation convertDocument(
        @NotNull final ApartmentDefectConfirmationDocument apartmentDefectConfirmationDocument
    ) {
        final UgdSsrApartmentDefectConfirmation ugdSsrApartmentDefectConfirmation
            = createDocument(getAnyAccessType(), apartmentDefectConfirmationDocument.getId());

        final ApartmentDefectConfirmationData apartmentDefectConfirmationData =
            of(apartmentDefectConfirmationDocument.getDocument())
                .map(ApartmentDefectConfirmation::getApartmentDefectConfirmationData)
                .orElseThrow(() -> new SolrDocumentConversionException(apartmentDefectConfirmationDocument.getId()));

        return solrApartmentDefectConfirmationMapper.toUgdSsrApartmentDefectConfirmation(
            ugdSsrApartmentDefectConfirmation, apartmentDefectConfirmationData
        );
    }
}
