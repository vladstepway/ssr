package ru.croc.ugd.ssr.integration.processor;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.SNILSDoubleType;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.integration.service.MdmIntegrationService;
import ru.croc.ugd.ssr.mapper.SsrSmevResponseMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.SsrSmevResponseDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.integration.asur.apartmentdata.Datalist;
import ru.croc.ugd.ssr.model.integration.asur.snilsdoubles.НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SendEmailService;
import ru.croc.ugd.ssr.service.bus.response.BusResponseService;
import ru.croc.ugd.ssr.service.disabledperson.DisabledPersonService;
import ru.croc.ugd.ssr.service.document.DisabledPersonDocumentService;
import ru.croc.ugd.ssr.service.document.SsrSmevResponseDocumentService;
import ru.croc.ugd.ssr.smevresponse.SsrSmevResponseData;
import ru.reinform.cdp.exception.RIException;
import ru.reinform.cdp.utils.core.RIXmlUtils;

import java.io.StringReader;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Slf4j
@Component
@AllArgsConstructor
public class SmevResponseProcessor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final SsrSmevResponseDocumentService ssrSmevResponseDocumentService;
    private final SsrSmevResponseMapper ssrSmevResponseMapper;
    private final FlatService flatService;
    private final IntegrationService integrationService;
    private final MdmIntegrationService mdmIntegrationService;
    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final SendEmailService sendEmailService;
    private final BusResponseService busResponseService;
    private final DisabledPersonDocumentService disabledPersonDocumentService;
    private final DisabledPersonService disabledPersonService;

    @Transactional
    public void process() {
        try (final Stream<SsrSmevResponseDocument> smevResponseDocumentStream = ssrSmevResponseDocumentService
            .findByProcessEndDateTimeIsNullAndStream()
        ) {
            smevResponseDocumentStream.forEach(this::process);
        }
    }

    private void process(final SsrSmevResponseDocument ssrSmevResponseDocument) {
        log.info("SsrSmevResponseDocument processing started: id {}", ssrSmevResponseDocument.getId());
        final SmevResponse smevResponse = ssrSmevResponseMapper.toSmevResponse(ssrSmevResponseDocument);

        final SsrSmevResponseData ssrSmevResponseData = ssrSmevResponseDocument
            .getDocument()
            .getSsrSmevResponseData();
        ssrSmevResponseData.setProcessStartDateTime(LocalDateTime.now());
        ssrSmevResponseDocumentService
            .updateDocument(ssrSmevResponseDocument.getId(), ssrSmevResponseDocument, true, false, null);

        try {
            parseMessageSmev(smevResponse);
        } catch (Exception e) {
            log.warn("SsrSmevResponseDocument unable to process: id {}", ssrSmevResponseDocument.getId(), e);
            ssrSmevResponseData.setProcessingFailure(true);
        }

        ssrSmevResponseData.setProcessEndDateTime(LocalDateTime.now());
        ssrSmevResponseDocumentService
            .updateDocument(ssrSmevResponseDocument.getId(), ssrSmevResponseDocument, true, false, null);

        log.info("SsrSmevResponseDocument processing finished: id {}", ssrSmevResponseDocument.getId());
    }

    /**
     * Парсинг входящего сообщения SMEV.
     *
     * @param response входящее сообщение
     */
    private void parseMessageSmev(final SmevResponse response) {
        final String serviceNumber = response.getServiceNumber();
        final Optional<BusRequestDocument> optionalRequestByServiceNumber = busResponseService
            .findRequestByServiceNumber(serviceNumber);

        if (optionalRequestByServiceNumber.isPresent()) {
            busResponseService.handleResponse(optionalRequestByServiceNumber.get(), response);
        } else {
            handleResponseInLegacyStyle(response);
        }
    }

    private void handleResponseInLegacyStyle(final SmevResponse response) {
        if (response.getStatusCode().equals("1004") && nonNull(response.getResultDescription())) {
            parseSmev1004(response);
        } else if (
            response.getStatusCode().equals("1005")
                || response.getStatusCode().equals("1002")
                || response.getStatusCode().equals("1006")
                || response.getStatusCode().equals("1001")
                || response.getStatusCode().equals("1008")
                || response.getStatusCode().equals("1009")
        ) {
            parseSmevError(response, response.getNote());
        }
    }

    /**
     * Обработка ответа smev с кодом 1004.
     *
     * @param response ответ smev
     */
    private void parseSmev1004(final SmevResponse response) {
        if (response.getResultDescription().contains("ОТВЕТ_СНИЛС")) {
            parseSnilsData(response);
        } else if (response.getResultDescription().startsWith("<datalist")) {
            parseApartmentData(response);
        }
    }

    /**
     * Обработка ответа smev с кодами ошибок.
     *
     * @param response ответ smev
     * @param message  описание ошибки
     */
    private void parseSmevError(SmevResponse response, String message) {
        // нет параметров отвечающих за тип ответа(тип документа), так что ищем и по жителям, и по квартирам
        PersonDocument personDocument = personDocumentService.getPersonByEno(response.getServiceNumber());
        if (nonNull(personDocument)) {
            updatePersonIntegrationPfrStatus(personDocument, message, response.getStatusCode());
        } else {
            String flatId = flatService.getFlatIdByEno(response.getServiceNumber());
            if (nonNull(flatId)) {
                if (response.getStatusCode().equals("1005")) {
                    updateFlatIntegrationEzdStatus(flatId, message, "обогащено");
                } else {
                    updateFlatIntegrationEzdStatus(flatId, message, "ошибка обогащения");
                }
            } else {
                log.debug("Пришел ответ для неизвестного документа");
            }
        }
    }

    /**
     * Обноляем статус интеграции по жителю из ПФР.
     *
     * @param personDocument житель
     * @param message        сообщение
     */
    private void updatePersonIntegrationPfrStatus(PersonDocument personDocument, String message, String code) {
        if (!code.equals("1005")) {
            personDocument.getDocument().getPersonData().setUpdatedFromPFRstatus(message);
        } else if (!personDocument.getDocument().getPersonData().getUpdatedFromPFRstatus().equals("обогащено")) {
            personDocument.getDocument().getPersonData().setUpdatedFromPFRstatus(message);
        }
        personDocument.getDocument().getPersonData().setUpdatedFromPFRdate(LocalDate.now());

        if (code.equals("1005")) {
            personDocument.getDocument().getPersonData().setUpdatedFromELKstatus("обогащено");
            personDocument.getDocument().getPersonData().setUpdatedFromELKdate(LocalDate.now());
        }
        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, message
        );
    }

    /**
     * Обноляем статус интеграции по квартире из ЕЖД.
     *
     * @param flatId  id квартиры
     * @param message сообщение
     */
    private void updateFlatIntegrationEzdStatus(String flatId, String message, String fullMessage) {
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchByFlatId(flatId);
        List<FlatType> flatTypes = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
        Optional<FlatType> optionalFlatType = flatTypes.stream()
            .filter(type -> flatId.equals(type.getFlatID())).findAny();
        if (optionalFlatType.isPresent()) {
            FlatType flatType = optionalFlatType.get();
            flatType.setUpdatedFromEZDstatus(message);
            flatType.setUpdatedFromEZDdate(LocalDate.now());
            flatType.setUpdatedFullStatus(fullMessage);
            flatType.setUpdatedFullDate(LocalDate.now());
            realEstateDocumentService.updateDocument(
                realEstateDocument.getId(), realEstateDocument, true, true, message
            );
        } else {
            log.debug("Не найден realEstateDocument, содержащий квартиру с flatId = " + flatId);
        }
    }

    /**
     * Парсим ответ по документу 10477.
     *
     * @param response ответ
     */
    private void parseSnilsData(SmevResponse response) {
        log.info("Начало обработки документа 10477 с ЕНО: {}", response.getServiceNumber());

        PersonDocument personDocument = personDocumentService.getPersonByEno(response.getServiceNumber());
        if (isNull(personDocument)) {
            log.info("personDocument not found by service number {}", response.getServiceNumber());
            return;
        }
        PersonType personData = personDocument.getDocument().getPersonData();
        personData.setUpdatedFromPFRdate(LocalDate.now());
        personData.setUpdatedFromPFRstatus("обогащено");

        if (response.getResultDescription().contains("НЕОДНОЗНАЧНЫЙ_ОТВЕТ_СНИЛС")) {
            log.debug("Пришел неоднозначный ответ по СНИЛС для документа: {}", personDocument.getId());
            parseSnilsDoublesData(response, personData);
        } else {
            personData.setSNILS(
                getStringByXpath(response.getResultDescription(), "/ОТВЕТ_СНИЛС/СтраховойНомер")
            );
            personData.setGender(
                getStringByXpath(response.getResultDescription(), "/ОТВЕТ_СНИЛС/Пол")
            );

            try {
                personData.setSsoID(mdmIntegrationService.getSsoId(personData.getSNILS()));
            } catch (Exception e) {
                log.error("При обогащении жителя из мос.ру произошла ошибка: {}", e.toString());
                if (isNull(e.getMessage())) {
                    sendEmailService.sendEmailErrorResettlement(personData.getUNOM().toString(), "");
                } else {
                    sendEmailService.sendEmailErrorResettlement(personData.getUNOM().toString(), e.getMessage());
                }
            }
            personData.setUpdatedFromELKdate(LocalDate.now());
            personData.setUpdatedFromELKstatus("обогащено");
        }

        personDocumentService.updateDocument(
            personDocument.getId(), personDocument, true, true, ""
        );

        log.info("Обработка документа 10477 завершена, id документа: {}", personDocument.getId());
    }

    /**
     * Парсим неоднозначный ответ по документу 10477.
     *
     * @param response ответ
     */
    private void parseSnilsDoublesData(SmevResponse response, PersonType personData) {
        НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС doublesSnilsResponse = RIXmlUtils.unmarshal(
            response.getResultDescription(), НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.class
        );

        personData.setGender(doublesSnilsResponse.getПол());

        PersonType.SNILSDoubles snilsDoubles;
        if (isNull(personData.getSNILSDoubles())) {
            snilsDoubles = new PersonType.SNILSDoubles();
            personData.setSNILSDoubles(snilsDoubles);
        } else {
            snilsDoubles = personData.getSNILSDoubles();
        }

        for (НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике doubles : doublesSnilsResponse.getСведенияОдвойнике()) {
            SNILSDoubleType snilsDoubleType = new SNILSDoubleType();
            snilsDoubleType.setSNILS(doubles.getСтраховойНомер());

            НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения birthdayPlace = doubles.getМестоРождения();
            if (nonNull(birthdayPlace)) {
                snilsDoubleType.setBptype(birthdayPlace.getТипМестаРождения());
                snilsDoubleType.setBtown(birthdayPlace.getГородРождения());
                snilsDoubleType.setBarea(birthdayPlace.getРайонРождения());
                snilsDoubleType.setBregion(birthdayPlace.getРегионРождения());
                snilsDoubleType.setBcountry(birthdayPlace.getСтранаРождения());
            }

            НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент passport =
                doubles.getУдостоверяющийДокумент();
            if (nonNull(passport)) {
                snilsDoubleType.setDocType(passport.getТипУдостоверяющего());
                НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ document =
                    passport.getДокумент();
                if (nonNull(document)) {
                    snilsDoubleType.setDocName(document.getНаименованиеУдостоверяющего());
                    snilsDoubleType.setDocSerRUS(document.getСерияРусскиеБуквы());
                    snilsDoubleType.setDocSerLAT(document.getСерияРимскиеЦифры());
                    snilsDoubleType.setDocNum(
                        nonNull(document.getНомерУдостоверяющего())
                            ? document.getНомерУдостоверяющего().toString()
                            : null
                    );
                    if (nonNull(document.getДатаВыдачи())) {
                        try {
                            snilsDoubleType.setDocDate(LocalDate.parse(document.getДатаВыдачи(), FORMATTER));
                        } catch (DateTimeParseException e) {
                            log.error(
                                "Пришел кривой ответ АСУР, вместо даты ДатаВыдачи пришло: {}", document.getДатаВыдачи()
                            );
                        }

                    }
                }
            }

            snilsDoubles.getSNILSDouble().add(snilsDoubleType);
        }

        //запрашивать ssoId не будем.
        personData.setUpdatedFromELKdate(LocalDate.now());
        personData.setUpdatedFromELKstatus("обогащено");
    }

    /**
     * Обработаем пришедший xml по 760ому документу.
     *
     * @param response ответ
     */
    private void parseApartmentData(SmevResponse response) {
        log.info("Обработка документа 760 с ЕНО: {}", response.getServiceNumber());

        RealEstateDocument realEstateDocument = realEstateDocumentService
            .getRealEstatesByEno(response.getServiceNumber());
        if (isNull(realEstateDocument)) {
            log.info("realEstateDocument not found by service number {}", response.getServiceNumber());
            return;
        }
        if (isNull(realEstateDocument.getDocument().getRealEstateData())) {
            log.info("realEstateDocument with id({}) doesn't have realEstateData", realEstateDocument.getId());
            return;
        }

        RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();

        if (isNull(realEstateData.getFlats()) || isNull(realEstateData.getFlats().getFlat())) {
            log.info("realEstateDocument with id({}) doesn't have flats", realEstateDocument.getId());
            return;
        }

        List<FlatType> flats = realEstateData.getFlats().getFlat();

        Optional<FlatType> flat = flats.stream()
            .filter(
                i -> nonNull(i.getIntegrationNumber()) && i.getIntegrationNumber().equals(response.getServiceNumber())
            )
            .findFirst();

        if (flat.isPresent()) {
            Datalist datalist;
            FlatType flatType = flat.get();
            try {
                datalist = RIXmlUtils.unmarshal(response.getResultDescription(), Datalist.class);
                // удалим всех жителей по квартире, если по ним не было отправлено уведомление
                final List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(flatType.getFlatID());
                final List<String> deletedPersonsDocumentIds = personDocuments.stream()
                    .map(PersonDocument::getDocument)
                    .filter(person -> {
                        final PersonType personType = person.getPersonData();
                        return isNull(personType.getSendedMessages()) && !personType.isCreatedFromDisabledPerson();
                    })
                    .map(person ->
                        personDocumentService.deleteDocument(
                            person.getDocumentID(), true, "parseApartmentData"
                        ).getId()
                    )
                    .collect(Collectors.toList());
                //удаляем связь удаленных жителей с маломобильными гражданами
                disabledPersonService.unbindDisabledPersonsWithDeletedPersons(deletedPersonsDocumentIds);
                //данные для дома
                realEstateData.setWallMaterial(datalist.getData().getMaterialSten());
                realEstateData.setLift(datalist.getData().getLift());
                // наполнение квартиры данными, жителями
                fillFlatData(flatType, datalist.getData());
                List<Datalist.People.Man> manList = datalist.getPeople().getMan();
                for (Datalist.People.Man man : manList) {
                    if (nonNull(man.getRegType()) && "1".equals(man.getRegType().toString().trim())) {
                        log.debug("Create person with permanent registration: fio = {}", man.getFio());
                        createPerson(
                            man, flatType.getFlatID(), datalist, realEstateData.getUNOM().toString()
                        );
                    } else {
                        log.debug(
                            "Skip person creation with non-permanent registration: fio = {}, regType = {} ",
                            man.getFio(),
                            man.getRegType()
                        );
                    }
                }
            } catch (RIException e) {
                log.debug(e.getUserMessage());
                log.debug(e.toString());
                log.debug("Ошибка десериализации ответа АСУР: {}", response.getResultDescription());

                // проставим квартире ошибочный статус интеграции
                flatType.setUpdatedFromEZDdate(LocalDate.now());
                flatType.setUpdatedFromEZDstatus("ошибка обогащения");
            }
        }

        realEstateDocumentService.updateDocument(
            realEstateDocument.getId(), realEstateDocument, true, true, ""
        );

        log.info("Обработка документа 760 завершена, id документа: {}", realEstateDocument.getId());
    }

    /**
     * Начиняем квартиру данными.
     *
     * @param flatType квартира
     * @param data     данные
     */
    private void fillFlatData(FlatType flatType, Datalist.Data data) {
        flatType.setFlatNumber(data.getFlat()); //Номер квартиры
        flatType.setUpdatedFromEZDdate(LocalDate.now());
        flatType.setUpdatedFromEZDstatus("обогащено");
        flatType.setRoomsCount(
            nonNull(data.getCroomFlat())
                ? BigInteger.valueOf(Integer.parseInt(data.getCroomFlat().toString()))
                : null
        ); //Количество комнат
        flatType.setVanna(data.getVanna()); //Наличие ванной
        flatType.setSchemaHw(data.getSchemaHw()); //Тип системы ГВС
        flatType.setSanType(data.getSanType()); //Тип санузла
        flatType.setSKitchen(
            nonNull(data.getSKitchen())
                ? Float.parseFloat(data.getSKitchen().replaceAll(",", "."))
                : null
        ); //Площадь кухни
        flatType.setSPriv(
            nonNull(data.getSPriv())
                ? Float.parseFloat(data.getSPriv().replaceAll(",", "."))
                : null
        ); //Приведенная доля общей площади жилого помещения
        flatType.setSLet(
            nonNull(data.getSLet())
                ? Float.parseFloat(data.getSLet().replaceAll(",", "."))
                : null
        ); //Площадь балконов, лоджий, веранд, террас
        flatType.setSGil(
            nonNull(data.getSGil())
                ? Float.parseFloat(data.getSGil().replaceAll(",", "."))
                : null
        ); //Жилая площадь
        flatType.setSAlllet(
            nonNull(data.getSAllLet())
                ? Float.parseFloat(data.getSAllLet().replaceAll(",", "."))
                : null
        ); //Площадь жилого помещения (с учетом балконов, лоджий, веранд, террас)
        flatType.setSAll(
            nonNull(data.getSAll())
                ? Float.parseFloat(data.getSAll().replaceAll(",", "."))
                : null
        ); //Площадь жилого помещения

        //В частной собственности или нет
        if (nonNull(data.getIsSobstv())) {
            if (data.getIsSobstv().toString().trim().equals("1")) {
                flatType.setIsSobstv("в частной собственности");
            } else if (data.getIsSobstv().toString().trim().equals("0")) {
                flatType.setIsSobstv("не частной собственности");
            }
        }

        flatType.setIsDolg(data.getIsDolg()); //Задолженность по оплате жилого помещения и коммунальных услуг
        flatType.setKanal(data.getKanal()); //Наличие водопровода
        flatType.setHw(data.getHw()); //Горячее водоснабжени
        flatType.setGas(data.getGas()); //Газоснабжение
        flatType.setElectr(data.getElectr()); //Электроснабжение
        flatType.setCw(data.getCw()); //Водопровод
        flatType.setFloor(
            nonNull(data.getFloor()) ? data.getFloor().intValue() : null
        ); //Этаж
        flatType.setCounterType(data.getCounterType()); //Наличие приборов учета
        flatType.setCentrOtopl(data.getCentrOtopl()); //Центральное отопление
        flatType.setHousePrinadl(data.getHousePrinadl()); //Тип жилого фонда (ЖСК/муниципальный)
        flatType.setFlatType(data.getFlatType()); //Тип квартиры
        flatType.setOwners(data.getOwners()); //Собственники
    }

    /**
     * Создаем жителя квартиры.
     *
     * @param man      данные по жителю
     * @param flatId   id квартиры
     * @param datalist smev сообщение, для определения собственника
     * @param unom     unom дома
     */
    private void createPerson(Datalist.People.Man man, String flatId, Datalist datalist, String unom) {
        PersonType personType = new PersonType();
        personType.setRelType(man.getRelType());

        //В частной собственности или нет
        if (nonNull(man.getRegType())) {
            if (man.getRegType().toString().trim().equals("1")) {
                personType.setRegType("постоянно");
            } else if (man.getRegType().toString().trim().equals("2")
                || man.getRegType().toString().trim().equals("3")) {
                personType.setRegType("временно");
            } else if (man.getRegType().toString().trim().equals("4")) {
                personType.setRegType("бухгалтерия");
            }
        }

        personType.setRegFrom(man.getRegFrom());
        if (isNotEmpty(man.getRegDate())) {
            try {
                personType.setRegDate(LocalDate.parse(man.getRegDate(), FORMATTER));
            } catch (DateTimeParseException e) {
                log.error("Пришел кривой ответ АСУР, вместо даты man.RegDate пришло: {}", man.getRegDate());
            }

        }
        personType.setUpdatedFromEZDdate(LocalDate.now());
        personType.setPassportPrior(man.getPassportPrior());
        personType.setPassport(man.getPassport());
        personType.setFIO(man.getFio());
        personType.setBirthLocation(man.getBirthLocation());
        if (isNotEmpty(man.getBirthDate())) {
            try {
                personType.setBirthDate(LocalDate.parse(man.getBirthDate(), FORMATTER));
            } catch (DateTimeParseException e) {
                log.error("Пришел кривой ответ АСУР, вместо даты man.BirthDate пришло: {}", man.getBirthDate());
            }
        }
        personType.setArriveMoscow(man.getArriveMoscow());

        personType.setFlatID(flatId);
        personType.setUNOM(new BigInteger(unom));

        //определение собственника
        ownerDefinition(personType, datalist);

        Person person = new Person();
        person.setPersonData(personType);
        PersonDocument personDocument = new PersonDocument();
        personDocument.setDocument(person);
        PersonDocument document = personDocumentService.createDocument(personDocument, true, "");
        log.debug("Created Person with permanent registration: personDocumentId = {}", document.getId());
        disabledPersonService.bindDisabledPersonWithPerson(document);
        //отправим запрос на получение СНИЛС, если это не тест
        if (!flatId.equals("testValue23")) {
            // проверим дату рождения и если она меньше 1801 года, то проставляем статус обогащения
            PersonType personData = document.getDocument().getPersonData();
            if (nonNull(personData.getBirthDate()) && personType.getBirthDate().getYear() > 1800) {
                log.info("updateSnilsFromPfr: createPerson");
                integrationService.updateSnilsFromPfr(personDocument);
            } else {
                personData.setUpdatedFromPFRdate(LocalDate.now());
                personData.setUpdatedFromPFRstatus(
                    "Невалидная дата рождения (ранее 1801г.), запрос в ПФР не может быть отправлен"
                );
                personData.setUpdatedFromELKdate(LocalDate.now());
                personData.setUpdatedFromELKstatus("обогащено");

                personDocumentService.updateDocument(document.getId(), document, true, true, "");
            }
        }

    }

    /**
     * Определение собственник ли житель и просталвение доли.
     *
     * @param person   житель
     * @param datalist smev сообщение
     */
    private void ownerDefinition(PersonType person, Datalist datalist) {
        String fio = person.getFIO();
        int count = 0;
        for (Datalist.People.Man man : datalist.getPeople().getMan()) {
            if (man.getFio().equalsIgnoreCase(fio)) {
                count++;
            }
        }
        if (count > 1) {
            return;
        }

        if (nonNull(datalist.getOwners())) {
            for (Datalist.Owners.Owner owner : datalist.getOwners().getOwner()) {
                if (nonNull(owner.getFio()) && !owner.getFio().isEmpty()) {
                    StringBuilder ownerFio = new StringBuilder();
                    for (String s : owner.getFio()) {
                        ownerFio.append(s).append(" ");
                    }
                    if (ownerFio.toString().trim().equalsIgnoreCase(fio)) {
                        person.setOwner(true);
                        person.setOwnPart(
                            nonNull(owner.getOnePart()) && !owner.getOnePart().isEmpty()
                                ? owner.getOnePart().get(0).toString()
                                : null
                        );
                        try {
                            person.setOwnPerc(
                                nonNull(owner.getOwnPerc()) && !owner.getOwnPerc().isEmpty()
                                    ? Float.parseFloat(
                                    owner.getOwnPerc().get(0).trim().replaceAll(",", ".")
                                )
                                    : null
                            );
                        } catch (NumberFormatException e) {
                            log.error(e.toString());
                        }
                        return;
                    }
                }
            }
            person.setOwner(false);
        } else if (nonNull(datalist.getData().getOwners())) {
            String[] owners = datalist.getData().getOwners().split(",");
            for (String owner : owners) {
                if (owner.equalsIgnoreCase(fio)) {
                    person.setOwner(true);
                    return;
                }
            }
            person.setOwner(false);
        }
    }

    /**
     * Получить строку по xPath.
     *
     * @param xml        исходная xml
     * @param expression путь до значения
     * @return значение
     */
    private static String getStringByXpath(String xml, String expression) {
        try {
            InputSource inputXml = new InputSource(new StringReader(xml));
            XPath path = XPathFactory.newInstance().newXPath();
            return path.compile(expression).evaluate(inputXml);
        } catch (XPathExpressionException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
