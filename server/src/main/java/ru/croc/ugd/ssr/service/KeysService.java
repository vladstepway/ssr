package ru.croc.ugd.ssr.service;

import java.util.List;

/**
 * Сервис по сдаче/выдаче ключей.
 */
public interface KeysService {

    /**
     * Обновление данных по сдаче/выдаче ключей.
     *
     * @param unoms UNOM-ы домов
     */
    void processUpdateKeysByUnoms(List<String> unoms);

    /**
     * Обновление статуса переселения по информации о сдаче/выдаче ключей.
     */
    void processUpdateRelocationStatus();
}
