package ru.croc.ugd.ssr.service.changelog;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ugd.ssr.db.dao.ChangelogDao;
import ru.croc.ugd.ssr.db.specification.DocumentLogDataSpecification;
import ru.croc.ugd.ssr.db.specification.SearchCriteria;
import ru.croc.ugd.ssr.db.specification.SearchOperation;
import ru.croc.ugd.ssr.dto.changelog.ChangelogDto;
import ru.croc.ugd.ssr.dto.changelog.ChangelogEvent;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.changelog.processor.ChangelogAttributeDescriptionProcessor;
import ru.reinform.cdp.db.model.ChangeLogData;
import ru.reinform.cdp.db.model.DocumentLogData;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;


/**
 * Сервис для работы с логикой журналирования.
 */
public abstract class ChangelogService {

    private final ChangelogDao dao;
    private final LdapService ldapService;
    private final DictionaryService dictionaryService;
    private final AttributeConverter attributeConverter;

    @Autowired
    private UserService userService;

    /**
     * Список атрибутов.
     */
    private Set<ChangelogAttribute> attributes;

    /**
     * Конструктор.
     * @param dao                 ChangelogDao
     * @param ldapService         ldapService
     * @param dictionaryService   dictionaryService
     * @param attributeConverter  attributeConverter
     */
    public ChangelogService(
        ChangelogDao dao,
        LdapService ldapService,
        DictionaryService dictionaryService,
        AttributeConverter attributeConverter
    ) {
        this.dao = dao;
        this.ldapService = ldapService;
        this.dictionaryService = dictionaryService;
        this.attributeConverter = attributeConverter;
    }

    /**
     * Заполняет атрибуты для анализа из справочника.
     */
    @PostConstruct
    public void afterInit() {
        attributes = attributeConverter.convertNewDictElements(
            dictionaryService.getDictionaryElementsAsServiceUser(getDictName())
        );
    }

    /**
     * Возвращает историю изменений документа PERSON.
     *
     * @param docId                id дашборда
     * @param page                 номер страицы
     * @param pageSize             размер страницы
     * @param dateFrom             фильтр - дата от
     * @param dateTo               фильтр - дата до
     * @param logins               фильтр - логины пользователей
     * @param pathToAttribute      фильтр - Атрибут
     * @return массив с историей изменений
     */
    public Page<ChangelogDto> getChangelog(
        String docId, int page, int pageSize, LocalDate dateFrom, LocalDate dateTo,
        List<String> logins, String pathToAttribute
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by("dateEdit").descending());

