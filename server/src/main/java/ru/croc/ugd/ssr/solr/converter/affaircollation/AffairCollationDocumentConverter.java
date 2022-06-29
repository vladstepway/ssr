package ru.croc.ugd.ssr.solr.converter.affaircollation;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.affaircollation.AffairCollation;
import ru.croc.ugd.ssr.affaircollation.AffairCollationData;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrAffairCollation;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * AffairCollationDocumentConverter.
 */
@Service
@RequiredArgsConstructor
public class AffairCollationDocumentConverter
    extends SsrDocumentConverter<AffairCollationDocument, UgdSsrAffairCollation> {

    private final SolrAffairCollationMapper solrAffairCollationMapper;

    @NotNull
    @Override
    public DocumentType<AffairCollationDocument> getDocumentType() {
        return SsrDocumentTypes.AFFAIR_COLLATION;
    }

    @NotNull
    @Override
    public UgdSsrAffairCollation convertDocument(@NotNull final AffairCollationDocument affairCollationDocument) {
        final UgdSsrAffairCollation affairCollation =
            createDocument(getAnyAccessType(), affairCollationDocument.getId());

        final AffairCollationData affairCollationData = of(affairCollationDocument.getDocument())
            .map(AffairCollation::getAffairCollationData)
            .orElseThrow(() -> new SolrDocumentConversionException(affairCollationDocument.getId()));

        return solrAffairCollationMapper.toUgdSsrAffairCollation(affairCollation, affairCollationData);
    }
}
