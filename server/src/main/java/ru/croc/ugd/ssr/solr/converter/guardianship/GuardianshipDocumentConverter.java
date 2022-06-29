package ru.croc.ugd.ssr.solr.converter.guardianship;

import static java.util.Optional.of;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequest;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.solr.UgdSsrGuardianship;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

/**
 * GuardianshipDocumentConverter.
 */
@Service
@Slf4j
public class GuardianshipDocumentConverter extends SsrDocumentConverter<GuardianshipRequestDocument, UgdSsrGuardianship> {

    private final SolrGuardianshipMapper solrGuardianshipMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;

    public GuardianshipDocumentConverter(
        SolrGuardianshipMapper solrGuardianshipMapper,
        @Lazy PersonDocumentService personDocumentService,
        @Lazy FlatService flatService
    ) {
        this.solrGuardianshipMapper = solrGuardianshipMapper;
        this.personDocumentService = personDocumentService;
        this.flatService = flatService;
    }

    @NotNull
    @Override
    public DocumentType<GuardianshipRequestDocument> getDocumentType() {
        return SsrDocumentTypes.GUARDIANSHIP_REQUEST;
    }

    @NotNull
    @Override
    public UgdSsrGuardianship convertDocument(@NotNull final GuardianshipRequestDocument guardianshipRequestDocument) {
        final UgdSsrGuardianship ugdSsrGuardianship =
            createDocument(getAnyAccessType(), guardianshipRequestDocument.getId());

        final GuardianshipRequestData guardianshipRequest =
            of(guardianshipRequestDocument.getDocument())
                .map(GuardianshipRequest::getGuardianshipRequestData)
                .orElseThrow(() -> new SolrDocumentConversionException(guardianshipRequestDocument.getId()));

        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto =
            retrieveRealEstateDataAndFlatInfoDto(guardianshipRequest.getRequesterPersonId());

        return solrGuardianshipMapper
            .toUgdSsrGuardianship(ugdSsrGuardianship, guardianshipRequest, realEstateDataAndFlatInfoDto);
    }

    private RealEstateDataAndFlatInfoDto retrieveRealEstateDataAndFlatInfoDto(final String personDocumentId) {
        return personDocumentService.fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(this::fetchRealEstateAndFlat)
            .orElse(null);
    }

    private RealEstateDataAndFlatInfoDto fetchRealEstateAndFlat(final String flatId) {
        try {
            return flatService.fetchRealEstateAndFlat(flatId);
        } catch (Exception e) {
            log.warn("Unable to fetchRealEstateAndFlat: {}", e.getMessage(), e);
            return null;
        }
    }
}