        Page<ChangeLogData> logs = this.findDocumentChangelogRows(
            docId,
            pageable,
            dateFrom,
            dateTo,
            logins,
            pathToAttribute
        );
        return logs.map(log -> {
            ChangelogDto changelogDto = new ChangelogDto();
            changelogDto.setDate(log.getDateEdit());
            changelogDto.setAssignee(getAssignee(log));
            changelogDto.setOrganization(getOrganization(log));
            changelogDto.setEvents(getEventsByChangeLog(log));
            return changelogDto;
        });
    }

    /**
     * Получает UserBean-ы всех пользователей, вносивших изменения в документ.
     *
     * @param idDoc id документа
     * @return список пользователей
     */
    public List<UserBean> getUsersWhoChangedDocument(String idDoc) {
        List<String> logins = dao.findLoginsWhoChangedDocument(idDoc);
        logins.remove("anonymousUser"); // удалим анонима, если есть - их быть не должно
        logins.remove("SYSTEM"); // удалим систему, если есть - это не логин
        if (logins.isEmpty()) {
            return Collections.emptyList();
        }

        return ldapService.getUsersByLogin(logins);
    }

    private String getAssignee(ChangeLogData changeLog) {
        String userName = changeLog.getUserName();
        if ("anonymousUser".equals(userName)) {
            return "Анонимный пользователь";
        }
        if ("SYSTEM".equals(userName)) {
            return "Система";
        }
        try {
            UserBean user = userService.getUserBeanByLogin(userName);
            if (!isNull(user)) {
                return user.getDisplayName();
            }
            return "Автоматическое формирование";
        } catch (Exception ex) {
            return "Автоматическое формирование";
        }
    }

    private String getOrganization(ChangeLogData changeLog) {
        String userName = changeLog.getUserName();
        if ("anonymousUser".equals(userName) || "SYSTEM".equals(userName)) {
            return null;
        }
        try {
            UserBean user = userService.getUserBeanByLogin(userName);
            if (!isNull(user)) {
                return user.getDepartmentFullName();
            }
            return "Нет данных";
        } catch (Exception ex) {
            return "Нет данных";
        }
    }

    private List<ChangelogEvent> getEventsByChangeLog(ChangeLogData changeLog) {
        List<ChangelogEvent> list = new ArrayList<>();

        JSONObject oldJson = getOldJsonByChangelogId(changeLog.getId());
        JSONObject newJson = getNewJsonByChangelogId(changeLog.getId());
        String patch = changeLog.getJsonPatch();
        JSONArray jsonPatchArray = nonNull(patch) ? new JSONArray(patch) : null;

        if (isNull(jsonPatchArray)) {
            jsonPatchArray = new JSONArray();
        }
        if (isNull(oldJson) && nonNull(newJson)) {
            // Запись только создана
            final JSONArray finalJsonPatchArray = jsonPatchArray;
            attributes.forEach(attribute -> {
                Object filledAttribute = newJson.optQuery(attribute.getPath());
                if (nonNull(filledAttribute)) {
                    finalJsonPatchArray.put(
                        new JSONObject()
                            .put("op", "add")
                            .put("path", attribute.getPath())
                            .put("value", filledAttribute)
                    );
                }
            });
        }

        List<JSONObject> patches = StreamSupport.stream(jsonPatchArray.spliterator(), false)
            .map(value -> (JSONObject) value)
            .flatMap(this::splitComplexValues)
            .collect(Collectors.toList());

        for (JSONObject jsonObject : patches) {
            ChangelogEvent event = processAttribute(jsonObject, oldJson, attributes);
            if (event.getAttrTitle() != null
                || event.getDescription() != null && event.getDescription().size() > 0) {
                list.add(event);
            }
        }

        list.addAll(
            processCompositeElementIfNeeded(patches, attributes, oldJson, newJson)
        );

        return list;
    }

    /**
     * Разбивает patch с комплексным значением (например, addInfo).
     *
     * @param value Пример: ({"op": "add", "path": "/Person/PersonData/addInfo", "value": {"isDead": "1"}})
     * @return Пример: ({"op": "add", "path": "/Person/PersonData/addInfo/isDead", "value": "1"})
     */
    private Stream<JSONObject> splitComplexValues(JSONObject value) {
        String path = value.getString("path");
        if (isComplex(path)) {
            if (value.has("value")) {
                JSONObject jsonObject = value.getJSONObject("value");
                return jsonObject.keySet()
                    .stream()
                    .map(key -> new JSONObject()
                        .put("op", "add")
                        .put("path", path + "/" + key)
                        .put("value", jsonObject.get(key))
                    );
            } else {
                return null;
            }
        } else {
            return Stream.of(value);
        }
    }

    private ChangelogEvent processAttribute(
        JSONObject patch,
        JSONObject oldJson,
        Set<ChangelogAttribute> attributes
    ) {
        ChangelogEvent changelogEvent = new ChangelogEvent();
        String path = patch.getString("path");
        Optional<ChangelogAttribute> optionalChangelogAttribute = attributes.stream()
            .filter(a -> a.getPath().equals(path))
            .findFirst();
        // обработка атрибута элемента массива
        if (!optionalChangelogAttribute.isPresent()) {
            optionalChangelogAttribute = attributes.stream()
                .filter(a -> a.getPath().contains("/-/"))
                .filter(a -> path.matches(a.getPath().replace("/-/", "/\\d+/")))
                .findFirst();
        }
        // идет обработка массива
        if (!optionalChangelogAttribute.isPresent()) {
            optionalChangelogAttribute = attributes.stream()
                .filter(a -> !a.getPath().contains("/-/")) // не атрибут элемента массива
                .filter(a -> a.getPath().contains("/-")) // элемент массива
                .filter(a -> a.getPath().startsWith(path) // если в массив добавлен первый элемент
                    || path.startsWith(a.getPath().substring(0, a.getPath().lastIndexOf("/-"))))
                .findFirst();
        }
        if (optionalChangelogAttribute.isPresent()) {
            final ChangelogAttribute changelogAttribute = optionalChangelogAttribute.get();
            final ChangelogAttributeDescriptionProcessor attributeProcessor = changelogAttribute.getProcessor();
            if (attributeProcessor.shouldSkipAttribute(oldJson, patch, changelogAttribute.getType())) {
                return changelogEvent;
            }
            final Object oldValue = attributeProcessor.getOldValue(oldJson, patch, changelogAttribute.getType());
            final Object newValue = attributeProcessor.getNewValue(oldJson, patch, changelogAttribute.getType());

            changelogEvent.setAttrName(changelogAttribute.getName());
            changelogEvent.setAttrTitle(changelogAttribute.getTitle());
            changelogEvent.setDescription(
                attributeProcessor.processDescription(patch, oldJson, changelogAttribute.getType())
            );
            changelogEvent.setOldValue(oldValue);
            changelogEvent.setNewValue(newValue);
            changelogEvent.setAttrType(changelogAttribute.getType());
        }
        return changelogEvent;
    }

    private ChangelogEvent createChangelogEvent(
        final ChangelogAttribute changelogAttribute, final JSONObject oldJson, final JSONObject newJson
    ) {
        final ChangelogEvent changelogEvent = new ChangelogEvent();
        final ChangelogAttributeDescriptionProcessor attributeProcessor = changelogAttribute.getProcessor();

        changelogEvent.setAttrName(changelogAttribute.getName());
        changelogEvent.setAttrTitle(changelogAttribute.getTitle());
        changelogEvent.setDescription(
            attributeProcessor.processDescription(oldJson, newJson)
        );
        changelogEvent.setAttrType(changelogAttribute.getType());
        return changelogEvent;
    }

    // Проверка наличия изменения по составному элементу и его обработка
    private List<ChangelogEvent> processCompositeElementIfNeeded(
        final List<JSONObject> patches,
        final Set<ChangelogAttribute> attributes,
        final JSONObject oldJson,
        final JSONObject newJson
    ) {
        return attributes.stream()
            .filter(attribute -> attribute.getPath().endsWith("/*"))
            .filter(attribute -> existsInPatches(attribute.getPath(), patches))
            // при наличии одинаковых процессоров обработка производится однократно
            .collect(Collectors.toMap(ChangelogAttribute::getProcessor, Function.identity(), (a1, a2) -> a1))
            .values()
            .stream()
            .map(attribute -> createChangelogEvent(attribute, oldJson, newJson))
            .collect(Collectors.toList());
    }

    private boolean existsInPatches(final String path, final List<JSONObject> patches) {
        return patches.stream()
            .anyMatch(patch -> patch.getString("path")
                .startsWith(
                    path.substring(0, path.lastIndexOf("/*"))
                )
            );
    }

    /**
     * Получает наименование справочника с атрибутами.
     *
     * @return наименование справочника
     */
    protected abstract String getDictName();

    /**
     * Проверяет, необходимо ли разбивать указанный путь на несколько.
     *
     * @param path путь
     * @return true/false
     */
    private boolean isComplex(String path) {
        return attributes.stream().anyMatch(a ->
            /*
             * Комплексный путь И начинается с этого пути И не равен ему.
             * ПРИМЕР: path = /Person/PersonData/phones
             *   /Person/PersonData/phones/phoneNumber1 начинается с этого пути, но не равен ему.
             *   Поэтому он будет разбит на составные части.
             *   Работает только для add - так как объкт phones: { phoneNumber1: ... } может быть только добавлен
             *   Далее работают стандартные обработчики.
             */
            a.isComplexPath() && a.getPath().startsWith(path) && !a.getPath().equals(path)
        );
    }

    private JSONObject getOldJsonByChangelogId(String changelogId) {
        return dao.getOldJsonObject(changelogId)
            .map(JSONObject::new)
            .orElse(null);
    }

    private JSONObject getNewJsonByChangelogId(String changelogId) {
        return dao.getNewJsonObject(changelogId)
            .map(JSONObject::new)
            .orElse(null);
    }

    private Page<ChangeLogData> findDocumentChangelogRows(
        String idDoc,
        Pageable pageable,
        LocalDate dateFrom,
        LocalDate dateTo,
        List<String> logins,
        String pathToAttribute
    ) {
        DocumentLogDataSpecification spec = new DocumentLogDataSpecification();
        spec.add(new SearchCriteria("idDoc", idDoc, SearchOperation.EQUAL));
        if (dateFrom != null) {
            spec.add(
                new SearchCriteria(
                    "dateEdit",
                    dateFrom.atStartOfDay(),
                    SearchOperation.DATE_TIME_GREATER_THAN_EQUAL
                )
            );
        }
        if (dateTo != null) {
            spec.add(
                new SearchCriteria(
                    "dateEdit",
                    dateTo.atTime(LocalTime.MAX),
                    SearchOperation.DATE_TIME_LESS_THAN_EQUAL
                )
            );
        }
        if (logins != null) {
            spec.add(new SearchCriteria("userName", logins, SearchOperation.IN_STRINGS));
        }
        if (pathToAttribute != null) {
            spec.add(new SearchCriteria("jsonPatch", pathToAttribute, SearchOperation.JSONPATCH_HAS));
        }
        spec.add(
            new SearchCriteria(
                "jsonPatch",
                attributes.stream()
                    .map(ChangelogAttribute::getPath)
                    .collect(Collectors.toList()),
                SearchOperation.JSONPATCH_HAS_ARRAY
            )
        );

        Page<DocumentLogData> rawData = dao.findAll(spec, pageable);
        if (rawData.getTotalElements() == 0) {
            return Page.empty();
        }
        return rawData.map(this::parseDocumentData);
    }

    private ChangeLogData parseDocumentData(@Nonnull DocumentLogData documentLogData) {
        return this.toChangeLog(documentLogData);
    }

    /**
     * Честно взято из DocumentLogService.
     *
     * @param documentLogData данные из DAO
     * @return платформенный ChangeLog
     */
    private ChangeLogData toChangeLog(DocumentLogData documentLogData) {
        ChangeLogData changeLog = new ChangeLogData();
        changeLog.setId(documentLogData.getId());
        changeLog.setIdDoc(documentLogData.getIdDoc());
        changeLog.setDocType(documentLogData.getDocType());
        changeLog.setDateEdit(documentLogData.getDateEdit());
        changeLog.setUserName(documentLogData.getUserName());
        if (documentLogData.getJsonOld() == null && documentLogData.getJsonNew() != null) {
            changeLog.setJsonOnInsert(documentLogData.getJsonNew());
        } else if (documentLogData.getJsonOld() != null && documentLogData.getJsonNew() == null) {
            changeLog.setJsonOnDelete(documentLogData.getJsonOld());
        } else {
            changeLog.setJsonPatch(documentLogData.getJsonPatch());
        }

        changeLog.setNotes(documentLogData.getNotes());
        return changeLog;
    }

}

