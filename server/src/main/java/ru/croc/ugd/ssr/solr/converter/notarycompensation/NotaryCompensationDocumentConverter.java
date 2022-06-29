package ru.croc.ugd.ssr.solr.converter.notarycompensation;

import static java.util.Optional.of;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensation;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationApplicant;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.solr.UgdSsrNotaryCompensation;
import ru.croc.ugd.ssr.solr.converter.SolrDocumentConversionException;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.DateTimeUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NotaryCompensationDocumentConverter.
 */
@Service
@Slf4j
public class NotaryCompensationDocumentConverter
    extends SsrDocumentConverter<NotaryCompensationDocument, UgdSsrNotaryCompensation> {

    private final SolrNotaryCompensationMapper solrNotaryCompensationMapper;
    private final PersonDocumentService personDocumentService;

    public NotaryCompensationDocumentConverter(
        final SolrNotaryCompensationMapper solrNotaryCompensationMapper,
        @Lazy final PersonDocumentService personDocumentService
    ) {
        this.solrNotaryCompensationMapper = solrNotaryCompensationMapper;
        this.personDocumentService = personDocumentService;
    }

    @NotNull
    @Override
    public DocumentType<NotaryCompensationDocument> getDocumentType() {
        return SsrDocumentTypes.NOTARY_COMPENSATION;
    }

    @NotNull
    @Override
    public UgdSsrNotaryCompensation convertDocument(
        @NotNull final NotaryCompensationDocument notaryCompensationDocument
    ) {
        final UgdSsrNotaryCompensation ugdSsrNotaryCompensation
            = createDocument(getAnyAccessType(), notaryCompensationDocument.getId());

        final NotaryCompensationData notaryCompensation =
            of(notaryCompensationDocument.getDocument())
                .map(NotaryCompensation::getNotaryCompensationData)
                .orElseThrow(() -> new SolrDocumentConversionException(notaryCompensationDocument.getId()));

        final NotaryCompensationApplicant applicant = notaryCompensation.getApplicant();

        final String applicantFullName = PersonUtils.getFullName(
            applicant.getLastName(), applicant.getFirstName(), applicant.getMiddleName()
        );

        final PersonDocument personDocument = personDocumentService.fetchById(applicant.getPersonDocumentId())
            .orElse(null);

        final List<PersonDocument> otherFlatOwners = personDocumentService.getOtherFlatOwners(personDocument);

        final List<String> ownersFullNames = otherFlatOwners
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(this::extractFullNameAndBirthDate)
            .collect(Collectors.toList());

        return solrNotaryCompensationMapper.toUgdSsrNotaryCompensation(
            ugdSsrNotaryCompensation,
            notaryCompensation,
            applicantFullName,
            ownersFullNames
        );
    }

    private String extractFullNameAndBirthDate(final PersonType personType) {
        final String formattedDate = DateTimeUtils.getFormattedDate(personType.getBirthDate());
        return Stream.of(
                personType.getFIO(),
                StringUtils.isNotBlank(formattedDate) ? "(" + formattedDate + ")" : ""
            )
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }
}
