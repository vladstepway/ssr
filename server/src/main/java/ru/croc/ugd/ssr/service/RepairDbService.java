package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FirstFlowError;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.service.flows.PersonsUpdateFlowsService;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис по восстановлению БД.
 */
@Service
@AllArgsConstructor
public class RepairDbService {

    private static final Logger LOG = LoggerFactory.getLogger(RepairDbService.class);

    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService;
    private final PersonsUpdateFlowsService personsUpdateFlowsService;

    /**
     * Удаление дублирующих квартир в домах.
     */
    public void deleteDoublesFlats() {
        List<String> unomsWithDoublesFlats = getIdsWithDoublesFlats();
        for (String id : unomsWithDoublesFlats) {
            LOG.info("Начало обработки дома с квартирами-дублями, id: {}", id);
            RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocument(id);
            ArrayList<String> flatsNumbers = new ArrayList<>();
            List<FlatType> flats = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
            List<FlatType> flatsToRemove = new ArrayList<>();
            for (FlatType flat : flats) {
                if (!flatsNumbers.contains(flat.getApartmentL4VALUE())) {
                    flatsNumbers.add(flat.getApartmentL4VALUE());
                } else {
                    LOG.info("Обнаружена квартира-дубль, id: {}. Удаляем", flat.getFlatID());
                    List<PersonDocument> personDocuments = personDocumentService.fetchByFlatId(flat.getFlatID());
                    if (nonNull(personDocuments) && !personDocuments.isEmpty()) {
                        LOG.info("Удаляем жителей, привязанных к квартире");
                        for (PersonDocument personDocument : personDocuments) {
                            LOG.info("Удаление жителя с id: {}", personDocument.getId());
                            personDocumentService.deleteDocument(personDocument.getId(), true, "");
                        }
                    }
                    flatsToRemove.add(flat);
                }
            }
            for (FlatType flatType : flatsToRemove) {
                flats.remove(flatType);
            }
            realEstateDocumentService.updateDocument(
                id, realEstateDocument, true, true, ""
            );
            LOG.info("Дом с квартирами-дублями обработан, id: {}", id);
        }
    }

    /**
     * Удаление комнат из домов.
     */
    @Async
    public void deleteRooms() {
        LOG.info("Запуск процедуры удаления комнат");
        LOG.info("Начинаем поиск домов с комнатами");
        long countDocuments = realEstateDocumentService.countDocuments();
        int totalRepair = 0;
        for (int i = 0; i <= countDocuments / 100; i++) {
            List<RealEstateDocument> realEstateDocuments = getRealEstatePage(i);
            for (RealEstateDocument realEstateDocument : realEstateDocuments) {
                List<FlatType> flats = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
                List<FlatType> flatsWithRooms = flats.stream().filter(
                    flat ->
                        nonNull(flat.getRooms())
                            && nonNull(flat.getRooms().getRoom())
                            && flat.getRooms().getRoom().size() > 0
                ).collect(Collectors.toList());
                if (flatsWithRooms.size() > 0) {
                    LOG.info(
                        "Найден дом с комнатами, уном: {}. Удаляем комнаты.",
                        realEstateDocument.getDocument().getRealEstateData().getUNOM()
                    );
                    for (FlatType flat : flatsWithRooms) {
                        flat.getRooms().getRoom().clear();
                        LOG.debug("Квартира под номером {} обработана", flat.getApartmentL4VALUE());
                    }
                    realEstateDocumentService.updateDocument(
                        realEstateDocument.getId(), realEstateDocument, true, true, "repairDB"
                    );
                    totalRepair++;
                    LOG.info(
                        "Дом с комнатами, уном: {} обработан.",
                        realEstateDocument.getDocument().getRealEstateData().getUNOM()
                    );
                }
            }
        }
        LOG.info("Процедура удаления комнат завершена");
        LOG.info("Обработано домов: {}", totalRepair);
    }

