package ru.croc.ugd.ssr.solr.converter;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FirstFlowErrorAnalytics;
import ru.croc.ugd.ssr.FirstFlowErrorAnalyticsData;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.solr.UgdSsrFirstFlowErrorAnalytics;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.format.DateTimeFormatter;

/**
 * Конвертор для БП обработки 1ого потока ДГИ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FirstFlowErrorAnalyticsDocumentConverter
    extends SsrDocumentConverter<FirstFlowErrorAnalyticsDocument, UgdSsrFirstFlowErrorAnalytics> {
    private final DocumentConverterService service;

    @Override
    public DocumentType<FirstFlowErrorAnalyticsDocument> getDocumentType() {
        return SsrDocumentTypes.FIRST_FLOW_ERROR_ANALYTICS;
    }

    @Override
    public UgdSsrFirstFlowErrorAnalytics convertDocument(@NotNull FirstFlowErrorAnalyticsDocument document) {
        final UgdSsrFirstFlowErrorAnalytics position = createDocument(getAnyAccessType(), document.getId());
        final FirstFlowErrorAnalyticsData source = of(document.getDocument())
            .map(FirstFlowErrorAnalytics::getData)
            .orElseThrow(() -> new SolrDocumentConversionException(document.getId()));

        if (null != source.getUnom()) {
            RealEstateDocument realEstateByUnom = getRealEstateDocument(source.getUnom());
            if (realEstateByUnom != null) {
                RealEstateDataType realEstateData = realEstateByUnom.getDocument().getRealEstateData();
                if (StringUtils.isNotBlank(realEstateData.getAddress())) {
                    position.setFirstRow("Данные по дому " + realEstateData.getAddress());
                }
                String secondRow = "(" + source.getUnom() + ")";
                if (realEstateData.getUpdatedFromDgiDate() != null) {
                    secondRow += " от " + realEstateData.getUpdatedFromDgiDate().format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    );
                }
                position.setSecondRow(secondRow);
            }
        }
        return position;
    }

    private RealEstateDocument getRealEstateDocument(final String unom) {
        try {
            return service.getRealEstateByUnom(unom);
        } catch (Exception e) {
            log.warn("Unable to getRealEstateDocument: {}", e.getMessage(), e);
            return null;
        }
    }
}
