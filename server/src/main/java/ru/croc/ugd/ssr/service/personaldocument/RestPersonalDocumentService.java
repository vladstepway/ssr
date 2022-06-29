package ru.croc.ugd.ssr.service.personaldocument;

import ru.croc.ugd.ssr.dto.personaldocument.RestAddApplicationDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestMergeFilesDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestParsedDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonDataDocumentsDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentDto;

import java.util.List;

/**
 * Сервис для работы со сведениями о документах.
 */
public interface RestPersonalDocumentService {

    /**
     * Получить сведения о документах.
     *
     * @param id ИД документа сведения о документах
     * @return сведения о документах
     */
    RestPersonalDocumentDto fetchById(final String id);

    /**
     * Получить документы по жителю.
     *
     * @param personDocumentId ИД документа жителя
     * @return документы
     */
    RestPersonDataDocumentsDto fetchAll(final String personDocumentId);

    /**
     * Создать сведения о документах.
     *
     * @param body тело запроса
     * @return сведения о документах
     */
    RestPersonalDocumentDto create(final RestCreatePersonalDocumentDto body);

    /**
     * Разобрать документы по типам.
     *
     * @param id ИД документа сведений о документах
     * @param restParsedDocumentDtos документы, выделенные из единого файла документов.
     */
    void parse(final String id, final List<RestParsedDocumentDto> restParsedDocumentDtos);

    /**
     * Объединить файлы документов в единый файл.
     *
     * @param body тело запроса
     * @return единый файл
     */
    byte[] merge(final RestMergeFilesDto body);

    /**
     * Добавить в сведения о документах документ заявления.
     *
     * @param body тело запроса
     * @return сведения о документах
     */
    RestPersonalDocumentDto addApplicationDocument(final RestAddApplicationDocumentDto body);

    /**
     * Получить последние сведения о документах.
     *
     * @param affairId ИД семьи
     * @param isParsed  возвращать только уже разобранные сведения о документах
     * @return сведения о документах
     */
    RestPersonalDocumentDto getLastPersonalDocument(final String affairId, final boolean isParsed);
}