    /**
     * Сопоставление жителей ДГП и ДГИ в задачах на разбор.
     */
    @Async
    public void firstFlowErrorFix() {
        LOG.info("Запуск процедуры сопоставления жителей");
        LOG.info("Начинаем поиск задач на разбор");
        long countDocuments = firstFlowErrorAnalyticsService.countDocuments();
        int totalRepair = 0;
        for (int i = 0; i <= countDocuments / 100; i++) {
            List<FirstFlowErrorAnalyticsDocument> errorAnalyticsDocuments = getFirstFlowErrorAnalyticsPage(i);
            for (FirstFlowErrorAnalyticsDocument errorDocument : errorAnalyticsDocuments) {
                List<FirstFlowError> errors = errorDocument.getDocument().getData().getErrors();
                String unom = errorDocument.getDocument().getData().getUnom();
                RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
                for (FirstFlowError error : errors.stream()
                        .filter(e -> nonNull(e.getMessage()) && nonNull(e.getFlat()))
                        .filter(e -> StringUtils.isNotBlank(e.getPersonId()))
                        .filter(e -> StringUtils.isBlank(e.getSolution()))
                        .collect(Collectors.toList())) {
                    try {
                        PersonDocument personDocument = personDocumentService.fetchDocument(error.getPersonId());
                        PersonType personData = personDocument.getDocument().getPersonData();

                        if (Objects.isNull(personData.getUNOM())) {
                            // найден житель из ДГИ, не сопоставленный с ДГП
                            // теперь проверим, должен ли он был сопоставиться

                            for (FirstFlowError errorToFix : errors.stream()
                                    .filter(e -> Objects.isNull(e.getMessage()) && Objects.isNull(e.getFlat()))
                                    .filter(e -> StringUtils.isNotBlank(e.getPersonId()))
                                    .filter(e -> StringUtils.isBlank(e.getSolution()))
                                    .collect(Collectors.toList())) {
                                try {
                                    PersonDocument personDocumentToFix = personDocumentService
                                            .fetchDocument(errorToFix.getPersonId());
                                    PersonType personDataToFix = personDocumentToFix.getDocument().getPersonData();

                                    boolean result = (StringUtils.isNotBlank(personDataToFix.getFirstName())
                                            && StringUtils.isNotBlank(personDataToFix.getMiddleName())
                                            && StringUtils.isNotBlank(personDataToFix.getLastName())
                                            && personDataToFix.getFirstName()
                                            .equalsIgnoreCase(error.getMessage().getFirstName())
                                            && personDataToFix.getMiddleName()
                                            .equalsIgnoreCase(error.getMessage().getMiddleName())
                                            && personDataToFix.getLastName()
                                            .equalsIgnoreCase(error.getMessage().getLastName()));
                                    result = result
                                            && Objects.nonNull(personDataToFix.getBirthDate())
                                            && personDataToFix.getBirthDate().equals(error.getMessage().getBirthDate());
                                    result = result && error.getFlat().getSnosFlatNum()
                                            .equals(personDataToFix.getFlatNum());

                                    if (result) {
                                        // эти жители должны были сопоставиться - исправляем
                                        LOG.info("Найдены жители, которые должны были сопоставиться. Исправляем.");
                                        final String personIdToDelete = error.getPersonId();
                                        error.setPersonId(errorToFix.getPersonId());
                                        errors.remove(errorToFix);

                                        // обновим информацию по жителю из ДГИ
                                        personDataToFix.setWaiter(error.getMessage().getIsQueue());
                                        personDataToFix.setPersonID(error.getMessage().getPersonId());
                                        personDataToFix.setAffairId(error.getFlat().getAffairId());
                                        personDataToFix.setStatusLiving(error.getFlat().getStatusLiving());
                                        personDataToFix.setEncumbrances(error.getFlat().getEncumbrances());

                                        List<String> roomNum = personDataToFix.getRoomNum();
                                        List<String> snosRoomsNum = error.getFlat().getSnosRoomsNum()
                                                .stream()
                                                .filter(Objects::nonNull)
                                                .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
                                                .distinct()
                                                .collect(Collectors.toList());
                                        roomNum.clear();
                                        roomNum.addAll(snosRoomsNum);

                                        PersonType.AddFlatInfo addFlatInfo = personDataToFix.getAddFlatInfo() == null
                                                ? new PersonType.AddFlatInfo() : personDataToFix.getAddFlatInfo();
                                        personDataToFix.setAddFlatInfo(addFlatInfo);
                                        addFlatInfo.setNoFlat(error.getFlat().getNoFlat());
                                        addFlatInfo.setOwnFederal(error.getFlat().getIsFederal());
                                        addFlatInfo.setInCourt(error.getFlat().getInCourt());

                                        PersonType.AddInfo addInfo = personDataToFix.getAddInfo() == null
                                                ? new PersonType.AddInfo() : personDataToFix.getAddInfo();
                                        personDataToFix.setAddInfo(addInfo);
                                        addInfo.setIsDead(error.getMessage().getIsDead());

                                        // если все сошлось, то обновим и удалим ошибку
                                        if (compareDocument(error, personDataToFix, realEstateDocument)) {
                                            if (StringUtils.isNotBlank(error.getMessage().getChangeStatus())
                                                    && "Удален".equals(error.getMessage().getChangeStatus().trim())) {
                                                refreshResult(error, personDataToFix, realEstateDocument);
                                                personDataToFix.setUpdatedFromDgiStatus(null);
                                                personDataToFix.setUpdatedFromDgiDate(null);
                                            } else {
                                                personDataToFix.setUpdatedFromDgiStatus(2);
                                                personDataToFix.setUpdatedFromDgiDate(LocalDate.now());

                                                if (StringUtils.isNotBlank(error.getMessage().getFirstName())) {
                                                    personDataToFix.setFirstName(error.getMessage().getFirstName());
                                                }
                                                if (StringUtils.isNotBlank(error.getMessage().getLastName())) {
                                                    personDataToFix.setLastName(error.getMessage().getLastName());
                                                }
                                                if (StringUtils.isNotBlank(error.getMessage().getMiddleName())) {
                                                    personDataToFix.setMiddleName(error.getMessage().getMiddleName());
                                                }
                                                personDataToFix.setFIO(
                                                        personsUpdateFlowsService.getFio(
                                                                personDataToFix.getFirstName(),
                                                                personDataToFix.getLastName(),
                                                                personDataToFix.getMiddleName()
                                                        )
                                                );
                                                errors.remove(error);
                                            }
                                        } else {
                                            refreshResult(error, personDataToFix, realEstateDocument);
                                        }

                                        personDocumentService.updateDocument(
                                                personDocumentToFix.getId(),
                                                personDocumentToFix,
                                                true,
                                                true,
                                                "db repair"
                                        );

                                        personDocumentService.deleteDocument(personIdToDelete, false, "db repair");

                                        totalRepair++;
                                        break;
                                    }
                                } catch (Exception ex) {
                                    LOG.error(ex.getLocalizedMessage());
                                }
                            }
                        }
                        firstFlowErrorAnalyticsService.updateDocument(
                                errorDocument.getId(),
                                errorDocument,
                                true,
                                true,
                                "db repair"
                        );
                    } catch (Exception ex) {
                        LOG.error(ex.getLocalizedMessage());
                    }
                }

            }
        }
        LOG.info("Процедура сопоставления жителей завершена");
        LOG.info("Обработано ошибок: {}", totalRepair);
    }

