package ru.croc.ugd.ssr.solr.converter.ipev;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ipev.IpevLog;
import ru.croc.ugd.ssr.ipev.IpevLogData;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrIpevLog;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * IpevLogDocumentConverter.
 */
@Service
@AllArgsConstructor
public class IpevLogDocumentConverter extends SsrDocumentConverter<IpevLogDocument, UgdSsrIpevLog> {

    private final SolrIpevLogMapper solrIpevLogMapper;

    @NotNull
    @Override
    public DocumentType<IpevLogDocument> getDocumentType() {
        return SsrDocumentTypes.IPEV_LOG;
    }

    @NotNull
    @Override
    public UgdSsrIpevLog convertDocument(@NotNull final IpevLogDocument ipevLogDocument) {
        final UgdSsrIpevLog ugdSsrIpevLog = createDocument(getAnyAccessType(), ipevLogDocument.getId());

        final IpevLogData ipevLogData = of(ipevLogDocument.getDocument())
            .map(IpevLog::getIpevLogData)
            .orElseThrow(() -> new SolrDocumentConversionException(ipevLogDocument.getId()));

        return solrIpevLogMapper.toUgdSsrIpevLog(ugdSsrIpevLog, ipevLogData);
    }

}
