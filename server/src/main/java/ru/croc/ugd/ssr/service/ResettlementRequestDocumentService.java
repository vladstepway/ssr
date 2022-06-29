package ru.croc.ugd.ssr.service;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.db.dao.ResettlementRequestDao;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.model.Task;
import ru.reinform.cdp.bpm.model.VariableCondition;
import ru.reinform.cdp.bpm.model.VariableConditionOperationEnum;
import ru.reinform.cdp.bpm.model.VariableTypeEnum;
import ru.reinform.cdp.bpm.service.BpmRuntimeService;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис по работе с БП сущностью расселения.
 */
@Service
@AllArgsConstructor
public class ResettlementRequestDocumentService extends AbstractDocumentService<ResettlementRequestDocument> {

    private final BpmRuntimeService bpmRuntimeService;
    private final ResettlementRequestDao resettlementRequestDao;

    @Nonnull
    @Override
    public DocumentType<ResettlementRequestDocument> getDocumentType() {
        return SsrDocumentTypes.RESETTLEMENT_REQUEST;
    }

    /**
     * Создание документа по БП.
     *
     * @param notes Комментарий к операции
     * @return документ
     */
    @Nonnull
    @Transactional
    public String createDocumentWithProcess(
        @ApiParam(value = "Комментарий к операции") @RequestParam(required = false) String notes
    ) {
        ResettlementRequestDocument document = new ResettlementRequestDocument();
        ResettlementRequest resettlementRequest = new ResettlementRequest();
        ResettlementRequestType main = new ResettlementRequestType();
        resettlementRequest.setMain(main);
        document.setDocument(resettlementRequest);

        main.setStatus("Активна");

        ResettlementRequestDocument requestDocument = createDocument(document, true, notes);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return startDocumentProcess(requestDocument, username, notes);
    }

    @Nonnull
    private String startDocumentProcess(
        @Nonnull ResettlementRequestDocument document,
        String applicant,
        @Nullable String notes
    ) {
        try {
            List<FormProperty> variables = new ArrayList<>();
            variables.add(new FormProperty("EntityIdVar", document.getId()));
            variables.add(new FormProperty("applicant", applicant));
            ProcessInstance process = bpmRuntimeService.startProcessViaForm(
                "ugdssr_objectResettlementRequest",
                variables
            );
            VariableCondition variableCondition = new VariableCondition();
            variableCondition.setOperation(VariableConditionOperationEnum.EQUALS);
            variableCondition.setType(VariableTypeEnum.STRING);
            variableCondition.setName("EntityIdVar");
            variableCondition.setValue(document.getId());

            List<Task> taskList = bpmRuntimeService.findTasks(
                "ugdssr_objectResettlementRequest%",
                "objectChoosingId",
                Collections.singletonList(variableCondition),
                null,
                false
            );
            return taskList.get(0).getId();
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}(notes: {1})", RIExceptionUtils.method(), notes)
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

    public Optional<String> getCipIdForPerson(final PersonType person) {
        final Optional<String> unomOpt = Optional.ofNullable(person)
            .map(PersonType::getUNOM)
            .map(BigInteger::toString);
        final String personFlatId = Optional.ofNullable(person)
            .map(PersonType::getFlatID)
            .orElse(null);
        if (unomOpt.isPresent()) {
            final List<HouseToSettle> housesToSettle = getHousesToSettle(unomOpt.get());
            return StreamUtils.or(
                getCipIdByFlatId(housesToSettle, personFlatId),
                () -> getCipIdWithoutFlat(housesToSettle));
        }
        return Optional.empty();
    }

    public Optional<String> getCipIdForPerson(final PersonDocument personDocument) {
        return Optional.ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .flatMap(this::getCipIdForPerson);
    }

    private Optional<String> getCipIdWithoutFlat(final List<HouseToSettle> housesToSettle) {
        return housesToSettle
            .stream()
            .filter(this::containsEmptyFlatsToResettle)
            .map(HouseToSettle::getInformationCenterCode)
            .filter(Objects::nonNull)
            .findFirst();
    }

    private boolean containsEmptyFlatsToResettle(final HouseToSettle houseToSettle) {
        return houseToSettle
            .getHousesToResettle()
            .stream()
            .anyMatch(houseToResettle -> CollectionUtils.isEmpty(houseToResettle.getFlats()));
    }

    private Optional<String> getCipIdByFlatId(final List<HouseToSettle> housesToSettle, final String personFlatId) {
        return housesToSettle
            .stream()
            .filter(houseToSettle -> containsFlatToResettle(houseToSettle, personFlatId))
            .map(HouseToSettle::getInformationCenterCode)
            .findFirst();
    }

    private boolean containsFlatToResettle(final HouseToSettle houseToSettle, final String flatId) {
        return houseToSettle
            .getHousesToResettle()
            .stream()
            .anyMatch(houseToResettle -> houseToResettle.getFlats()
                .stream()
                .anyMatch(id -> StringUtils.equals(id, flatId))
            );
    }

    private List<HouseToSettle> getHousesToSettle(final String unom) {
        return resettlementRequestDao
            .fetchResettlementRequestByUnom(unom)
            .stream()
            .map(this::parseDocumentData)
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .filter(Objects::nonNull)
            .map(ResettlementRequestType::getHousesToSettle)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private ResettlementRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private ResettlementRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, ResettlementRequestDocument.class);
    }

    public List<ResettlementRequestDocument> fetchAllByCipId(final String cipId) {
        return resettlementRequestDao
            .fetchResettlementRequestByCipId(cipId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<ResettlementRequestDocument> fetchAllByRealEstateUnom(final String realEstateUnom) {
        return resettlementRequestDao.fetchResettlementRequestByUnom(realEstateUnom)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
