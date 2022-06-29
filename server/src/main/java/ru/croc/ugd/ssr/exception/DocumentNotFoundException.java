package ru.croc.ugd.ssr.exception;

public class DocumentNotFoundException extends SsrException {
    public DocumentNotFoundException() {
        super("Документ не найден");
    }
}
