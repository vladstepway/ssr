package ru.croc.ugd.ssr.exception;

/**
 * Валидационная ошибка при отсутствии количества комнат.
 */
public class MissedRoomNumber extends SsrException {

    /**
     * Создает MissedRoomNumber.
     */
    public MissedRoomNumber() {
        super("Количество комнат должно быть указано");
    }
}
