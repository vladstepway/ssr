package ru.croc.ugd.ssr.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.db.dao.RoomDao;
import ru.reinform.cdp.utils.mapper.JsonMapper;

/**
 * Сервис для работы с комнатой.
 */
@Service
@AllArgsConstructor
public class RoomService {

    private final RoomDao roomDao;
    private final JsonMapper jsonMapper;

    /**
     * Получить объект комнаты по id.
     *
     * @param id - идентификатор комнаты
     * @return JSON объект комнаты
     */
    public RoomType fetchRoom(String id) {
        String fetchRoom = roomDao.fetch(id);
        if (fetchRoom == null) {
            return null;
        } else {
            return jsonMapper.readObject(fetchRoom, RoomType.class);
        }
    }

}
