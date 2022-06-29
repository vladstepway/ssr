package ru.croc.ugd.ssr.service.disabledperson;

import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonImportDto;

public interface RestDisabledPersonImportService {

    /**
     * Создать пустой документ загрузки сведений по маломобильным гражданам.
     *
     * @return данные о загрузке сведений по маломобильным гражданам
     */
    RestDisabledPersonImportDto create();

    /**
     * Выполнить разбор файла.
     *
     * @param id ИД документа загрузки сведений по маломобильным гражданам
     * @param fileStoreId ИД в FileStore файла со сведениями по маломобильным гражданам
     */
    void parseFile(final String id, final String fileStoreId);

    /**
     * Получить документ загрузки сведений по маломобильным гражданам.
     *
     * @param id ИД документа загрузки сведений по маломобильным гражданам
     * @return данные о загрузке сведений по маломобильным гражданам
     */
    RestDisabledPersonImportDto fetchById(final String id);
}