    private void refreshResult(
            FirstFlowError error,
            PersonType personDataToFix,
            RealEstateDocument realEstateDocument
    ) {
        FlatType flatType = null;
        if (realEstateDocument.getDocument().getRealEstateData().getFlats() != null) {
            Optional<FlatType> optFlatType = realEstateDocument.getDocument()
                    .getRealEstateData()
                    .getFlats()
                    .getFlat()
                    .stream()
                    .filter(f -> StringUtils.isNotBlank(f.getFlatID()))
                    .filter(f -> f.getFlatID().equals(personDataToFix.getFlatID()))
                    .findAny();
            if (optFlatType.isPresent()) {
                flatType = optFlatType.get();
            }
        }

        personsUpdateFlowsService.presetValue(
                personDataToFix.getCadNum(),
                error.getFlat() != null ?  error.getFlat().getSnosCadnum() : null,
                error.getResult()::setCadastralNumber
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getAffairId(),
                error.getFlat() != null ?  error.getFlat().getAffairId() : null,
                error.getResult()::setAffairId
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getEncumbrances(),
                error.getFlat() != null ?  error.getFlat().getEncumbrances() : null,
                error.getResult()::setEncumbrances
        );

        List<String> roomsNum;
        if (error.getFlat() != null) {
            roomsNum = error.getFlat().getSnosRoomsNum()
                    .stream()
                    .filter(Objects::nonNull)
                    .flatMap(rn -> Arrays.stream(rn.replace(" ", "").split(",")))
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            roomsNum = new ArrayList<>();
        }
        Collections.sort(personDataToFix.getRoomNum());
        Collections.sort(roomsNum);
        if (personDataToFix.getRoomNum().equals(roomsNum)) {
            error.getResult().getRoomsNum().addAll(personDataToFix.getRoomNum());
        } else {
            if (personDataToFix.getRoomNum().size() > 0) {
                error.getResult().getRoomsNum().addAll(personDataToFix.getRoomNum());
            } else if (roomsNum.size() > 0) {
                error.getResult().getRoomsNum().addAll(roomsNum);
            }
        }

        boolean isCommunDgpValue = flatType != null
                && "Коммунальная".equals(flatType.getFlatType());
        boolean isCommunDgiValue = error.getFlat() != null
                && error.getFlat().getSnosRoomsNum() != null
                && error.getFlat().getSnosRoomsNum().size() > 0;
        error.getResult().setCommun(isCommunDgpValue && isCommunDgiValue);

        personsUpdateFlowsService.presetValue(
                personDataToFix.getAddFlatInfo() == null ? null : personDataToFix.getAddFlatInfo().getNoFlat(),
                error.getFlat() != null ? error.getFlat().getNoFlat() : null,
                error.getResult()::setNoFlat
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getAddFlatInfo() == null ? null : personDataToFix.getAddFlatInfo().getOwnFederal(),
                error.getFlat() != null ? error.getFlat().getIsFederal() : null,
                error.getResult()::setIsFederal
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getAddFlatInfo() == null ? null : personDataToFix.getAddFlatInfo().getInCourt(),
                error.getFlat() != null ? error.getFlat().getInCourt() : null,
                error.getResult()::setInCourt
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getPersonID(),
                error.getMessage() != null ? error.getMessage().getPersonId() : null,
                error.getResult()::setPersonId
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getSNILS(),
                error.getMessage() != null ? error.getMessage().getSnils() : null,
                error.getResult()::setSnils
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getGender(),
                error.getMessage() != null ? error.getMessage().getSex() : null,
                error.getResult()::setSex
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getStatusLiving(),
                error.getFlat() != null ? error.getFlat().getStatusLiving() : null,
                error.getResult()::setStatusLiving
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getWaiter(),
                error.getMessage() != null ? error.getMessage().getIsQueue() : null,
                error.getResult()::setIsQueue
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getAddInfo() == null ? null : personDataToFix.getAddInfo().getIsDead(),
                error.getMessage() != null ? error.getMessage().getIsDead() : null,
                error.getResult()::setIsDead
        );
        personsUpdateFlowsService.presetValue(
                personDataToFix.getAddInfo() == null ? null : personDataToFix.getAddInfo().getDelReason(),
                error.getMessage() != null ? error.getMessage().getDelReason() : null,
                error.getResult()::setDelReason
        );
    }

