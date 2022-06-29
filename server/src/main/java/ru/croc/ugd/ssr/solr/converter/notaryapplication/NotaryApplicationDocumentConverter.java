package ru.croc.ugd.ssr.solr.converter.notaryapplication;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.croc.ugd.ssr.solr.UgdSsrNotaryApplication;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * NotaryApplicationDocumentConverter.
 */
@Service
@Slf4j
public class NotaryApplicationDocumentConverter
    extends SsrDocumentConverter<NotaryApplicationDocument, UgdSsrNotaryApplication> {

    private final NotaryDocumentService notaryDocumentService;
    private final SolrNotaryApplicationMapper solrNotaryApplicationMapper;

    /**
     * Creates CommissionInspectionDocumentConverter.
     * @param notaryDocumentService notaryDocumentService
     * @param solrNotaryApplicationMapper solrNotaryApplicationMapper
     */
    public NotaryApplicationDocumentConverter(
        @Lazy final NotaryDocumentService notaryDocumentService,
        final SolrNotaryApplicationMapper solrNotaryApplicationMapper
    ) {
        this.notaryDocumentService = notaryDocumentService;
        this.solrNotaryApplicationMapper = solrNotaryApplicationMapper;
    }

    @NotNull
    @Override
    public DocumentType<NotaryApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY_APPLICATION;
    }

    @NotNull
    @Override
    public UgdSsrNotaryApplication convertDocument(
        @NotNull final NotaryApplicationDocument notaryApplicationDocument
    ) {
        final UgdSsrNotaryApplication ugdSsrNotaryApplication
            = createDocument(getAnyAccessType(), notaryApplicationDocument.getId());

        final NotaryApplicationType notaryApplication =
            of(notaryApplicationDocument.getDocument())
                .map(NotaryApplication::getNotaryApplicationData)
                .orElseThrow(() -> new SolrDocumentConversionException(notaryApplicationDocument.getId()));

        final NotaryType notaryType = ofNullable(notaryApplication)
            .map(NotaryApplicationType::getNotaryId)
            .map(this::fetchNotaryDocument)
            .map(NotaryDocument::getDocument)
            .map(Notary::getNotaryData)
            .orElse(null);

        final String notaryFullName = ofNullable(notaryType)
            .map(NotaryType::getFullName)
            .orElse(null);

        final List<String> notaryEmployeeLogins = ofNullable(notaryType)
            .map(NotaryType::getEmployeeLogin)
            .map(Arrays::asList)
            .orElse(new ArrayList<>());

        return solrNotaryApplicationMapper.toUgdSsrNotaryApplication(
            ugdSsrNotaryApplication,
            notaryApplication,
            notaryFullName,
            notaryEmployeeLogins
        );
    }

    private NotaryDocument fetchNotaryDocument(final String notaryId) {
        try {
            return notaryDocumentService.fetchDocument(notaryId);
        } catch (Exception e) {
            log.warn("Unable to fetchNotaryDocument: {}", e.getMessage(), e);
            return null;
        }
    }
}
