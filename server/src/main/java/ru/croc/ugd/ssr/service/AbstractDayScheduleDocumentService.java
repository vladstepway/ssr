package ru.croc.ugd.ssr.service;

import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractDayScheduleDocumentService<T extends DayScheduleDocumentAbstract>
    extends AbstractDocumentService<T> {

    public abstract List<T> fetchAllByDateAndParentDocumentId(
        final LocalDate from, final LocalDate to, final String parentDocumentId, final boolean isFreeOnly
    );
}
