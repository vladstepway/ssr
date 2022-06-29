package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.computel.common.filenet.client.FilenetFileBean;
import ru.computel.common.filenet.client.FilenetFolderBean;
import ru.croc.ugd.ssr.builder.DictionaryUploadDataBuilder;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.db.projection.BuildingData;
import ru.croc.ugd.ssr.dictionaryupload.DictionaryUploadDataDto;
import ru.croc.ugd.ssr.dictionaryupload.DictionaryUploadDataType;
import ru.croc.ugd.ssr.dto.CcoDto;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис по работе с выгруженными данными.
 */
@Slf4j
@Service
public class DictionaryUploadDataService {

    private static final String SUBSYSTEM_CODE = "UGD_SSR";
    private static final String ADDRESS_FROM_DICTIONARY_FOLDER = "addressFrom";
    private static final String ADDRESS_TO_DICTIONARY_FOLDER = "addressTo";
    private static final String PEOPLE_DICTIONARY_FOLDER = "people";
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy hh:mm:ss";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    private final String rootPath;
    private final DictionaryUploadDataBuilder dictionaryUploadDataBuilder;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final FilestoreV2RemoteService remoteService;
    private final FilestoreRemoteService filestoreRemoteService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final SystemProperties systemProperties;

    /**
     * Конструктор класса DictionaryUploadDataService.
     *
     * @param rootPath rootPath
     * @param dictionaryUploadDataBuilder dictionaryUploadDataBuilder
     * @param capitalConstructionObjectService capitalConstructionObjectService
     * @param remoteService remoteService
     * @param filestoreRemoteService filestoreRemoteService
     * @param realEstateDocumentService realEstateDocumentService
     * @param systemProperties systemProperties
     */
    public DictionaryUploadDataService(
        @Value("${app.filestore.ssr.rootPath}") final String rootPath,
        final DictionaryUploadDataBuilder dictionaryUploadDataBuilder,
        final CapitalConstructionObjectService capitalConstructionObjectService,
        final FilestoreV2RemoteService remoteService,
        final FilestoreRemoteService filestoreRemoteService,
        final RealEstateDocumentService realEstateDocumentService,
        final SystemProperties systemProperties
    ) {
        this.rootPath = rootPath;
        this.dictionaryUploadDataBuilder = dictionaryUploadDataBuilder;
        this.capitalConstructionObjectService = capitalConstructionObjectService;
        this.remoteService = remoteService;
        this.filestoreRemoteService = filestoreRemoteService;
        this.realEstateDocumentService = realEstateDocumentService;
        this.systemProperties = systemProperties;
    }

    /**
     * Получение справочников с выгруженными данными.
     *
     * @return список справочников с выгруженными данными.
     */
    public List<DictionaryUploadDataDto> getDictionaryUploadDataList() {
        return Stream
            .of(
                getAddressDictionary(ADDRESS_FROM_DICTIONARY_FOLDER, DictionaryUploadDataType.ADDRESS_FROM),
                getAddressDictionary(ADDRESS_TO_DICTIONARY_FOLDER, DictionaryUploadDataType.ADDRESS_TO),
                getAddressDictionary(PEOPLE_DICTIONARY_FOLDER, DictionaryUploadDataType.PEOPLE)
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Генерирует и сохраняет справочник заселяемых адресов.
     */
    public void generateAddressFromDictionary() {
        final List<BuildingData> buildingData = realEstateDocumentService.getBuildingDataList();

        final byte[] reportByteArray = dictionaryUploadDataBuilder
            .buildAddressDictionary(true, buildingData);

        if (reportByteArray == null) {
            log.warn("Справочник расселяемых адресов не был сгенерирован");
            return;
        }

        saveFile(reportByteArray, "address-from-", ADDRESS_FROM_DICTIONARY_FOLDER);

        log.info("Справочник расселяемых домов сгенерирован успешно");
    }

    /**
     * Генерирует и сохраняет справочник расселяемых адресов.
     */
    public void generateAddressToDictionary() {
        final List<CcoDto> cco = capitalConstructionObjectService.fetchCcoList();

        final byte[] reportByteArray = dictionaryUploadDataBuilder
            .buildAddressDictionary(false, cco);

        if (reportByteArray == null) {
            log.warn("Справочник заселяемых адресов не был сгенерирован");
            return;
        }

        saveFile(reportByteArray, "address-to-", ADDRESS_TO_DICTIONARY_FOLDER);

        log.info("Справочник заселяемых домов сгенерирован успешно");
    }

    /**
     * Генерирует и сохраняет справочник о жителях.
     */
    public void generatePeopleDictionary() {
        final byte[] reportByteArray = dictionaryUploadDataBuilder.buildPeopleDictionary();

        if (reportByteArray == null) {
            log.warn("Справочник жителей не был сгенерирован");
            return;
        }

        saveFile(reportByteArray, "people-", PEOPLE_DICTIONARY_FOLDER);

        log.info("Справочник жителей сгенерирован успешно");
    }

    private void saveFile(final byte[] report, final String filenamePrefix, final String folderName) {
        final String folderId = createFolder(folderName);

        final String fileName = filenamePrefix + DATE_FORMATTER.format(LocalDateTime.now()) + ".xlsx";

        final String fileId = filestoreRemoteService.createFile(
            fileName, null, report, folderId,
            "xlsx", null, null, "UGD");
        log.info("Сохранен справочник с выгруженными данными, ID файла: {}", fileId);
    }

    private DictionaryUploadDataDto getAddressDictionary(
        final String folderName,
        final DictionaryUploadDataType fileType
    ) {
        final String addressFromFolderId = createFolder(folderName);

        final FilenetFolderBean filenetFolderBean = filestoreRemoteService
            .getFolderContent(addressFromFolderId, false, "ugd");

        return ofNullable(filenetFolderBean.getFolderContentFiles())
            .map(List::stream)
            .orElse(Stream.empty())
            .max(Comparator.comparing(FilenetFileBean::getDateLastModified))
            .map(fileBean -> DictionaryUploadDataDto.builder()
                .fileLink(fileBean.getVersionSeriesGuid())
                .lastModifiedDate(fileBean.getDateLastModified())
                .type(fileType)
                .build())
            .orElse(null);
    }

    /**
     * Создает папку для сохранения файлов.
     *
     * @param dictionaryFolderName название папки
     *
     * @return folderId идентификатор папки
     */
    @Cacheable(value = CacheConfig.DICTIONARY_UPLOAD_DATA)
    public String createFolder(final String dictionaryFolderName) {
        final CreateFolderRequest request = new CreateFolderRequest();
        request.setPath(rootPath + "/dictionaryUploadData/" + dictionaryFolderName);
        request.setErrorIfAlreadyExists(false);
        request.setAttrs(FilestoreFolderAttrs.builder()
            .folderTypeID("-")
            .folderEntityID("-")
            .folderSourceReference(FilestoreSourceRef.SERVICE.name())
            .build());
        return remoteService.createFolder(request, systemProperties.getSystem(), SUBSYSTEM_CODE).getId();
    }

}
