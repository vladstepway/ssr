package ru.croc.ugd.ssr.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.service.DictionaryUploadDataService;


/**
 * DictionaryUploadDataScheduler.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DictionaryUploadDataScheduler {

    private final DictionaryUploadDataService dictionaryUploadDataService;

    /**
     * Вызывает метод создания и сохранения справочника расселяемых домов.
     *
     */
    @Scheduled(cron = "${schedulers.dictionary-upload-data.cron:0 0 2 * * ?}")
    public void generateDictionaryUploadData() {
        dictionaryUploadDataService.generateAddressFromDictionary();
        dictionaryUploadDataService.generateAddressToDictionary();
        dictionaryUploadDataService.generatePeopleDictionary();
    }

}
