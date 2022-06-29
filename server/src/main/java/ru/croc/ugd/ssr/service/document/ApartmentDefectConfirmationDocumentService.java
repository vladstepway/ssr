package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ApartmentDefectConfirmationDao;
import ru.croc.ugd.ssr.model.ApartmentDefectConfirmationDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApartmentDefectConfirmationDocumentService
    extends DocumentWithFolder<ApartmentDefectConfirmationDocument> {

    private final ApartmentDefectConfirmationDao apartmentDefectConfirmationDao;

    @NotNull
    @Override
    public DocumentType<ApartmentDefectConfirmationDocument> getDocumentType() {
        return SsrDocumentTypes.APARTMENT_DEFECT_CONFIRMATION;
    }

    public List<ApartmentDefectConfirmationDocument> fetchByUnom(String unom) {
        return apartmentDefectConfirmationDao.fetchByUnom(unom)
            .stream()
            .filter(Objects::nonNull)
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
