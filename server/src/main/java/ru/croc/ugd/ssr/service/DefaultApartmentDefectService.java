package ru.croc.ugd.ssr.service;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.ApartmentDefect;
import ru.croc.ugd.ssr.model.ApartmentDefectDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Сервис дефектов квартиры.
 */
@Service
public class DefaultApartmentDefectService extends AbstractDocumentService<ApartmentDefectDocument>
    implements ApartmentDefectService {

    @Nonnull
    @Override
    public DocumentType<ApartmentDefectDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_DEFECT;
    }

    @Override
    public List<ApartmentDefectDocument> createDocuments(final List<ApartmentDefectDocument> apartmentDefectDocuments) {
        if (apartmentDefectDocuments == null || apartmentDefectDocuments.size() == 0) {
            return Collections.emptyList();
        }
        final List<ApartmentDefectDocument> createdDocuments =
            new ArrayList<>(apartmentDefectDocuments.size());
        apartmentDefectDocuments.forEach(apartmentDefectDocument -> {
            final ApartmentDefectDocument createdDocument =
                this.createDocument(apartmentDefectDocument, true, null);
            createdDocuments.add(createdDocument);
        });
        return createdDocuments;
    }

    @Override
    public void initDefectTypes() {
        initDefectType("Кладовка");
        initDefectType("Общие замечания по квартире");
    }

    private void initDefectType(final String flatElement) {
        final List<ApartmentDefectDocument> apartmentDefects =
            findDocuments("{\"ApartmentDefect\": {\"ApartmentDefectData\": {\"flatElement\" : \"Комната 1\"}}}");

        apartmentDefects.stream()
            .map(ApartmentDefectDocument::getDocument)
            .map(ApartmentDefect::getApartmentDefectData)
            .forEach(apartmentDefectType -> apartmentDefectType.setFlatElement(flatElement));

        createDocuments(apartmentDefects);
    }
}
