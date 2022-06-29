package ru.croc.ugd.ssr.solr.converter.egrn;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequest;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequestData;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnBuildingRequest;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * EgrnBuildingRequestDocumentConverter
 */
@Service
@AllArgsConstructor
public class EgrnBuildingRequestDocumentConverter
    extends SsrDocumentConverter<EgrnBuildingRequestDocument, UgdSsrEgrnBuildingRequest> {

    private final SolrEgrnBuildingRequestMapper solrEgrnBuildingRequestMapper;

    @NotNull
    @Override
    public DocumentType<EgrnBuildingRequestDocument> getDocumentType() {
        return SsrDocumentTypes.EGRN_BUILDING_REQUEST;
    }

    @NotNull
    @Override
    public UgdSsrEgrnBuildingRequest convertDocument(@NotNull EgrnBuildingRequestDocument egrnBuildingRequestDocument) {
        final UgdSsrEgrnBuildingRequest ugdSsrEgrnBuildingRequest =
            createDocument(getAnyAccessType(), egrnBuildingRequestDocument.getId());

        final EgrnBuildingRequestData egrnBuildingRequestData = of(egrnBuildingRequestDocument.getDocument())
            .map(EgrnBuildingRequest::getEgrnBuildingRequestData)
            .orElseThrow(() -> new SolrDocumentConversionException(egrnBuildingRequestDocument.getId()));

        return solrEgrnBuildingRequestMapper.toUgdSsrEgrnBuildingRequest(
            ugdSsrEgrnBuildingRequest,
            egrnBuildingRequestData
        );
    }
}
