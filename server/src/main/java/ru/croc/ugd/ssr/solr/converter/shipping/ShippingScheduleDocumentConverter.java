package ru.croc.ugd.ssr.solr.converter.shipping;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.ShippingDayScheduleDocument;
import ru.croc.ugd.ssr.shipping.ShippingDaySchedule;
import ru.croc.ugd.ssr.shipping.ShippingDayScheduleType;
import ru.croc.ugd.ssr.solr.UgdSsrShippingDaySchedule;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import javax.annotation.Nonnull;

/**
 * ShippingScheduleDocumentConverter.
 */
@Service
@AllArgsConstructor
public class ShippingScheduleDocumentConverter
    extends SsrDocumentConverter<ShippingDayScheduleDocument, UgdSsrShippingDaySchedule> {

    private final SolrShippingDayScheduleMapper solrShippingDayScheduleMapper;

    @Override
    public DocumentType<ShippingDayScheduleDocument> getDocumentType() {
        return SsrDocumentTypes.SHIPPING_DAY_SCHEDULE;
    }

    @Nonnull
    @Override
    public UgdSsrShippingDaySchedule convertDocument(@Nonnull ShippingDayScheduleDocument shippingDayScheduleDocument) {
        final UgdSsrShippingDaySchedule solrShippingSchedule =
            createDocument(getAnyAccessType(), shippingDayScheduleDocument.getId());

        final ShippingDayScheduleType shippingDayScheduleType = of(shippingDayScheduleDocument.getDocument())
            .map(ShippingDaySchedule::getShippingDayScheduleData)
            .orElseThrow(() -> new SolrDocumentConversionException(shippingDayScheduleDocument.getId()));

        return solrShippingDayScheduleMapper.toUgdSsrShippingDaySchedule(
            solrShippingSchedule, shippingDayScheduleType
        );
    }
}
