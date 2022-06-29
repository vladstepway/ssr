package ru.croc.ugd.ssr.integration.service.mqetpmv.mpgu;

public interface SsrMqetpmvMpguService {

    //TODO Konstantin rewrite
    <T> void exportMessage(final T messageObject, final String etpProfile, final Class<T> clazz);
}
