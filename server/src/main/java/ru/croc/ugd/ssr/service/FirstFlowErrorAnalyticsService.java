package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FirstFlowError;
import ru.croc.ugd.ssr.FirstFlowErrorAnalyticsData;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.integration.service.IntegrationService;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Сервис по работе с БП сущностью обработки ошибок 1 потока.
 */
@Service
@AllArgsConstructor
public class FirstFlowErrorAnalyticsService extends AbstractDocumentService<FirstFlowErrorAnalyticsDocument> {

    private static final String PROCESS_FIRST_FLOW_ERROR_ANALYTICS_KEY = "ugdssr_firstFlowErrorAnalytics";

    private static final Logger LOG = LoggerFactory.getLogger(FirstFlowErrorAnalyticsService.class);

    private final BpmService bpmService;
    private final PersonDocumentService personDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final PersonInfoMfrFlowService personInfoMfrFlowService;
    private final IntegrationService integrationService;

    @Nonnull
    @Override
    public DocumentType<FirstFlowErrorAnalyticsDocument> getDocumentType() {
        return SsrDocumentTypes.FIRST_FLOW_ERROR_ANALYTICS;
    }

    /**
     * Запустить новый процесс.
     *
     * @param id ИД документа
     * @return ИД запущенного процесса
     */
    public Optional<String> startDocumentProcess(final String id) {
        final Map<String, String> variablesMap = Collections.singletonMap(
            BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, id
        );

        return ofNullable(bpmService.startNewProcess(PROCESS_FIRST_FLOW_ERROR_ANALYTICS_KEY, variablesMap));
    }

