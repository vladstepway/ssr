package ru.croc.ugd.ssr.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.model.PersonDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис для формирования тела HTML-сообщения по успешному ооогащению дома из ЕЖД.
 */
@Service
@RequiredArgsConstructor
public class SuccessfullUpdatedStatusStatisticsService {

    private final PersonDocumentService personDocumentService;

    /**
     * Формирует HTML-сообщение со статистикой по успешно обновленному дому для дальнейшей рассылки.
     * @param realEstateData data отселяемого дома
     * @return               HTML-сообщение со статистикой
     */
    public String buildSuccessEmailBody(RealEstateDataType realEstateData) {
        List<PersonDocument> persons = personDocumentService.fetchByUnom(realEstateData.getUNOM().toString());
        List<FlatType> flats = realEstateData.getFlats().getFlat();

        StringBuilder body = new StringBuilder();
        body.append("Адрес дома: ").append(realEstateData.getAddress()).append("<br><br>");
        appendFlatBlockDescription(body, persons, flats, f -> true, null);

        body.append("<br><br>Среди них:<br>");
        appendFlatBlockDescription(
            body,
            persons,
            flats,
            f -> f.getRoomsCount() != null && f.getRoomsCount().intValue() == 1,
            "1-комнатные квартиры"
        );
        appendFlatBlockDescription(
            body,
            persons,
            flats,
            f -> f.getRoomsCount() != null && f.getRoomsCount().intValue() == 2,
            "2-комнатные квартиры"
        );
        appendFlatBlockDescription(
            body,
            persons,
            flats,
            f -> f.getRoomsCount() != null && f.getRoomsCount().intValue() == 3,
            "3-комнатные квартиры"
        );
        appendFlatBlockDescription(
            body,
            persons,
            flats,
            f -> f.getRoomsCount() != null && f.getRoomsCount().intValue() > 3,
            "4 и более комнат"
        );
        appendFlatBlockDescription(
            body,
            persons,
            flats,
            f -> f.getRoomsCount() == null || f.getRoomsCount().intValue() == 0,
            "Квартиры без информации по количеству комнат"
        );
        return body.toString();
    }

    /**
     * Добавляет в body блок статистики по жителям и квартирам, отфильтрованных по flatFilter.
     * @param body         сюда присоединяем статистику
     * @param persons      все жители дома
     * @param flats        все квартиры дома
     * @param flatFilter   фильтр квартир (по roomsCount)
     * @param title        заголовок блока
     */
    private void appendFlatBlockDescription(
            StringBuilder body,
            List<PersonDocument> persons,
            List<FlatType> flats,
            Predicate<FlatType> flatFilter,
            String title
    ) {
        if (StringUtils.isNotBlank(title)) {
            body.append("<br><strong>").append(title).append("</strong><br>");
        }
        List<FlatType> filteredFlats = flats
                .stream()
                .filter(flatFilter)
                .collect(Collectors.toList());
        List<PersonDocument> filteredPersons = persons
                .stream()
                .filter(p -> filteredFlats
                        .stream()
                        .anyMatch(f -> f.getFlatID() != null
                                && f.getFlatID().equals(p.getDocument().getPersonData().getFlatID()))
                )
                .collect(Collectors.toList());
        List<String> statistics = getStatistics(filteredPersons, filteredFlats);
        for (String description : statistics) {
            body.append(description).append("<br>");
        }
    }

    /**
     * Фрмирует статистику по жителям и кваритрам.
     * @param persons  список жителей
     * @param flats    список квартир
     * @return список строк со статистикой
     */
    private List<String> getStatistics(List<PersonDocument> persons, List<FlatType> flats) {
        List<String> res = new ArrayList<>();

        res.add("Количество жителей: " + persons.size());
        res.add("Количество жителей со СНИЛС: " + persons
                .stream()
                .filter(p -> StringUtils.isNotBlank(p.getDocument().getPersonData().getSNILS()))
                .count()
        );
        res.add("Количество жителей с SsoId: " + persons
                .stream()
                .filter(p -> StringUtils.isNotBlank(p.getDocument().getPersonData().getSsoID()))
                .count()
        );
        res.add("Количество собственников: " + persons
                .stream()
                .filter(p -> p.getDocument().getPersonData().isOwner() != null)
                .filter(p -> p.getDocument().getPersonData().isOwner())
                .count()
        );
        res.add("Количество собственников с SsoId: " + persons
                .stream()
                .filter(p -> p.getDocument().getPersonData().isOwner() != null)
                .filter(p -> p.getDocument().getPersonData().isOwner())
                .filter(p -> StringUtils.isNotBlank(p.getDocument().getPersonData().getSsoID()))
                .count()
        );
        res.add("Количество квартир: " + flats.size());

        int flatsWithSobstvAndSsoId = 0;
        for (FlatType flat : flats) {
            long filteredPersonsCount = persons
                    .stream()
                    .filter(p -> flat.getFlatID().equals(p.getDocument().getPersonData().getFlatID()))
                    .filter(p -> p.getDocument().getPersonData().isOwner() != null)
                    .filter(p -> p.getDocument().getPersonData().isOwner())
                    .filter(p -> StringUtils.isNotBlank(p.getDocument().getPersonData().getSsoID()))
                    .count();
            if (filteredPersonsCount > 0) {
                flatsWithSobstvAndSsoId++;
            }
        }
        res.add("Количество квартир с собственниками с SsoId: " + flatsWithSobstvAndSsoId);

        return res;
    }
}
