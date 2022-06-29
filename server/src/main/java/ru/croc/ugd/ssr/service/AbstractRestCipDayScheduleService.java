package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import ru.croc.ugd.ssr.DayScheduleEvents;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.enums.DayScheduleEventType;
import ru.croc.ugd.ssr.mapper.DayScheduleEventMapper;
import ru.croc.ugd.ssr.mapper.DayScheduleMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;
import ru.croc.ugd.ssr.service.cip.CipService;

import java.time.LocalDate;

public abstract class AbstractRestCipDayScheduleService<T extends DayScheduleDocumentAbstract>
    extends AbstractRestDayScheduleService<T> {

    private final CipService cipService;
    private final DayScheduleEventMapper dayScheduleEventMapper;

    public AbstractRestCipDayScheduleService(
        final AbstractDayScheduleDocumentService<T> dayScheduleDocumentService,
        final DayScheduleMapper<T> dayScheduleMapper,
        final CipService cipService,
        final DayScheduleEventMapper dayScheduleEventMapper
    ) {
        super(dayScheduleDocumentService, dayScheduleMapper);
        this.cipService = cipService;
        this.dayScheduleEventMapper = dayScheduleEventMapper;
    }

    @Override
    protected String getParentDocumentId(final RestDaySchedulePeriodDto periodDto) {
        return periodDto.getCipId();
    }

    @Override
    protected void afterCreate(final String cipId, final LocalDate date) {
        addCipEvent(cipId, date, DayScheduleEventType.CREATE_DAY);
    }

    @Override
    protected void afterUpdate(final String cipId, final LocalDate date) {
        addCipEvent(cipId, date, DayScheduleEventType.UPDATE_DAY);
    }

    @Override
    protected void afterDelete(final String cipId, final LocalDate date) {
        addCipEvent(cipId, date, DayScheduleEventType.DELETE_DAY);
    }

    @Override
    protected void afterDelete(final String cipId, final RestDaySchedulePeriodDto periodDto) {
        addCipEvent(cipId, null, periodDto, DayScheduleEventType.DELETE_PERIOD);
    }

    @Override
    protected void afterCopy(final String cipId, final LocalDate date, final RestDaySchedulePeriodDto periodDto) {
        addCipEvent(cipId, date, periodDto, DayScheduleEventType.COPY_PERIOD);
    }


    private void addCipEvent(final String cipId, final LocalDate date, final DayScheduleEventType type) {
        addCipEvent(cipId, dayScheduleEventMapper.toDayScheduleEvent(type.getCode(), date));
    }

    private void addCipEvent(
        final String cipId,
        final LocalDate date,
        final RestDaySchedulePeriodDto periodDto,
        final DayScheduleEventType type
    ) {
        addCipEvent(cipId, dayScheduleEventMapper.toDayScheduleEvent(type.getCode(), date, periodDto));
    }

    private void addCipEvent(final String cipId, DayScheduleEvents.DayScheduleEvent event) {
        ofNullable(cipId)
            .flatMap(cipService::fetchById)
            .map(cipDocument -> addCipEvent(cipDocument, event))
            .ifPresent(cipDocument ->
                cipService.updateDocument(cipDocument.getId(), cipDocument, true, true, null)
            );
    }

    protected abstract CipDocument addCipEvent(
        final CipDocument cipDocument, final DayScheduleEvents.DayScheduleEvent event
    );

}
