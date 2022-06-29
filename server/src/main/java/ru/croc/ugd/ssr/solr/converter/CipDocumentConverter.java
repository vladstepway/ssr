package ru.croc.ugd.ssr.solr.converter;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.solr.UgdSsrCip;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * Конвертор для ЦИПов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CipDocumentConverter extends SsrDocumentConverter<CipDocument, UgdSsrCip> {
    private RealEstateDocumentService realEstateDocumentService;

    @Override
    public DocumentType<CipDocument> getDocumentType() {
        return SsrDocumentTypes.CIP;
    }

    @Override
    public UgdSsrCip convertDocument(@NotNull CipDocument document) {
        final UgdSsrCip position = createDocument(getAnyAccessType(), document.getId());

        final CipType source = of(document.getDocument())
            .map(Cip::getCipData)
            .orElseThrow(() -> new SolrDocumentConversionException(document.getId()));

        if (null != source.getAddress()) {
            position.setUgdSsrCipAddress(source.getAddress());
        }
        if (null != source.getArea()) {
            position.setUgdSsrCipArea(source.getArea().getName());
        }
        if (null != source.getCipCode()) {
            position.setUgdSsrCipCode(source.getCipCode());
        }
        if (null != source.getPhone()) {
            position.setUgdSsrCipPhone(source.getPhone());
        }
        if (null != source.getWorkTime()) {
            position.setUgdSsrCipWorkTime(source.getWorkTime());
        }
        if (null != source.getCipDateEnd()) {
            position.setUgdSsrCipEndDate(source.getCipDateEnd());
        }
        if (null != source.getCipDateStart()) {
            position.setUgdSsrCipStartDate(source.getCipDateStart());
        }
        if (null != source.getCipStatus()) {
            position.setUgdSsrCipStatus(source.getCipStatus());
        }
        if (null != source.getCipDelReason()) {
            position.setUgdSsrCipDelReason(source.getCipDelReason());
        }
        if (null != source.getDistrict()) {
            position.setUgdSsrCipDistrict(source.getDistrict().getName());
        }

        position.setUgdSsrCipResettelmentCount(0L);
        position.setUgdSsrCipSettelmentCount(0L);

        if (null != source.getLinkedHouses()) {
            position.setUgdSsrCipSettelmentCount((long) source.getLinkedHouses().getLink().size());

            position.setUgdSsrCipResettelmentCount((long) source.getLinkedHouses()
                .getLink()
                .stream()
                .mapToInt(link -> countRealEstatesByLinkId(link.getId()))
                .sum());
        }

        return position;
    }

    private int countRealEstatesByLinkId(final String id) {
        try {
            return realEstateDocumentService.fetchDocumentByCcoId(id).size();
        } catch (Exception e) {
            log.warn("Unable to countRealEstatesByLinkId: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Autowired
    public void setRealEstateDocumentService(@Lazy RealEstateDocumentService realEstateDocumentService) {
        this.realEstateDocumentService = realEstateDocumentService;
    }
}
