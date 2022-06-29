package ru.croc.ugd.ssr.service;

import ru.croc.ugd.ssr.model.ApartmentDefectDocument;

import java.util.List;

/**
 * Сервис по созданию дефектов квартиры.
 */
public interface ApartmentDefectService {
    /**
     * Создать список документов дефектов.
     *
     * @param apartmentDefectDocuments Список документов
     * @return Список документов c ID
     */
    List<ApartmentDefectDocument> createDocuments(List<ApartmentDefectDocument> apartmentDefectDocuments);

    /**
     * Executes defect type initialization.
     */
    void initDefectTypes();
}
