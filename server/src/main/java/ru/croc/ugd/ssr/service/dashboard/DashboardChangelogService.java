package ru.croc.ugd.ssr.service.dashboard;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.DashboardChangelogDto;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.ChangeLogData;
import ru.reinform.cdp.document.service.DocumentLogService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Сервис для работы с логикой журналирования документов.
 */
@Service
public class DashboardChangelogService {

    private final DocumentLogService documentLogService;
    private final UserService userService;
    private final Map<String, String> propertyNameMap;
    private final List<JSONArray> jsonPatchArrays;

    /**
     * Конструктор.
     *
     * @param documentLogService DocumentLogService
     * @param userService        userService
     */
    public DashboardChangelogService(DocumentLogService documentLogService,
            UserService userService) {
        this.documentLogService = documentLogService;
        this.userService = userService;

        this.propertyNameMap = getPropertyNameMap();
        this.jsonPatchArrays = new ArrayList<>();
    }

    /**
     * Возвращает историю изменений документа DASHBOARD.
     *
     * @param docId id дашборда
     * @return массив с историей изменений (колонки: date, event, assignee, comment)
     */
    public List<DashboardChangelogDto> getChangelog(String docId) {
        List<DashboardChangelogDto> result = new ArrayList<>();
        for (ChangeLogData changeLog : documentLogService.findChangeLogDataRows(docId,
                SsrDocumentTypes.DASHBOARD,
                Sort.Direction.ASC)) {
            DashboardChangelogDto dashboardChangelogDto = DashboardChangelogDto.builder()
                .assignee(userService.getUserFioByLogin(changeLog.getUserName()))
                .comment(getCommentByChangeLog(changeLog))
                .date(changeLog.getDateEdit())
                .event(getEventByNotes(changeLog.getNotes()))
                .build();
            result.add(dashboardChangelogDto);
        }
        jsonPatchArrays.clear();
        return result;
    }

    /**
     * Формирует удобочитаемый текст колонки "Событие" по notes.
     *
     * @param notes комментарии при сохранении/изменении записи
     * @return событие (содержимое колонки истории изменений)
     */
    private String getEventByNotes(String notes) {
        if (notes == null || notes.isEmpty()) {
            return "";
        }
        switch (notes) {
            case "bpm create dashboard":
                return "Создана запись";
            case "edit create dashboard":
                return "Запись создана администратором";
            case "bpm dgi update":
                return "Внесена информация ДГИ";
            case "bpm dgp update":
                return "Внесена информация ДГП";
            case "bpm ds update":
                return "Внесена информация ДС";
            case "bpm fond update":
                return "Внесена информация Фонд";
            case "edit dgi update":
                return "Отредактирована информация ДГИ";
            case "edit dgp update":
                return "Отредактирована информация ДГП";
            case "edit ds update":
                return "Отредактирована информация ДС";
            case "edit fond update":
                return "Отредактирована информация Фонд";
            case "bpm publish dashboard":
                return "Запись опубликована";
            case "bpm archive dashboard":
                return "Запись перенесена в архив";
            default:
                break;
        }
        return "";
    }

    /**
     * Формирует удобочитаемый комментарий с изменениями полей в записи.
     *
     * @param changeLog запись ChangeLog
     * @return удобочитаемая строка (содержимое колонки истории изменений)
     */
    private String getCommentByChangeLog(ChangeLogData changeLog) {
        String notes = changeLog.getNotes();

        List<String> propertiesWithValues = new ArrayList<>();
        if (notes != null && (notes.equals("bpm dgi update")
                || notes.equals("bpm dgp update")
                || notes.equals("bpm ds update")
                || notes.equals("bpm fond update")
                || notes.equals("edit dgi update")
                || notes.equals("edit dgp update")
                || notes.equals("edit ds update")
                || notes.equals("edit fond update"))) {
            JSONArray jsonPatchArray = new JSONArray(changeLog.getJsonPatch());
            jsonPatchArrays.add(jsonPatchArray);
            for (int i = 0; i < jsonPatchArray.length(); i++) {
                propertiesWithValues.add(getPropertyWithValueByJsonObject(jsonPatchArray.getJSONObject(
                        i)));
            }

            String join = String.join("; ", propertiesWithValues);
            if (!join.isEmpty()) {
                return join + ".";
            }
        }

        return "";
    }

    /**
     * Формирует удобочитаемую строку со значениям свойства (пример: Количество жильцов: 12).
     *
     * @param jsonObject объект с полями op, path и value
     * @return строка с имененем и значением свойства
     */
    private String getPropertyWithValueByJsonObject(JSONObject jsonObject) {
        String op = jsonObject.getString("op");
        String path = jsonObject.getString("path");
        Object value;

        if (op.equals("copy")) {
            value = getValueFromJsonPath(jsonObject.getString("from"));
        } else {
            value = jsonObject.get("value");
        }

        String propertyName = propertyNameMap.get(path);
        if (propertyName == null) {
            return null;
        }

        return propertyName + ": " + value;
    }

