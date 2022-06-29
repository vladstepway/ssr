package ru.croc.ugd.ssr.exception;

public class StatusNotFoundException extends SsrException {

    public StatusNotFoundException() {
        super("Не найден статус");
    }
}