    private List<String> getIdsWithDoublesFlats() {
        LOG.info("Начинаем поиск домов с квартирами-дублями");
        ArrayList<String> doublesUnom = new ArrayList<>();
        long countDocuments = realEstateDocumentService.countDocuments();
        for (int i = 0; i <= countDocuments / 100; i++) {
            List<RealEstateDocument> realEstateDocuments = getRealEstatePage(i);
            for (RealEstateDocument realEstateDocument : realEstateDocuments) {
                List<FlatType> flats = realEstateDocument.getDocument().getRealEstateData().getFlats().getFlat();
                ArrayList<String> flatsNumbers = new ArrayList<>();
                for (FlatType flat : flats) {
                    if (!flatsNumbers.contains(flat.getApartmentL4VALUE())) {
                        flatsNumbers.add(flat.getApartmentL4VALUE());
                    } else {
                        doublesUnom.add(realEstateDocument.getId());
                        break;
                    }
                }
            }
        }
        LOG.info("Найдено домов с квартирами-дублями {}", doublesUnom.size());
        return doublesUnom;
    }

    /**
     * Получить RealEstate постранично 100шт на стр.
     *
     * @param page страница
     * @return список домов
     */
    private List<RealEstateDocument> getRealEstatePage(int page) {
        return realEstateDocumentService.fetchDocumentsPage(page, 100)
            .stream()
            .filter(
                i -> nonNull(i.getDocument().getRealEstateData())
                    && nonNull(i.getDocument().getRealEstateData().getFlats())
            )
            .collect(Collectors.toList());
    }