    /**
     * Возвращает значение в объекте по jsonPath.
     *
     * @param from jsonPath
     * @return значение по пути
     */
    private Object getValueFromJsonPath(String from) {
        for (int i = jsonPatchArrays.size() - 1; i >= 0; i--) {
            JSONArray jsonArray = jsonPatchArrays.get(i);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                String op = jsonObject.getString("op");
                String path = jsonObject.getString("path");
                if (path.equals(from)) {
                    if (op.equals("add") || op.equals("replace")) {
                        return jsonObject.get("value");
                    } else if (op.equals("copy")) {
                        from = jsonObject.getString("from");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Получение мапы с сопоставлением jsonPath к наименованию свойства.
     *
     * @return мапа (key - jsonPath, value - наименование свойства)
     */
    private Map<String, String> getPropertyNameMap() {
        Map<String, String> map = new HashMap<>();

        map.put("/dashboard/data/newBuildingsTotalAmount",
                "Всего \"стартовых\" новостроек передано под заселение");
        map.put("/dashboard/data/newBuildingsFlatsInfo/flatsTotalAmount", "Всего квартир");
        map.put("/dashboard/data/newBuildingsFlatsInfo/equivalentApartmentsAmount",
                "Равнозначные квартиры представлены гражданам");
        map.put("/dashboard/data/newBuildingsFlatsInfo/buyInApartmentsAmount",
                "Приобретены гражданами в порядке докупки");
        map.put("/dashboard/data/newBuildingsFlatsInfo/apartmentsWithCompensationAmount",
                "Представлены с компенсацией за меньшую жилую площадь");
        map.put("/dashboard/data/newBuildingsFlatsInfo/offeredApartmentsWithCompensationAmount",
                "Ведется предложение квартир с компенсацией за меньшую жилую площадь");
        map.put("/dashboard/data/newBuildingsFlatsInfo/offeredEquivalentApartmentsAmount",
                "Ведется предложение равнозначных квартир жителям");

        map.put("/dashboard/data/resettlementBuildingsInfo/allBuildingsAmount",
                "Всего домов отселяется");
        map.put("/dashboard/data/resettlementBuildingsInfo/demolishedAmount", "Снесены");
        map.put("/dashboard/data/resettlementBuildingsInfo/preparingForDemolitionAmount",
                "Отселены, готовятся к сносу");

        map.put("/dashboard/data/buildingsInResettlementProcessInfo/residentsResettledAmount",
                "Жители отселены, завершается изъятие нежилых помещений");
        map.put("/dashboard/data/buildingsInResettlementProcessInfo/paperworkCompletedAndFundIssuesAmount",
                "ДГИ завершил оформление документов жителям, остались вопросы Фонда "
                        + "(предоставление квартир с компенсацией за меньшую площадь)");
        map.put("/dashboard/data/buildingsInResettlementProcessInfo/resettlementInProcessAmount",
                "Ведется отселение, завершение планируется в указанном периоде");
        map.put("/dashboard/data/buildingsInResettlementProcessInfo/plannedResettlementCompletionTime",
                "Период планируемого завершения отселения");
        map.put("/dashboard/data/buildingsInResettlementProcessInfo/outsideResidenceResettlementAmount",
                "Переселение вне района проживания (включены в Реновацию + ведется изъятие)");
        map.put("/dashboard/data/buildingsInResettlementProcessInfo/partResettlementInProcessAmount",
                "Ведется частичное переселение");

        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/equivalentApartmentConsentedAmount",
                "Согласия на равнозначные квартиры");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/apartmentWithCompensationConsentedAmount",
                "Согласия на компенсацию и докупку");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/equivalentApartmentProposedAmount",
                "ДГИ предлагает равнозначные квартиры");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/apartmentWithCompensationProposedAmount",
                "Фонд предлагает компенсацию (нет равнозначных)");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/refusedAmount", "Отказы, суды");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/familiesAmount",
                "Количество семей, всего");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/resettledFamiliesAmount",
                "В том числе семей - переехали");
        map.put("/dashboard/data/resettlementBuildingsResidentsInfo/resettledResidentsAmount",
                "Количество человек - переехали");

        map.put("/dashboard/data/tasksInfo/year", "Год");
        map.put("/dashboard/data/tasksInfo/plannedResettledBuildingsAmount",
                "Количество домов (планируется отселить)");
        map.put("/dashboard/data/tasksInfo/actuallyResettledBuildingsRate",
                "Фактически отселено домов, %");
        map.put("/dashboard/data/tasksInfo/plannedResettledResidentsAmount",
                "Количество человек (планируется отселить)");
        map.put("/dashboard/data/tasksInfo/actuallyResettledResidenceRate",
                "Фактически переселено, %");

        return map;
    }

}
