package ru.croc.ugd.ssr.solr.converter.egrn;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.FlatEgrnResponse;
import ru.croc.ugd.ssr.egrn.RightHolderOut;
import ru.croc.ugd.ssr.egrn.RightHoldersOut;
import ru.croc.ugd.ssr.egrn.RightRecordsAboutProperty;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrEgrnFlatRequest;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * EgrnFlatRequestDocumentConverter
 */
@Service
public class EgrnFlatRequestDocumentConverter
    extends SsrDocumentConverter<EgrnFlatRequestDocument, UgdSsrEgrnFlatRequest> {

    private final SolrEgrnFlatRequestMapper solrEgrnFlatRequestMapper;
    private final EgrnFlatRequestService egrnFlatRequestService;

    public EgrnFlatRequestDocumentConverter(
        final SolrEgrnFlatRequestMapper solrEgrnFlatRequestMapper,
        @Lazy final EgrnFlatRequestService egrnFlatRequestService
    ) {
        this.solrEgrnFlatRequestMapper = solrEgrnFlatRequestMapper;
        this.egrnFlatRequestService = egrnFlatRequestService;
    }

    @NotNull
    @Override
    public DocumentType<EgrnFlatRequestDocument> getDocumentType() {
        return SsrDocumentTypes.EGRN_FLAT_REQUEST;
    }

    @NotNull
    @Override
    public UgdSsrEgrnFlatRequest convertDocument(
        @NotNull final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        final UgdSsrEgrnFlatRequest ugdSsrEgrnFlatRequest = createDocument(
            getAnyAccessType(), egrnFlatRequestDocument.getId()
        );

        final EgrnFlatRequestData egrnFlatRequestData = of(egrnFlatRequestDocument.getDocument())
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .orElseThrow(() -> new SolrDocumentConversionException(egrnFlatRequestDocument.getId()));

        if (egrnFlatRequestData.getEgrnResponse() == null) {
            return null;
        }

        return solrEgrnFlatRequestMapper.toUgdSsrEgrnFlatRequest(
            ugdSsrEgrnFlatRequest,
            ofNullable(egrnFlatRequestData.getEgrnResponse())
                .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
                .orElse(null),
            egrnFlatRequestData.getRequestCriteria(),
            isFlatResettled(egrnFlatRequestData)
        );
    }

    private boolean isFlatResettled(final EgrnFlatRequestData egrnFlatRequest) {
        final ExtractAboutPropertyRoom extractAboutPropertyRoom = ofNullable(egrnFlatRequest.getEgrnResponse())
            .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
            .orElse(null);
        if (extractAboutPropertyRoom == null) {
            return false;
        } else if (egrnFlatRequest.getRequestCriteria().getRealEstateDocumentId() != null) {
            return isRealEstateFlatResettled(extractAboutPropertyRoom);
        } else if (egrnFlatRequest.getRequestCriteria().getCcoDocumentId() != null) {
            return isCcoFlatSettled(extractAboutPropertyRoom);
        } else {
            return true;
        }
    }

    private boolean isRealEstateFlatResettled(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return isFlatLiving(extractAboutPropertyRoom)
            && !hasIndividualRightHolder(extractAboutPropertyRoom.getRightRecords());
    }

    private boolean isCcoFlatSettled(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return !isFlatLiving(extractAboutPropertyRoom)
            || hasIndividualRightHolder(extractAboutPropertyRoom.getRightRecords());
    }

    private boolean isFlatLiving(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return ofNullable(extractAboutPropertyRoom)
            .map(ExtractAboutPropertyRoom::getRoomRecord)
            .map(egrnFlatRequestService::hasLivingPurpose)
            .orElse(false);
    }

    private boolean hasIndividualRightHolder(final RightRecordsAboutProperty rightRecordsAboutProperty) {
        return ofNullable(rightRecordsAboutProperty)
            .map(RightRecordsAboutProperty::getRightRecord)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(RightRecordsAboutProperty.RightRecord::getRightHolders)
            .filter(Objects::nonNull)
            .map(RightHoldersOut::getRightHolder)
            .flatMap(Collection::stream)
            .map(RightHolderOut::getIndividual)
            .anyMatch(Objects::nonNull);
    }
}
