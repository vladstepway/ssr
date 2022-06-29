package ru.croc.ugd.ssr.service.disabledperson;

import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDetailsDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDto;
import ru.croc.ugd.ssr.model.PersonDocument;

import java.util.List;

/**
 * Сервис для работы со сведениями о маломобильности.
 */
public interface DisabledPersonService {

    /**
     * Получает сведения о маломобильности жителя.
     * @param unom unom
     * @param flatNumber номер квартиры
     * @param personDocumentId ИД документа жителя
     * @return сведения о маломобильности жителя
     */
    RestDisabledPersonDetailsDto fetchDisabledPersonDetailsByUnomAndFlatNumber(
        final String unom, final String flatNumber, final String personDocumentId
    );

    /**
     * Сохраняет сведения о маломобильности жителя.
     * @param disabledPersonDtos сведения о маломобильности жителей
     * @param disabledPersonImportDocumentId ИД документа загрузки сведений по маломобильным гражданам
     */
    void saveDisabledPersons(
        final List<RestDisabledPersonDto> disabledPersonDtos, final String disabledPersonImportDocumentId
    );

    /**
     * Удалить связь сведений о маломобильности с удаленными жителями.
     * @param deletedPersonsDocumentIds ИД документов удаленных жителей
     */
    void unbindDisabledPersonsWithDeletedPersons(final List<String> deletedPersonsDocumentIds);

    /**
     * Связать сведения о маломобильности с жителем.
     * @param personDocument Документ жителя
     */
    void bindDisabledPersonWithPerson(final PersonDocument personDocument);
}
