package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.DisabledPersonDocumentDao;
import ru.croc.ugd.ssr.db.projection.DisabledPersonDetailsProjection;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DisabledPersonDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DisabledPersonDocumentService extends SsrAbstractDocumentService<DisabledPersonDocument> {

    private final DisabledPersonDocumentDao disabledPersonDao;

    @NotNull
    @Override
    public DocumentType<DisabledPersonDocument> getDocumentType() {
        return SsrDocumentTypes.DISABLED_PERSON;
    }

    public List<DisabledPersonDetailsProjection> fetchDisabledPersonDetailsByUnomAndFlatNumber(
        final String unom,
        final String flatNumber
    ) {
        return disabledPersonDao.fetchDisabledPersonDetailsByUnomAndFlatNumber(unom, flatNumber);
    }

    public List<DisabledPersonDocument> fetchByUnomAndFlatNumber(final String unom, final String flatNumber) {
        return disabledPersonDao.fetchByUnomAndFlatNumber(unom, flatNumber)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public Optional<DisabledPersonDocument> fetchByUniqueExcelRecordId(final String uniqueExcelRecordId) {
        final List<DocumentData> disabledPersons = disabledPersonDao.fetchAllByUniqueExcelRecordId(uniqueExcelRecordId);

        if (disabledPersons.size() > 1) {
            log.warn("More than one disabled person have been found: uniqueExcelRecordId = {}", uniqueExcelRecordId);
        }

        return disabledPersons.stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public Optional<DisabledPersonDocument> fetchByUnomAndFlatNumberAndFullNameAndBirthDate(
        final String unom, final String flatNumber, final String fullName, final LocalDate birthDate
    ) {
        final List<DocumentData> disabledPersons = disabledPersonDao.fetchAllByUnomAndFlatNumberAndFullNameAndBirthDate(
            unom, flatNumber, fullName, birthDate
        );
        if (disabledPersons.size() > 1) {
            log.warn(
                "More than one disabled person have been found: "
                    + "unom = {}, flatNumber = {}, fullName = {}, birthDate = {}",
                unom,
                flatNumber,
                fullName,
                birthDate
            );
            return Optional.empty();
        } else {
            return disabledPersons.stream()
                .map(this::parseDocumentData)
                .findFirst();
        }
    }

    public List<DisabledPersonDocument> fetchAllByPersonDocumentIds(final List<String> deletedPersonsDocumentIds) {
        return disabledPersonDao.fetchAllByPersonDocumentIds(deletedPersonsDocumentIds)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
