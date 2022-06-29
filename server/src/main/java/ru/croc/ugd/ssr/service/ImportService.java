package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.xlsx.StreamingReader;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.NsiType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.controller.ImportController;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.imports.FlatAddInfo;
import ru.croc.ugd.ssr.model.imports.RealEstateAddInfo;
import ru.croc.ugd.ssr.model.imports.RealEstateEntranceAddInfo;
import ru.croc.ugd.ssr.model.imports.WearAddInfo;
import ru.croc.ugd.ssr.model.imports.npc.Root;
import ru.reinform.cdp.utils.core.RIXmlUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для импорта данных в приложение.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImportService {

    // размер страницы для чтения документов
    private static final int PAGE_SIZE = 100;
    private final FlatService flatService;
    private final JsonMapper jsonMapper;
    private final RealEstateDocumentService realEstateDocumentService;

    /**
     * Метод для обогащения первоначального импорта кадастровыми номерами ОН.
     *
     * @param stringPathCadastralNums - путь до csv файла с кадастровыми номерами ОН
     */
    public void importRealEstateCadastralNums(String stringPathCadastralNums) {
        try (
            CSVReader cadastralNumsReader = new CSVReader(
                new InputStreamReader(new FileInputStream(stringPathCadastralNums), "windows-1251")
            )
        ) {
            // загрузим из БД текущие документы REAL-ESTATE
            log.info("ImportService: начата обработка кадастровых номеров ОН");
            List<RealEstateDocument> realEstateDocumentList = new ArrayList<>();
            int iterator = 0;
            List<RealEstateDocument> documentPage = realEstateDocumentService.fetchDocumentsPage(iterator, PAGE_SIZE);
            while (documentPage.size() > 0) {
                iterator += 1;
                realEstateDocumentList.addAll(documentPage);
                documentPage = realEstateDocumentService.fetchDocumentsPage(iterator, PAGE_SIZE);
            }

            String[] nextLineArray;
            // заполним кадастровые номера ОН
            cadastralNumsReader.readNext(); //skip title rows
            cadastralNumsReader.readNext();
            while ((nextLineArray = cadastralNumsReader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String globalObjectId = removeQuotes(values[3]);
                Optional<RealEstateDocument> optionalRealEstateDocument = realEstateDocumentList
                    .stream()
                    .filter(
                        document -> document.getDocument().getRealEstateData().getGlobalID() != null
                            && globalObjectId.equals(
                            document.getDocument().getRealEstateData().getGlobalID().toString()
                        )
                    )
                    .findFirst();
                if (optionalRealEstateDocument.isPresent()) {
                    // нашли ОН с указанным globalId
                    RealEstateDocument realEstateDocument = optionalRealEstateDocument.get();
                    RealEstate realEstate = realEstateDocument.getDocument();
                    RealEstateDataType realEstateData = realEstate.getRealEstateData();

                    RealEstateDataType.CadastralNums cadastralNumsObject
                        = realEstateData.getCadastralNums() == null
                        ? new RealEstateDataType.CadastralNums()
                        : realEstateData.getCadastralNums();
                    List<RealEstateDataType.CadastralNums.CadastralNum> cadastralNums
                        = cadastralNumsObject.getCadastralNum();

                    RealEstateDataType.CadastralNums.CadastralNum cadastralNum
                        = new RealEstateDataType.CadastralNums.CadastralNum();

                    cadastralNum.setGlobalID(new BigInteger(values[0]));
                    cadastralNum.setValue(removeQuotes(values[5]));

                    cadastralNums.add(cadastralNum);
                    realEstateData.setCadastralNums(cadastralNumsObject);
                }

            }
            log.info("ImportService: закончена обработка кадастровых номеров ОН");
            log.info("ImportService: начат импорт кадастровых номеров ОН");

            // отправим все на сервер
            realEstateDocumentList.forEach(
                document -> realEstateDocumentService.updateDocument(
                    document.getId(),
                    document,
                    true,
                    true,
                    "Add cadastral nums"
                )
            );
            log.info("ImportService: закончен импорт кадастровых номеров ОН");
        } catch (CsvValidationException | IOException ex) {
            log.error(ImportController.class.getName() + ": exception while reading file");
            log.error(ex.getMessage());
        }
    }

    /**
     * Метод для обогащения первоначального импорта кадастровыми номерами ЗУ.
     *
     * @param stringPathCadastralNumsZu - путь до csv файла с кадастровыми номерами ЗУ
     */
    public void importRealEstateCadastralNumsZu(String stringPathCadastralNumsZu) {
        try (
            CSVReader cadastralNumsZUReader = new CSVReader(
                new InputStreamReader(new FileInputStream(stringPathCadastralNumsZu), "windows-1251")
            )
        ) {
            // загрузим из БД текущие документы REAL-ESTATE
            log.info("ImportService: начата обработка кадастровых номеров ЗУ");
            List<RealEstateDocument> realEstateDocumentList = new ArrayList<>();
            int iterator = 0;
            List<RealEstateDocument> documentPage = realEstateDocumentService.fetchDocumentsPage(iterator, PAGE_SIZE);
            while (documentPage.size() > 0) {
                iterator += 1;
                realEstateDocumentList.addAll(documentPage);
                documentPage = realEstateDocumentService.fetchDocumentsPage(iterator, PAGE_SIZE);
            }

            String[] nextLineArray;
            // заполним кадастровые номера ЗУ
            cadastralNumsZUReader.readNext(); //skip title rows
            cadastralNumsZUReader.readNext();
            while ((nextLineArray = cadastralNumsZUReader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String globalObjectId = removeQuotes(values[3]);
                Optional<RealEstateDocument> optionalRealEstateDocument = realEstateDocumentList
                    .stream()
                    .filter(
                        document -> document.getDocument().getRealEstateData().getGlobalID() != null
                            && globalObjectId.equals(
                            document.getDocument().getRealEstateData().getGlobalID().toString()
                        )
                    )
                    .findFirst();
                if (optionalRealEstateDocument.isPresent()) {
                    // нашли ОН с указанным globalId
                    RealEstateDocument realEstateDocument = optionalRealEstateDocument.get();
                    RealEstate realEstate = realEstateDocument.getDocument();
                    RealEstateDataType realEstateData = realEstate.getRealEstateData();

                    RealEstateDataType.CadastralNumsZU cadastralNumsObject
                        = realEstateData.getCadastralNumsZU() == null
                        ? new RealEstateDataType.CadastralNumsZU()
                        : realEstateData.getCadastralNumsZU();
                    List<RealEstateDataType.CadastralNumsZU.CadastralZU> cadastralNums
                        = cadastralNumsObject.getCadastralZU();

                    RealEstateDataType.CadastralNumsZU.CadastralZU cadastralNum
                        = new RealEstateDataType.CadastralNumsZU.CadastralZU();
                    cadastralNum.setGlobalID(new BigInteger(values[0]));
                    cadastralNum.setValue(removeQuotes(values[5]));

                    cadastralNums.add(cadastralNum);
                    realEstateData.setCadastralNumsZU(cadastralNumsObject);
                }
            }

            log.info("ImportService: закончена обработка кадастровых номеров ЗУ");
            log.info("ImportService: начат импорт кадастровых номеров ЗУ");

            // отправим все на сервер
            realEstateDocumentList.forEach(
                document -> realEstateDocumentService.updateDocument(
                    document.getId(),
                    document,
                    true,
                    true,
                    "Add cadastral nums ZU"
                )
            );

            log.info("ImportService: закончен импорт кадастровых номеров ЗУ");
        } catch (CsvValidationException | IOException ex) {
            log.error(ImportController.class.getName() + ": exception while reading file");
            log.error(ex.getMessage());
        }
    }

    /**
     * Метод для импорта данных по ОН и квартирам из xlsx и csv файлов соответсвенно.
     *
     * @param stringPathRealEstates - путь к файлу с ОН (xlsx)
     * @param stringPathFlats       - путь к файлу с квартирами, объектами и коммуналками, и даже ЗУ (csv)
     */
    public void importRealEstatesWithFlats(String stringPathRealEstates, String stringPathFlats) {
        try (
            InputStream realEstatesIS = new FileInputStream(new File(stringPathRealEstates));
            Workbook realEstatesWorkBook = StreamingReader.builder()
                .rowCacheSize(100)      // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)       // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(realEstatesIS);   // InputStream or File for XLSX file (required)
            CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(stringPathFlats), "windows-1251")
            )
        ) {
            List<RealEstateDocument> realEstateDocumentList = new ArrayList<>();

            realEstateDocumentService.deleteAll(); // удаляет все документы типа REAL-ESTATE - ОСТОРОЖНО!
            log.info("ImportService: начало загрузки и обработки файла с ОН");

            for (Sheet sheet : realEstatesWorkBook) {
                for (Row r : sheet) {
                    if (r.getRowNum() == 0) {
                        continue;  // skip row with titles
                    }

                    RealEstateDataType data = new RealEstateDataType();

                    data.setAddress(r.getCell(1).getStringCellValue());
                    if (!r.getCell(2).getStringCellValue().equals("-")) {
                        data.setUNOM(new BigInteger(r.getCell(2).getStringCellValue()));
                    }
                    NsiType district = new NsiType();
                    district.setName(r.getCell(3).getStringCellValue());
                    data.setDISTRICT(district);

                    RealEstate realEstate = new RealEstate();
                    realEstate.setRealEstateData(data);

                    RealEstateDocument document = new RealEstateDocument();
                    document.setDocument(realEstate);
                    realEstateDocumentList.add(document);
                }
            }

            log.info("ImportService: файл с ОН обработан");
            log.info("ImportService: начата обработка файла-выгрузки из ОДОПМ");

            String[] nextLineArray;
            reader.readNext(); //skip title rows
            reader.readNext();
            while ((nextLineArray = reader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String unom = removeQuotes(values[6]);


                if ("0".equals(unom)) {
                    // unom пустой, значит, это квартира
                    final String address = removeQuotes(values[5]);
                    Optional<RealEstateDocument> optionalRealEstateDocument = realEstateDocumentList
                        .stream()
                        .filter(
                            document -> address.contains(
                                document.getDocument().getRealEstateData().getAddress() + ", квартира"
                            )
                        )
                        .findFirst();

                    if (optionalRealEstateDocument.isPresent()) {
                        log.debug("ImportService: квартира нашлась - " + address);
                        // квартира нашлась по адресу, добавим

                        // проверим, не является ли она коммуналкой
                        if (address.contains("комната")) {
                            final String croppedAddress = address.substring(0, address.indexOf(", комната"));
                            RealEstateDocument realEstateDocument = optionalRealEstateDocument.get();
                            RealEstate realEstate = realEstateDocument.getDocument();
                            RealEstateDataType realEstateData = realEstate.getRealEstateData();

                            RealEstateDataType.Flats flatsObject
                                = realEstateData.getFlats() == null
                                ? new RealEstateDataType.Flats()
                                : realEstateData.getFlats();
                            realEstateData.setFlats(flatsObject);

                            Optional<FlatType> flatOptional = flatsObject.getFlat()
                                .stream().filter(f -> f.getAddress().equals(croppedAddress)).findAny();

                            FlatType flat;
                            if (flatOptional.isPresent()) {
                                flat = flatOptional.get();
                            } else {
                                flat = new FlatType();

                                flat.setGlobalID(new BigInteger(values[0]));
                                flat.setFlatID(values[0]);

                                flat.setAID(removeQuotes(values[3]));
                                flat.setAddress(croppedAddress);
                                NsiType l4type = new NsiType();
                                l4type.setName(removeQuotes(values[21]));
                                flat.setApartmentL4TYPE(l4type);
                                flat.setApartmentL4VALUE(removeQuotes(values[22]));

                                flat.setNREG(removeQuotes(values[27]));
                                flat.setNFIAS(removeQuotes(values[29]));

                                NsiType sostad = new NsiType();
                                sostad.setName(removeQuotes(values[37]));
                                flat.setSOSTAD(sostad);

                                NsiType status = new NsiType();
                                status.setName(removeQuotes(values[38]));
                                flat.setSTATUS(status);

                                List<FlatType> flats = flatsObject.getFlat();
                                flats.add(flat);
                            }

                            RoomType room = new RoomType();

                            NsiType l5type = new NsiType();
                            l5type.setName(removeQuotes(values[23]));
                            room.setRoomID(values[0]);
                            room.setRoomL5TYPE(l5type);
                            room.setRoomL5VALUE(removeQuotes(values[24]));

                            FlatType.Rooms roomsObject
                                = flat.getRooms() == null
                                ? new FlatType.Rooms()
                                : flat.getRooms();

                            List<RoomType> rooms = roomsObject.getRoom();
                            rooms.add(room);
                            flat.setRooms(roomsObject);
                        } else {
                            FlatType flat = new FlatType();

                            flat.setGlobalID(new BigInteger(values[0]));
                            flat.setFlatID(values[0]);

                            flat.setAID(removeQuotes(values[3]));
                            flat.setAddress(removeQuotes(values[5]));
                            NsiType l4type = new NsiType();
                            l4type.setName(removeQuotes(values[21]));
                            flat.setApartmentL4TYPE(l4type);
                            flat.setApartmentL4VALUE(removeQuotes(values[22]));

                            flat.setNREG(removeQuotes(values[27]));
                            flat.setNFIAS(removeQuotes(values[29]));

                            NsiType sostad = new NsiType();
                            sostad.setName(removeQuotes(values[37]));
                            flat.setSOSTAD(sostad);

                            NsiType status = new NsiType();
                            status.setName(removeQuotes(values[38]));
                            flat.setSTATUS(status);

                            RealEstateDocument realEstateDocument = optionalRealEstateDocument.get();
                            RealEstate realEstate = realEstateDocument.getDocument();
                            RealEstateDataType realEstateData = realEstate.getRealEstateData();
                            RealEstateDataType.Flats flatsObject
                                = realEstateData.getFlats() == null
                                ? new RealEstateDataType.Flats()
                                : realEstateData.getFlats();

                            List<FlatType> flats = flatsObject.getFlat();
                            flats.add(flat);
                            realEstateData.setFlats(flatsObject);
                        }
                    }
                } else {
                    // unom есть, значит это ОН

                    Optional<RealEstateDocument> optionalRealEstateDocument = realEstateDocumentList
                        .stream()
                        .filter(
                            document -> document.getDocument().getRealEstateData().getUNOM() != null
                                && unom.equals(document.getDocument().getRealEstateData().getUNOM().toString())
                        )
                        .findFirst();

                    if (optionalRealEstateDocument.isPresent()) {
                        // ОС нашелся по UNOM-у, обогатим данные
                        // квартира нашлась по адресу, добавим
                        RealEstateDocument realEstateDocument = optionalRealEstateDocument.get();
                        RealEstate realEstate = realEstateDocument.getDocument();
                        RealEstateDataType realEstateData = realEstate.getRealEstateData();

                        log.debug("ImportService: ОН нашелся - " + realEstateData.getAddress());

                        realEstateData.setGlobalID(new BigInteger(values[0]));

                        NsiType objectStatus = new NsiType();
                        objectStatus.setName(removeQuotes(values[2]));
                        realEstateData.setSTATUS(objectStatus);

                        NsiType objectType = new NsiType();
                        objectType.setName(removeQuotes(values[4]));
                        realEstateData.setOBJTYPE(objectType);

                        NsiType subjectRfr1 = new NsiType();
                        subjectRfr1.setName(removeQuotes(values[7]));
                        realEstateData.setSubjectRFP1(subjectRfr1);

                        NsiType settlementP3 = new NsiType();
                        settlementP3.setName(removeQuotes(values[8]));
                        realEstateData.setSettlementP3(settlementP3);

                        NsiType townP4 = new NsiType();
                        townP4.setName(removeQuotes(values[9]));
                        realEstateData.setTownP4(townP4);

                        NsiType munOkrugP5 = new NsiType();
                        munOkrugP5.setName(removeQuotes(values[10]));
                        realEstateData.setMunOkrugP5(munOkrugP5);

                        NsiType localityP6 = new NsiType();
                        localityP6.setName(removeQuotes(values[11]));
                        realEstateData.setLocalityP6(localityP6);

                        NsiType elementP7 = new NsiType();
                        elementP7.setName(removeQuotes(values[12]));
                        realEstateData.setElementP7(elementP7);

                        NsiType additionalAddrElementP90 = new NsiType();
                        additionalAddrElementP90.setName(removeQuotes(values[13]));
                        realEstateData.setAdditionalAddrElementP90(additionalAddrElementP90);

                        NsiType refinementAddrP91 = new NsiType();
                        refinementAddrP91.setName(removeQuotes(values[14]));
                        realEstateData.setRefinementAddrP91(refinementAddrP91);

                        NsiType houseL1Type = new NsiType();
                        houseL1Type.setName(removeQuotes(values[15]));
                        realEstateData.setHouseL1TYPE(houseL1Type);
                        realEstateData.setHouseL1VALUE(removeQuotes(values[16]));

                        NsiType corpL2Type = new NsiType();
                        corpL2Type.setName(removeQuotes(values[17]));
                        realEstateData.setCorpL2TYPE(corpL2Type);
                        realEstateData.setCorpL2VALUE(removeQuotes(values[18]));

                        NsiType buildingL3Type = new NsiType();
                        buildingL3Type.setName(removeQuotes(values[19]));
                        realEstateData.setBuildingL3TYPE(buildingL3Type);
                        realEstateData.setBuildingL3VALUE(removeQuotes(values[20]));

                        NsiType admArea = new NsiType();
                        admArea.setName(removeQuotes(values[25]));
                        realEstateData.setADMAREA(admArea);

                        NsiType adrType = new NsiType();
                        adrType.setName(removeQuotes(values[35]));
                        realEstateData.setADRTYPE(adrType);

                        NsiType vid = new NsiType();
                        vid.setName(removeQuotes(values[36]));
                        realEstateData.setVID(vid);
                    }
                }
            }

            log.info("ImportService: закончена обработка файла-выгрузки из ОДОПМ");
            log.info("ImportService: начат импорт файла-выгрузки из ОДОПМ");

            // отправим все на сервер
            realEstateDocumentList.forEach(
                document -> realEstateDocumentService.createDocument(
                    document,
                    true,
                    "Real estate import from xlsx"
                )
            );

            log.info("ImportService: закончен импорт файла-выгрузки из ОДОПМ");
        } catch (CsvValidationException ex) {
            log.error(
                ImportController.class.getName()
                    + ": exception while reading file with path - "
                    + stringPathFlats
            );
            log.error(ex.getMessage());
        } catch (IOException ex) {
            log.error(
                ImportController.class.getName()
                    + ": exception while reading file"
            );
            log.error(ex.getMessage());
        }
    }

    /**
     * Обрезает первый и последний символы строки.
     *
     * @param str - исходная строка
     * @return подстрока, убрали первый и последний символы из str
     */
    private String removeQuotes(String str) {
        if (str == null || str.length() < 2) {
            return "";
        }
        return str.substring(1, str.length() - 1);
    }

    /**
     * Загрузить доп информацию в отселяемые дома.
     *
     * @param json json
     */
    public void loadAddInfoForRealEstate(MultipartFile json) {
        log.info("Запуск процесса по обновлению доп информации отселяемого дома");

        List<RealEstateAddInfo> realEstateAddInfos;
        try {
            realEstateAddInfos = Arrays.asList(
                new ObjectMapper().readValue(json.getInputStream(), RealEstateAddInfo[].class)
            );
        } catch (IOException e) {
            log.error("Ошибка при получении данных из файла");
            log.error(e.getMessage());
            return;
        }

        for (RealEstateAddInfo realEstateAddInfo : realEstateAddInfos) {
            log.debug("Начало обновление дома с УНОМ: {}", realEstateAddInfo.getUnom());
            RealEstateDocument realEstate = realEstateDocumentService.fetchDocumentByUnom(realEstateAddInfo.getUnom());

            if (isNull(realEstate)) {
                log.error("Не удалось найти дом с уном: {}", realEstateAddInfo.getUnom());
                continue;
            }
            RealEstateDataType realEstateData = realEstate.getDocument().getRealEstateData();

            realEstateData.setCoordinates(realEstateAddInfo.getGeoData());
            realEstateData.setVoteYesPercent(realEstateAddInfo.getProcZa());
            realEstateData.setVoteNoPercent(realEstateAddInfo.getProcProtiv());

            if (nonNull(realEstateAddInfo.getWears()) && !realEstateAddInfo.getWears().isEmpty()) {
                realEstateData.setWearInfo(new RealEstateDataType.WearInfo());

                List<RealEstateDataType.WearInfo.WearItem> wearItems = realEstateData.getWearInfo().getWearItem();
                for (WearAddInfo wear : realEstateAddInfo.getWears()) {
                    RealEstateDataType.WearInfo.WearItem newWear = new RealEstateDataType.WearInfo.WearItem();
                    newWear.setDate(LocalDate.now());
                    newWear.setWearPercent(wear.getWearPercent());
                    newWear.setWearYear(wear.getWearYear());
                    wearItems.add(newWear);
                }
            }

            realEstateDocumentService.updateDocument(
                realEstate.getId(), realEstate, true, true, "AddInfo update"
            );

            log.debug("Дом с УНОМ: {} обработан", realEstateAddInfo.getUnom());
        }

        log.info("Процесс обновления доп информации отселяемого дома завершен");
    }

    /**
     * Формирование JSON с данными по координатам RealEstate.
     *
     * @param path27451csv Путь до csv файла каталога 27451.
     * @param path28609csv Путь до csv файла каталога 28609.
     * @param path28608csv Путь до csv файла каталога 28608.
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @return JSON.
     */
    public String realEstateJsonAddInfo(
        String path27451csv, String path28609csv, String path28608csv, String pathNpcXml
    ) {
        List<RealEstateAddInfo> estateAddInfos = realEstateDocumentService.getUnomRealEstates()
            .stream()
            .map(unom -> {
                return new RealEstateAddInfo(
                    unom,
                    null,
                    new ArrayList<WearAddInfo>(),
                    null,
                    null
                );
            })
            .collect(Collectors.toList());

        return jsonMapper.writeObject(
            fillVotesAndWearRealEstateAddInfo(
                fillGeoDataRealEstateAddInfo(estateAddInfos, path27451csv), pathNpcXml
            )
        );
    }

    private List<RealEstateAddInfo> fillGeoDataRealEstateAddInfo(
        List<RealEstateAddInfo> estateAddInfos, String path27451csv
    ) {
        try (
            CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(path27451csv), "windows-1251")
            )
        ) {
            String[] nextLineArray;
            reader.readNext(); //skip title rows
            reader.readNext();
            while ((nextLineArray = reader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");
                final String unom = removeQuotes(values[6]);
                if (!"0".equals(unom)) {
                    Optional<RealEstateAddInfo> optionalRealEstateAddInfo = estateAddInfos
                        .stream()
                        .filter(estate -> estate.getUnom().equals(unom))
                        .findFirst();
                    optionalRealEstateAddInfo.ifPresent(
                        realEstateAddInfo -> realEstateAddInfo.setGeoData(removeQuotes(values[40]))
                    );
                }
            }
            return estateAddInfos;
        } catch (CsvValidationException | IOException ex) {
            log.error(ex.getMessage());
            return estateAddInfos;
        }
    }

    private List<RealEstateAddInfo> fillWearsRealEstateAddInfo(
        List<RealEstateAddInfo> estateAddInfos, String path28608csv
    ) {
        try (
            CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(path28608csv), "windows-1251")
            )
        ) {
            String[] nextLineArray;
            reader.readNext(); //skip title rows
            while ((nextLineArray = reader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = removeQuotes(Arrays.toString(nextLineArray));
                String[] values = nextLine.split(";", -1);
                final String unom = values[3];
                if (!"0".equals(unom)) {
                    Optional<RealEstateAddInfo> optionalRealEstateAddInfo = estateAddInfos
                        .stream()
                        .filter(estate -> estate.getUnom().equals(unom))
                        .findFirst();
                    if (optionalRealEstateAddInfo.isPresent()) {
                        WearAddInfo wearAddInfoOne = new WearAddInfo();
                        wearAddInfoOne.setWearPercent(values[25]);
                        wearAddInfoOne.setWearYear(2020);
                        WearAddInfo wearAddInfoTwo = new WearAddInfo();
                        if (StringUtils.isNotBlank(values[27])) {
                            wearAddInfoTwo.setWearYear(Integer.parseInt(values[27]));
                        }
                        wearAddInfoTwo.setWearPercent(values[26]);
                        List<WearAddInfo> wears = optionalRealEstateAddInfo.get().getWears();
                        wears.add(wearAddInfoOne);
                        wears.add(wearAddInfoTwo);
                    }
                }
            }
            return estateAddInfos;
        } catch (CsvValidationException | IOException ex) {
            log.error(ex.getMessage());
            return estateAddInfos;
        }
    }

    private List<RealEstateAddInfo> fillVotesAndWearRealEstateAddInfo(
        List<RealEstateAddInfo> estateAddInfos, String pathNpcXml
    ) {
        try {
            InputStream reader = new FileInputStream(pathNpcXml);
            StringWriter writer = new StringWriter();
            IOUtils.copy(reader, writer, "UTF-8");
            Root unmarshal = RIXmlUtils.unmarshal(writer.toString(), Root.class);
            reader.close();
            writer.close();
            for (Root.Region region : unmarshal.getRegion()) {
                for (Root.Region.Kvartal kvartal : region.getKvartal()) {
                    for (Root.Region.Kvartal.SnosBuildings.SnosBuilding snosBuilding
                        : kvartal.getSnosBuildings().getSnosBuilding()) {
                        String unom = snosBuilding.getUnom().toString();
                        Optional<RealEstateAddInfo> optionalRealEstateAddInfo = estateAddInfos
                            .stream()
                            .filter(estate -> estate.getUnom().equals(unom))
                            .findFirst();
                        if (optionalRealEstateAddInfo.isPresent()) {
                            RealEstateAddInfo realEstateAddInfo = optionalRealEstateAddInfo.get();
                            WearAddInfo wearAddInfo = new WearAddInfo();
                            wearAddInfo.setWearPercent(snosBuilding.getProcIzn().toString());
                            if (nonNull(snosBuilding.getGodUstIzn())) {
                                wearAddInfo.setWearYear(snosBuilding.getGodUstIzn());
                            }
                            List<WearAddInfo> wears = realEstateAddInfo.getWears();
                            wears.add(wearAddInfo);
                            realEstateAddInfo.setProcZa(snosBuilding.getProcZa().toString());
                            realEstateAddInfo.setProcProtiv(snosBuilding.getProcProtiv().toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return estateAddInfos;
    }

    /**
     * Формирование JSON RealEstate по unom.
     *
     * @param path27451csv Путь до csv файла каталога 27451.
     * @param path27455csv Путь до csv файла каталога 27455.
     * @param path27456csv Путь до csv файла каталога 27456.
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @param targetUnom   Уном.
     * @param odopmAddress Адрес из ОДОПМ.
     * @return json.
     */
    public String createRealEstateJsonByUnom(
        String path27451csv, String path27455csv, String path27456csv,
        String pathNpcXml, String targetUnom, String odopmAddress
    ) {
        RealEstateDocument document = new RealEstateDocument();
        RealEstate realEstate = new RealEstate();
        document.setDocument(realEstate);
        RealEstateDataType realEstateData = new RealEstateDataType();
        realEstate.setRealEstateData(realEstateData);

        realEstateData.setUNOM(new BigInteger(targetUnom));
        realEstateData.setAddress(odopmAddress);

        try (
            CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(path27451csv), "windows-1251")
            )
        ) {
            String[] nextLineArray;
            reader.readNext(); //skip title rows
            reader.readNext();
            while ((nextLineArray = reader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String unom = removeQuotes(values[6]);

                if ("0".equals(unom)) {
                    // unom пустой, значит, это квартира
                    final String address = removeQuotes(values[5]);

                    if (address.contains(realEstateData.getAddress() + ", квартира")) {
                        // квартира нашлась по адресу, добавим

                        // проверим, не является ли она коммуналкой
                        if (address.contains("комната")) {
                            final String croppedAddress = address.substring(0, address.indexOf(", комната"));

                            RealEstateDataType.Flats flatsObject
                                = realEstateData.getFlats() == null
                                ? new RealEstateDataType.Flats()
                                : realEstateData.getFlats();
                            realEstateData.setFlats(flatsObject);

                            Optional<FlatType> flatOptional = flatsObject.getFlat()
                                .stream().filter(f -> f.getAddress().equals(croppedAddress)).findAny();

                            FlatType flat;
                            if (flatOptional.isPresent()) {
                                flat = flatOptional.get();
                            } else {
                                flat = new FlatType();

                                flat.setGlobalID(new BigInteger(values[0]));
                                flat.setFlatID(values[0]);

                                flat.setAID(removeQuotes(values[3]));
                                flat.setAddress(croppedAddress);
                                NsiType l4type = new NsiType();
                                l4type.setName(removeQuotes(values[21]));
                                flat.setApartmentL4TYPE(l4type);
                                flat.setApartmentL4VALUE(removeQuotes(values[22]));

                                flat.setNREG(removeQuotes(values[27]));
                                flat.setNFIAS(removeQuotes(values[29]));

                                NsiType sostad = new NsiType();
                                sostad.setName(removeQuotes(values[37]));
                                flat.setSOSTAD(sostad);

                                NsiType status = new NsiType();
                                status.setName(removeQuotes(values[38]));
                                flat.setSTATUS(status);

                                List<FlatType> flats = flatsObject.getFlat();
                                flats.add(flat);
                            }

                            RoomType room = new RoomType();

                            NsiType l5type = new NsiType();
                            l5type.setName(removeQuotes(values[23]));
                            room.setRoomID(values[0]);
                            room.setRoomL5TYPE(l5type);
                            room.setRoomL5VALUE(removeQuotes(values[24]));

                            FlatType.Rooms roomsObject
                                = flat.getRooms() == null
                                ? new FlatType.Rooms()
                                : flat.getRooms();

                            List<RoomType> rooms = roomsObject.getRoom();
                            rooms.add(room);
                            flat.setRooms(roomsObject);
                        } else {
                            FlatType flat = new FlatType();

                            flat.setGlobalID(new BigInteger(values[0]));
                            flat.setFlatID(values[0]);

                            flat.setAID(removeQuotes(values[3]));
                            flat.setAddress(removeQuotes(values[5]));
                            NsiType l4type = new NsiType();
                            l4type.setName(removeQuotes(values[21]));
                            flat.setApartmentL4TYPE(l4type);
                            flat.setApartmentL4VALUE(removeQuotes(values[22]));

                            flat.setNREG(removeQuotes(values[27]));
                            flat.setNFIAS(removeQuotes(values[29]));

                            NsiType sostad = new NsiType();
                            sostad.setName(removeQuotes(values[37]));
                            flat.setSOSTAD(sostad);

                            NsiType status = new NsiType();
                            status.setName(removeQuotes(values[38]));
                            flat.setSTATUS(status);

                            RealEstateDataType.Flats flatsObject
                                = realEstateData.getFlats() == null
                                ? new RealEstateDataType.Flats()
                                : realEstateData.getFlats();

                            List<FlatType> flats = flatsObject.getFlat();
                            flats.add(flat);
                            realEstateData.setFlats(flatsObject);
                        }
                    }
                } else {
                    // unom есть, значит это ОН
                    if (targetUnom.equals(unom)) {
                        // ОС нашелся по UNOM-у, обогатим данные
                        // квартира нашлась по адресу, добавим
                        realEstateData.setGlobalID(new BigInteger(values[0]));

                        NsiType objectStatus = new NsiType();
                        objectStatus.setName(removeQuotes(values[2]));
                        realEstateData.setSTATUS(objectStatus);

                        NsiType objectType = new NsiType();
                        objectType.setName(removeQuotes(values[4]));
                        realEstateData.setOBJTYPE(objectType);

                        NsiType subjectRfr1 = new NsiType();
                        subjectRfr1.setName(removeQuotes(values[7]));
                        realEstateData.setSubjectRFP1(subjectRfr1);

                        NsiType settlementP3 = new NsiType();
                        settlementP3.setName(removeQuotes(values[8]));
                        realEstateData.setSettlementP3(settlementP3);

                        NsiType townP4 = new NsiType();
                        townP4.setName(removeQuotes(values[9]));
                        realEstateData.setTownP4(townP4);

                        NsiType munOkrugP5 = new NsiType();
                        munOkrugP5.setName(removeQuotes(values[10]));
                        realEstateData.setMunOkrugP5(munOkrugP5);

                        NsiType localityP6 = new NsiType();
                        localityP6.setName(removeQuotes(values[11]));
                        realEstateData.setLocalityP6(localityP6);

                        NsiType elementP7 = new NsiType();
                        elementP7.setName(removeQuotes(values[12]));
                        realEstateData.setElementP7(elementP7);

                        NsiType additionalAddrElementP90 = new NsiType();
                        additionalAddrElementP90.setName(removeQuotes(values[13]));
                        realEstateData.setAdditionalAddrElementP90(additionalAddrElementP90);

                        NsiType refinementAddrP91 = new NsiType();
                        refinementAddrP91.setName(removeQuotes(values[14]));
                        realEstateData.setRefinementAddrP91(refinementAddrP91);

                        NsiType houseL1Type = new NsiType();
                        houseL1Type.setName(removeQuotes(values[15]));
                        realEstateData.setHouseL1TYPE(houseL1Type);
                        realEstateData.setHouseL1VALUE(removeQuotes(values[16]));

                        NsiType corpL2Type = new NsiType();
                        corpL2Type.setName(removeQuotes(values[17]));
                        realEstateData.setCorpL2TYPE(corpL2Type);
                        realEstateData.setCorpL2VALUE(removeQuotes(values[18]));

                        NsiType buildingL3Type = new NsiType();
                        buildingL3Type.setName(removeQuotes(values[19]));
                        realEstateData.setBuildingL3TYPE(buildingL3Type);
                        realEstateData.setBuildingL3VALUE(removeQuotes(values[20]));

                        NsiType admArea = new NsiType();
                        admArea.setName(removeQuotes(values[25]));
                        realEstateData.setADMAREA(admArea);

                        NsiType adrType = new NsiType();
                        adrType.setName(removeQuotes(values[35]));
                        realEstateData.setADRTYPE(adrType);

                        NsiType vid = new NsiType();
                        vid.setName(removeQuotes(values[36]));
                        realEstateData.setVID(vid);

                        realEstateData.setCoordinates(removeQuotes(values[40]));
                    }
                }
            }
        } catch (CsvValidationException | IOException ex) {
            log.error(ex.getMessage());
        }

        try (
            CSVReader cadastralNumsReader = new CSVReader(
                new InputStreamReader(new FileInputStream(path27455csv), "windows-1251")
            )
        ) {
            String[] nextLineArray;
            // заполним кадастровые номера ОН
            cadastralNumsReader.readNext(); //skip title rows
            cadastralNumsReader.readNext();
            while ((nextLineArray = cadastralNumsReader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String globalObjectId = removeQuotes(values[3]);

                if (realEstateData.getGlobalID().toString().equals(globalObjectId)) {
                    // нашли ОН с указанным globalId

                    RealEstateDataType.CadastralNums cadastralNumsObject
                        = realEstateData.getCadastralNums() == null
                        ? new RealEstateDataType.CadastralNums()
                        : realEstateData.getCadastralNums();
                    List<RealEstateDataType.CadastralNums.CadastralNum> cadastralNums
                        = cadastralNumsObject.getCadastralNum();

                    RealEstateDataType.CadastralNums.CadastralNum cadastralNum
                        = new RealEstateDataType.CadastralNums.CadastralNum();

                    cadastralNum.setGlobalID(new BigInteger(values[0]));
                    cadastralNum.setValue(removeQuotes(values[5]));

                    cadastralNums.add(cadastralNum);
                    realEstateData.setCadastralNums(cadastralNumsObject);
                }

            }
        } catch (CsvValidationException | IOException ex) {
            log.error(ex.getMessage());
        }

        try (
            CSVReader cadastralNumsZUReader = new CSVReader(
                new InputStreamReader(new FileInputStream(path27456csv), "windows-1251")
            )
        ) {
            String[] nextLineArray;
            // заполним кадастровые номера ЗУ
            cadastralNumsZUReader.readNext(); //skip title rows
            cadastralNumsZUReader.readNext();
            while ((nextLineArray = cadastralNumsZUReader.readNext()) != null) {
                if (nextLineArray.length == 0) {
                    continue;
                }
                String nextLine = nextLineArray[0];
                String[] values = nextLine.split(";");

                final String globalObjectId = removeQuotes(values[3]);

                if (realEstateData.getGlobalID().toString().equals(globalObjectId)) {
                    // нашли ОН с указанным globalId
                    RealEstateDataType.CadastralNumsZU cadastralNumsObject
                        = realEstateData.getCadastralNumsZU() == null
                        ? new RealEstateDataType.CadastralNumsZU()
                        : realEstateData.getCadastralNumsZU();
                    List<RealEstateDataType.CadastralNumsZU.CadastralZU> cadastralNums
                        = cadastralNumsObject.getCadastralZU();

                    RealEstateDataType.CadastralNumsZU.CadastralZU cadastralNum
                        = new RealEstateDataType.CadastralNumsZU.CadastralZU();
                    cadastralNum.setGlobalID(new BigInteger(values[0]));
                    cadastralNum.setValue(removeQuotes(values[5]));

                    cadastralNums.add(cadastralNum);
                    realEstateData.setCadastralNumsZU(cadastralNumsObject);
                }
            }
        } catch (CsvValidationException | IOException ex) {
            log.error(ex.getMessage());
        }

        try {
            InputStream reader = new FileInputStream(pathNpcXml);
            StringWriter writer = new StringWriter();
            IOUtils.copy(reader, writer, "UTF-8");
            Root unmarshal = RIXmlUtils.unmarshal(writer.toString(), Root.class);
            reader.close();
            writer.close();
            for (Root.Region region : unmarshal.getRegion()) {
                for (Root.Region.Kvartal kvartal : region.getKvartal()) {
                    for (Root.Region.Kvartal.SnosBuildings.SnosBuilding snosBuilding
                        : kvartal.getSnosBuildings().getSnosBuilding()) {
                        String unom = snosBuilding.getUnom().toString();
                        if (targetUnom.equals(unom)) {
                            realEstateData.setVoteYesPercent(snosBuilding.getProcZa().toString());
                            realEstateData.setVoteNoPercent(snosBuilding.getProcProtiv().toString());

                            realEstateData.setWearInfo(new RealEstateDataType.WearInfo());

                            RealEstateDataType.WearInfo.WearItem newWear = new RealEstateDataType.WearInfo.WearItem();
                            newWear.setDate(LocalDate.now());
                            newWear.setWearPercent(snosBuilding.getProcIzn().toString());
                            newWear.setWearYear(snosBuilding.getGodUstIzn());
                            List<RealEstateDataType.WearInfo.WearItem> wearItems
                                = realEstateData.getWearInfo().getWearItem();
                            wearItems.add(newWear);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return jsonMapper.writeObject(document);
    }

    /**
     * Формирование JSON с данными по этажности, подъездности RealEstate.
     *
     * @param pathNpcXml   Путь до xml файла НПЦ.
     * @return json.
     */
    public String realEstateJsonEntranceAddInfo(String pathNpcXml) {
        List<RealEstateEntranceAddInfo> estateAddInfos = realEstateDocumentService.getUnomRealEstates()
            .stream()
            .map(unom -> new RealEstateEntranceAddInfo(
                unom,
                flatService.getFlatsNumInfoByUnom(unom)
                    .stream()
                    .map(s -> new FlatAddInfo(s, null, null))
                    .collect(Collectors.toList()),
                null,
                null
            ))
            .collect(Collectors.toList());

        return jsonMapper.writeObject(
            fillEntrancesAndFloorRealEstateAddInfo(estateAddInfos, pathNpcXml)
        );
    }

    private List<RealEstateEntranceAddInfo> fillEntrancesAndFloorRealEstateAddInfo(
        List<RealEstateEntranceAddInfo> estateAddInfos, String pathNpcXml
    ) {
        try {
            InputStream reader = new FileInputStream(pathNpcXml);
            StringWriter writer = new StringWriter();
            IOUtils.copy(reader, writer, "UTF-8");
            Root unmarshal = RIXmlUtils.unmarshal(writer.toString(), Root.class);
            reader.close();
            writer.close();
            for (Root.Region region : unmarshal.getRegion()) {
                for (Root.Region.Kvartal kvartal : region.getKvartal()) {
                    for (Root.Region.Kvartal.SnosBuildings.SnosBuilding snosBuilding
                        : kvartal.getSnosBuildings().getSnosBuilding()) {
                        String unom = snosBuilding.getUnom().toString();
                        Optional<RealEstateEntranceAddInfo> optionalRealEstateAddInfo = estateAddInfos
                            .stream()
                            .filter(estate -> estate.getUnom().equals(unom))
                            .findFirst();
                        if (optionalRealEstateAddInfo.isPresent()) {
                            RealEstateEntranceAddInfo realEstateAddInfo = optionalRealEstateAddInfo.get();
                            realEstateAddInfo.setEntrances(snosBuilding.getPodezd().toString());
                            realEstateAddInfo.setFloors(snosBuilding.getFloorMax().toString());
                            for (Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat snosFlat
                                : snosBuilding.getSnosFlats().getSnosFlat()) {
                                String flatNumber = snosFlat.getKvnom();
                                Optional<FlatAddInfo> flatAddInfo = realEstateAddInfo.getFlats()
                                    .stream()
                                    .filter(flat -> flat.getApartNumber().equals(flatNumber))
                                    .findFirst();
                                flatAddInfo.ifPresent(
                                    addInfo -> {
                                        if (nonNull(snosFlat.getEntrance())) {
                                            addInfo.setEntrance(snosFlat.getEntrance().toString());
                                        }
                                        if (nonNull(snosFlat.getKvnomFloor())) {
                                            addInfo.setFloor(snosFlat.getFloor().toString());
                                        }
                                    }
                                );
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return estateAddInfos;
    }

    /**
     * Загрузка доп информации(подъездность, этажность) RealEstate из json.
     *
     * @param json json
     */
    public void loadEntranceAddInfoForRealEstate(MultipartFile json) {
        log.info("Запуск процесса по обновлению доп информации отселяемого дома");

        List<RealEstateEntranceAddInfo> realEstateAddInfos;
        try {
            realEstateAddInfos = Arrays.asList(
                new ObjectMapper().readValue(json.getInputStream(), RealEstateEntranceAddInfo[].class)
            );
        } catch (IOException e) {
            log.error("Ошибка при получении данных из файла");
            log.error(e.getMessage());
            return;
        }

        for (RealEstateEntranceAddInfo realEstateAddInfo : realEstateAddInfos) {
            log.debug("Начало обновление дома с УНОМ: {}", realEstateAddInfo.getUnom());
            RealEstateDocument realEstate = realEstateDocumentService.fetchDocumentByUnom(realEstateAddInfo.getUnom());

            if (isNull(realEstate)) {
                log.error("Не удалось найти дом с уном: {}", realEstateAddInfo.getUnom());
                continue;
            }
            RealEstateDataType realEstateData = realEstate.getDocument().getRealEstateData();

            if (nonNull(realEstateAddInfo.getEntrances())) {
                realEstateData.setEntranceCount(Integer.parseInt(realEstateAddInfo.getEntrances()));
            }
            if (nonNull(realEstateAddInfo.getFloors())) {
                realEstateData.setCfloor(Integer.parseInt(realEstateAddInfo.getFloors()));
            }

            if (nonNull(realEstateAddInfo.getFlats())) {
                for (FlatAddInfo infoFlat : realEstateAddInfo.getFlats()) {
                    Optional<FlatType> flatType = realEstateData.getFlats().getFlat()
                        .stream()
                        .filter(flat -> flat.getApartmentL4VALUE().equals(infoFlat.getApartNumber()))
                        .findFirst();
                    flatType.ifPresent(findFlat -> {
                        if (nonNull(infoFlat.getEntrance())) {
                            findFlat.setEntrance(infoFlat.getEntrance());
                        }
                        if (nonNull(infoFlat.getFloor())) {
                            findFlat.setFloor(Integer.parseInt(infoFlat.getFloor()));
                        }
                    });
                }
            }

            realEstateDocumentService.updateDocument(
                realEstate.getId(), realEstate, true, true, "AddInfo update"
            );

            log.debug("Дом с УНОМ: {} обработан", realEstateAddInfo.getUnom());
        }

        log.info("Процесс обновления доп информации отселяемого дома завершен");
    }

}
