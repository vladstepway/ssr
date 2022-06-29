package ru.croc.ugd.ssr.solr.converter.shipping;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.shipping.ShippingApplicant;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.solr.UgdSsrShippingApplication;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import javax.annotation.Nonnull;

/**
 * ShippingApplicationDocumentConverter.
 */
@Service
@Slf4j
public class ShippingApplicationDocumentConverter
    extends SsrDocumentConverter<ShippingApplicationDocument, UgdSsrShippingApplication> {

    private final SolrShippingApplicationMapper solrShippingApplicationMapper;
    private final PersonDocumentService personDocumentService;

    public ShippingApplicationDocumentConverter(
        final SolrShippingApplicationMapper solrShippingApplicationMapper,
        @Lazy
        final PersonDocumentService personDocumentService
    ) {
        this.solrShippingApplicationMapper = solrShippingApplicationMapper;
        this.personDocumentService = personDocumentService;
    }

    @Override
    public DocumentType<ShippingApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.SHIPPING_APPLICATION;
    }

    @Nonnull
    @Override
    public UgdSsrShippingApplication convertDocument(@Nonnull ShippingApplicationDocument shippingApplicationDocument) {
        final UgdSsrShippingApplication solrShippingApplication =
            createDocument(getAnyAccessType(), shippingApplicationDocument.getId());

        final ShippingApplicationType shippingApplicationData = of(shippingApplicationDocument.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .orElseThrow(() -> new SolrDocumentConversionException(shippingApplicationDocument.getId()));

        final PersonType person = ofNullable(shippingApplicationData.getApplicant())
            .map(ShippingApplicant::getPersonUid)
            .map(this::fetchPersonDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .orElse(null);

        return solrShippingApplicationMapper.toUgdSsrShippingApplication(
            solrShippingApplication, shippingApplicationData, person
        );
    }

    private PersonDocument fetchPersonDocument(final String personId) {
        try {
            return personDocumentService.fetchDocument(personId);
        } catch (Exception e) {
            log.warn("Unable to fetchPersonDocument: {}", e.getMessage(), e);
            return null;
        }
    }
}