    /**
     * Обработка документа.
     *
     * @param id ид документа
     */
    @Async
    public void handleDocument(@Nonnull String id) {
        LOG.info("saveFirstFlowErrorAnalysis запущен");

        FirstFlowErrorAnalyticsDocument document = fetchDocument(id);
        FirstFlowErrorAnalyticsData data = document.getDocument().getData();
        for (FirstFlowError error : data.getErrors()) {

            String personId = error.getPersonId();
            PersonDocument personDocument = personDocumentService.fetchDocument(personId);
            PersonType personData = personDocument.getDocument().getPersonData();
            FirstFlowError.Result result = error.getResult();
            FirstFlowError.Flat flat = error.getFlat();
            FirstFlowError.Message message = error.getMessage();

            if (StringUtils.isNotBlank(result.getAffairId())) {
                personData.setAffairId(result.getAffairId());
            }
            personData.setCadNum(result.getCadastralNumber());

            PersonType.AddInfo addInfo = personData.getAddInfo() == null
                ? new PersonType.AddInfo() : personData.getAddInfo();
            addInfo.setDelReason(result.getDelReason());
            addInfo.setIsDead(result.getIsDead());
            personData.setAddInfo(addInfo);

            personData.setEncumbrances(result.getEncumbrances());
            personData.setFirstName(result.getFirstName());
            personData.setLastName(result.getLastName());
            personData.setMiddleName(result.getMiddleName());
            personData.setFIO(getFio(
                result.getFirstName(), result.getLastName(), result.getMiddleName()
            ));

            boolean dgiIsCommun = false;
            boolean resultIsCommun = false;
            if (StringUtils.isNotBlank(result.getFlat()) && StringUtils.isNotBlank(result.getUnom())) {
                RealEstateDocument realEstateDocument
                    = realEstateDocumentService.fetchDocumentByUnom(result.getUnom());
                RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
                RealEstateDataType.Flats flats = realEstateData.getFlats() == null
                    ? new RealEstateDataType.Flats() : realEstateData.getFlats();
                realEstateData.setFlats(flats);
                Optional<FlatType> any = flats.getFlat()
                    .stream()
                    .filter(f -> result.getFlat().equalsIgnoreCase(f.getApartmentL4VALUE()))
                    .findAny();
                FlatType flatType;
                if (any.isPresent()) {
                    flatType = any.get();
                    personData.setFlatID(any.get().getFlatID());
                } else {
                    flatType = new FlatType();
                    flats.getFlat().add(flatType);

                    flatType.setFlatID(UUID.randomUUID().toString());
                    personData.setFlatID(flatType.getFlatID());

                    flatType.setFlatNumber(result.getFlat());
                    flatType.setApartmentL4VALUE(result.getFlat());
                    flatType.setNote("DGI");
                }
                dgiIsCommun = flat.getSnosRoomsNum() != null && flat.getSnosRoomsNum().size() > 0;
                resultIsCommun = result.isCommun();
                flatType.setFlatType(result.isCommun() ? "Коммунальная" : "Отдельная");
                realEstateDocumentService.updateDocument(
                    realEstateDocument.getId(),
                    realEstateDocument,
                    true,
                    true,
                    "dgi"
                );
                personData.setFlatNum(result.getFlat());
            }

            personData.setWaiter(result.getIsQueue());
            if (StringUtils.isNotBlank(result.getPersonId())) {
                personData.setPersonID(result.getPersonId());
            }

            PersonType.AddFlatInfo addFlatInfo = personData.getAddFlatInfo() == null
                ? new PersonType.AddFlatInfo() : personData.getAddFlatInfo();
            addFlatInfo.setInCourt(result.getInCourt());
            addFlatInfo.setOwnFederal(result.getIsFederal());
            addFlatInfo.setNoFlat(result.getNoFlat());
            personData.setAddFlatInfo(addFlatInfo);

            personData.getRoomNum().clear();
            personData.getRoomNum().addAll(result.getRoomsNum()
                .stream()
                .filter(r -> !personData.getRoomNum().contains(r))
                .collect(Collectors.toList()));

            personData.setGender(result.getSex());
            if (StringUtils.isNotBlank(result.getSnils())) {
                personData.setSNILS(result.getSnils());
            }
            if (StringUtils.isNotBlank(result.getStatusLiving())) {
                personData.setStatusLiving(result.getStatusLiving());
            }
            if (StringUtils.isNotBlank(result.getUnom())) {
                personData.setUNOM(new BigInteger(result.getUnom()));
            } else {
                personData.setUNOM(null);
            }
            personData.setBirthDate(result.getBirthDate());

            personData.setUpdatedFromDgiStatus(2);
            personData.setUpdatedFromDgiDate(LocalDate.now());

            if (StringUtils.isNotBlank(error.getSolution()) && "Удалить".equals(error.getSolution().trim())) {
                personData.setIsArchive(true);
            } else {
                boolean theSameFlag; // флаг, было ли хоть одно обновление
                theSameFlag = compareStringsWithNotNull(flat.getAffairId(), result.getAffairId());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(flat.getSnosCadnum(), result.getCadastralNumber());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(message.getDelReason(), result.getDelReason());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(message.getIsDead(), result.getIsDead());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(flat.getEncumbrances(), result.getEncumbrances());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(message.getFirstName(), result.getFirstName());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(message.getLastName(), result.getLastName());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(message.getMiddleName(), result.getMiddleName());
                theSameFlag = theSameFlag && dgiIsCommun == resultIsCommun;
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(flat.getSnosFlatNum(), result.getFlat());
                theSameFlag = theSameFlag && compareStringsWithNotNull(message.getIsQueue(), result.getIsQueue());
                theSameFlag = theSameFlag && compareStringsWithNotNull(message.getPersonId(), result.getPersonId());
                theSameFlag = theSameFlag && compareStringsWithNotNull(flat.getInCourt(), result.getInCourt());
                theSameFlag = theSameFlag && compareStringsWithNotNull(flat.getIsFederal(), result.getIsFederal());
                theSameFlag = theSameFlag && compareStringsWithNotNull(flat.getNoFlat(), result.getNoFlat());

                Collections.sort(flat.getSnosRoomsNum()); // для сравнения
                Collections.sort(result.getRoomsNum());
                theSameFlag = theSameFlag && flat.getSnosRoomsNum().equals(result.getRoomsNum());
                theSameFlag = theSameFlag && compareStringsWithNotNull(message.getSex(), result.getSex());
                theSameFlag = theSameFlag && compareStringsWithNotNull(message.getSnils(), result.getSnils());
                theSameFlag = theSameFlag
                    && compareStringsWithNotNull(flat.getStatusLiving(), result.getStatusLiving());
                theSameFlag = theSameFlag && compareStringsWithNotNull(flat.getSnosUnom(), result.getUnom());
                theSameFlag = theSameFlag && compareDatesWithNotNull(message.getBirthDate(), result.getBirthDate());
                if (theSameFlag) {
                    personData.setUpdatedFromDgiStatus(2);
                } else {
                    personData.setUpdatedFromDgiStatus(3);
                }
                personData.setUpdatedFromDgiDate(LocalDate.now());
            }
            personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
        }

        List<PersonDocument> personDocuments = personDocumentService.fetchByUnom(data.getUnom())
            .stream()
            .filter(p -> !p.getDocument().getPersonData().isIsArchive())
            .collect(Collectors.toList());

        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(data.getUnom());
        if (personDocuments.stream().allMatch(p -> p.getDocument().getPersonData().getUpdatedFromDgiStatus() == 2)) {
            realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiStatus("Обогащено");
            realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiDate(LocalDate.now());
        } else {
            realEstateDocument.getDocument().getRealEstateData().setUpdatedFromDgiStatus("Не обогащено");
        }
        realEstateDocumentService.updateDocument(realEstateDocument.getId(), realEstateDocument, true, true, "");

        // удалим жителей-пустышек (без UNOM), которые были созданы для обработки задачи
        List<String> emptyPersonIds = personDocumentService.fetchByFirstFlowErrorAnalyticsId(id);
        emptyPersonIds.forEach(personId ->
            personDocumentService.deleteDocument(personId, false, "delete empty person")
        );

        updateAllPersonsWithoutSnilsOrGender(personDocuments);

        personInfoMfrFlowService.sendPersonInfo(personDocuments);
        LOG.info("saveFirstFlowErrorAnalysis выполнен");
    }

    private void updateAllPersonsWithoutSnilsOrGender(final List<PersonDocument> personDocuments) {
        LOG.info("updateSnilsFromPfr: updateAllPersonsWithoutSnilsOrGender");
        personDocuments.stream()
            .filter(person -> StringUtils.isBlank(person.getDocument().getPersonData().getSNILS())
                || StringUtils.isBlank(person.getDocument().getPersonData().getGender())
            )
            .forEach(integrationService::updateSnilsFromPfr);
    }

    private boolean compareStringsWithNotNull(String s1, String s2) {
        return StringUtils.isBlank(s1) && StringUtils.isBlank(s2)
            || StringUtils.isNotBlank(s1) && StringUtils.isNotBlank(s2) && s1.equalsIgnoreCase(s2);
    }

    private boolean compareDatesWithNotNull(LocalDate d1, LocalDate d2) {
        return d1 == null && d2 == null
            || d1 != null && d1.equals(d2);
    }

    @NotNull
    private String getFio(String firstName, String lastName, String middleName) {
        String fio = lastName + " " + firstName;
        fio += StringUtils.isNotBlank(middleName) ? " " + middleName : "";
        return fio;
    }

}