    /**
     * Получить FirstFlowErrorAnalytics постранично 100шт на стр.
     *
     * @param page страница
     * @return список задач
     */
    private List<FirstFlowErrorAnalyticsDocument> getFirstFlowErrorAnalyticsPage(int page) {
        return firstFlowErrorAnalyticsService.fetchDocumentsPage(page, 100)
                .stream()
                .filter(
                        i -> nonNull(i.getDocument().getData())
                                && nonNull(i.getDocument().getData().getErrors())
                )
                .collect(Collectors.toList());
    }

    private boolean compareDocument(
            FirstFlowError error,
            PersonType personData,
            RealEstateDocument realEstateDocument
    ) {
        boolean result = personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getPersonID(), error.getMessage().getPersonId()
        );
        String unom = personData.getUNOM() == null ? null : personData.getUNOM().toString();
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(unom, error.getFlat().getSnosUnom());
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getCadNum(), error.getFlat().getSnosCadnum()
        );
        result = result
                && personsUpdateFlowsService.compareStringsWithNotNull(
                        personData.getLastName(), error.getMessage().getLastName()
        )
                && personsUpdateFlowsService.compareStringsWithNotNull(
                        personData.getFirstName(), error.getMessage().getFirstName()
        )
                && personsUpdateFlowsService.compareStringsWithNotNull(
                        personData.getMiddleName(), error.getMessage().getMiddleName()
        );
        result = result && personsUpdateFlowsService.compareDatesWithNotNull(
                personData.getBirthDate(), error.getMessage().getBirthDate()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getGender(), error.getMessage().getSex()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getSNILS(), error.getMessage().getSnils()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getAffairId(), error.getFlat().getAffairId()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getCadNum(), error.getFlat().getSnosCadnum()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getWaiter(), error.getMessage().getIsQueue()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getEncumbrances(), error.getFlat().getEncumbrances()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getFlatNum(), error.getFlat().getSnosFlatNum()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                personData.getStatusLiving(), error.getFlat().getStatusLiving()
        );

        List<String> roomNum = personData.getRoomNum();
        List<String> snosRoomsNum = error.getFlat().getSnosRoomsNum();
        Collections.sort(roomNum);
        Collections.sort(snosRoomsNum);
        result = result && roomNum.equals(snosRoomsNum);

        FlatType flatType = RealEstateUtils.findFlat(personData.getFlatID(), realEstateDocument);
        boolean isCommunDgpValue = flatType != null
                && "Коммунальная".equals(flatType.getFlatType());
        boolean isCommunDgiValue = error.getFlat().getSnosRoomsNum() != null
                && error.getFlat().getSnosRoomsNum().size() > 0;
        result = result && isCommunDgpValue == isCommunDgiValue;

        PersonType.AddInfo addInfo = personData.getAddInfo();
        String delReason = addInfo == null ? null : addInfo.getDelReason();
        String isDead = addInfo == null ? null : addInfo.getIsDead();
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                delReason, error.getMessage().getDelReason()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                isDead, error.getMessage().getIsDead()
        );

        PersonType.AddFlatInfo flatInfo = personData.getAddFlatInfo();
        String noFlat = flatInfo == null ? null : flatInfo.getNoFlat();
        String ownFederal = flatInfo == null ? null : flatInfo.getOwnFederal();
        String inCourt = flatInfo == null ? null : flatInfo.getInCourt();
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                noFlat, error.getFlat().getNoFlat()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                ownFederal, error.getFlat().getIsFederal()
        );
        result = result && personsUpdateFlowsService.compareStringsWithNotNull(
                inCourt, error.getFlat().getInCourt()
        );
        return result;
    }

}
