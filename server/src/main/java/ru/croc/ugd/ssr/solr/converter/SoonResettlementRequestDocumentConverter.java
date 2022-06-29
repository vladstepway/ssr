package ru.croc.ugd.ssr.solr.converter;

import static java.util.Optional.of;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.SoonResettlementRequest;
import ru.croc.ugd.ssr.SoonResettlementRequestType;
import ru.croc.ugd.ssr.model.SoonResettlementRequestDocument;
import ru.croc.ugd.ssr.solr.UgdSsrSoonResettlementRequest;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * Реализация конвертера для Запроса на скорое переселение.
 */
@Slf4j
@Component
public class SoonResettlementRequestDocumentConverter
    extends SsrDocumentConverter<SoonResettlementRequestDocument, UgdSsrSoonResettlementRequest> {

    @Override
    public DocumentType<SoonResettlementRequestDocument> getDocumentType() {
        return SsrDocumentTypes.SOON_RESETTLEMENT_REQUEST;
    }

    @Override
    public UgdSsrSoonResettlementRequest convertDocument(@NotNull SoonResettlementRequestDocument document) {
        final UgdSsrSoonResettlementRequest position = createDocument(getAnyAccessType(), document.getId());
        final SoonResettlementRequestType source = of(document.getDocument())
            .map(SoonResettlementRequest::getMain)
            .orElseThrow(() -> new SolrDocumentConversionException(document.getId()));

        // Переносим данные из документа в то, что отправим на индексацию в Solr
        // Разработчик, помни! Исходный документ - это всего лишь JSON - поэтому null-ом может
        // оказаться всё что угодно. Предохраняйтесь от NPE!
        position.setUgdSsrSoonResettlementRequestId(source.getSoonResettlementRequestID());
        if (null != source.getRealEstates()) {
            position.setUgdSsrSoonResettlementRequestRealEstates(source.getRealEstates());
        }

        return position;
    }

}
