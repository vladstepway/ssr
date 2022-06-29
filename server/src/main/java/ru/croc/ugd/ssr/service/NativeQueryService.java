package ru.croc.ugd.ssr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.SentMessageStatisticsDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Сервис для нативных запросов к БД.
 */
@Service
public class NativeQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(NativeQueryService.class);

    private static String SENT_MESSAGES_STATISTICS
            = "with list as ( "
            + "  select * from ( "
            + "    values "
            + "      ('Уведомление граждан о начале переселения', 0, 0, 0, 0, 1), "
            + "      ('Уведомление граждан о письме с предложением', 0, 0, 0, 0, 2), "
            + "      ('Уведомление о необходимости подписать заявление на согласие/отказ после просмотра квартиры'"
            + "        , 0, 0, 0, 0, 3), "
            + "      ('Уведомление граждан о поданном отказе', 0, 0, 0, 0, 4), "
            + "      ('Уведомление граждан о поданном согласии', 0, 0, 0, 0, 5), "
            + "      ('Уведомление граждан об устранении дефектов', 0, 0, 0, 0, 6), "
            + "      ('Уведомление о готовности проекта договора с приглашением на подписание договора'"
            + "        , 0, 0, 0, 0, 7), "
            + "      ('Уведомление о подписанном договоре', 0, 0, 0, 0, 8) "
            + "  ) as t(name, elk, sended, handed, percentage, order_num) "
            + ") "
            + "select "
            + "  coalesce(statistic.name, list.name) as name, "
            + "  coalesce(statistic.elk, list.elk) as elk, "
            + "  coalesce(statistic.sended, list.sended) as sended, "
            + "  coalesce(statistic.handed, list.handed) as handed, "
            + "  coalesce(statistic.percentage, list.percentage) as percentage "
            + "from ( "
            + "  select "
            + "  message ->> 'businessType' as name, "
            + "  sum(case when message ->> 'event' = 'Уведомление направлено в ЕЛК' then 1 else 0 end) as elk, "
            + "  sum(case when message ->> 'event' = 'Доставлено адресату' then 1 else 0 end) as sended, "
            + "  sum(case when message ->> 'event' = 'Вручено адресату' then 1 else 0 end) as handed, "
            + "  case when "
            + "    sum(case when message ->> 'event' = 'Доставлено адресату' then 1 else 0 end) = 0 then 0 "
            + "    else sum(case when message ->> 'event' = 'Вручено адресату' then 1 else 0 end) * 100 "
            + "      / sum(case when message ->> 'event' = 'Доставлено адресату' then 1 else 0 end) "
            + "  end as percentage "
            + "from ( "
            + "  select "
            + "    id, "
            + "    jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> "
            + "       'sendedMessages' -> 'Message') as message "
            + "  from ssr.documents where doc_type = 'PERSON' "
            + ") as message "
            + "where message ->> 'event' in ("
            + "  'Доставлено адресату', 'Вручено адресату', 'Уведомление направлено в ЕЛК') "
            + "group by message ->> 'businessType' "
            + ") as statistic "
            + "full join list on list.name = statistic.name "
            + "order by list.order_num";

    @Autowired
    private DataSource dataSource;

    /**
     * Получить статистику по доставленным/прочитанным уведомлениям жителям.
     *
     * @return список с DTO статистикой
     */
    public List<SentMessageStatisticsDto> getSentMessagesStatistics() {
        List<SentMessageStatisticsDto> result = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement sentMessagesStatisticsStatement
                        = connection.prepareStatement(SENT_MESSAGES_STATISTICS)
        ) {
            ResultSet rs = sentMessagesStatisticsStatement.executeQuery();
            while (rs.next()) {
                SentMessageStatisticsDto dto = SentMessageStatisticsDto.builder()
                    .name(rs.getString(1))
                    .elk(rs.getLong(2))
                    .sended(rs.getLong(3))
                    .handed(rs.getLong(4))
                    .percentage(rs.getInt(5))
                    .build();
                result.add(dto);
            }

            return result;
        } catch (SQLException e) {
            LOG.error(e.toString());
            return null;
        }
    }

}
