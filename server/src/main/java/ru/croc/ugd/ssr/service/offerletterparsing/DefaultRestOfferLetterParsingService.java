package ru.croc.ugd.ssr.service.offerletterparsing;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.offerletterparsing.RestOfferLetterParsingDto;
import ru.croc.ugd.ssr.mapper.OfferLetterParsingMapper;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsingType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.OfferLetterParsingDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultRestOfferLetterParsingService implements RestOfferLetterParsingService {

    private final OfferLetterParsingDocumentService offerLetterParsingDocumentService;
    private final PersonDocumentService personDocumentService;
    private final OfferLetterParsingMapper offerLetterParsingMapper;

    @Override
    public RestOfferLetterParsingDto fetchById(final String id) {
        final OfferLetterParsingDocument offerLetterParsingDocument =
            offerLetterParsingDocumentService.fetchDocument(id);
        final OfferLetterParsingType offerLetterParsingData = offerLetterParsingDocument.getDocument()
            .getOfferLetterParsingData();

        final PersonType.OfferLetters.OfferLetter offerLetter = retrieveOfferLetter(offerLetterParsingData);
        return offerLetterParsingMapper.toRestOfferLetterParsingDto(offerLetterParsingDocument, offerLetter);
    }

    private PersonType.OfferLetters.OfferLetter retrieveOfferLetter(
        final OfferLetterParsingType offerLetterParsingData
    ) {
        final String letterId = ofNullable(offerLetterParsingData)
            .map(OfferLetterParsingType::getLetterId)
            .orElse(null);

        return ofNullable(offerLetterParsingData)
            .map(OfferLetterParsingType::getAffairId)
            .map(personDocumentService::fetchByAffairId)
            .orElse(Collections.emptyList())
            .stream()
            .map(personDocument -> PersonUtils.getOfferLetter(personDocument, letterId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElse(null);
    }
}
