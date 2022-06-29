package ru.croc.ugd.ssr.service.flatappointment;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DayScheduleEvents;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.mapper.DayScheduleEventMapper;
import ru.croc.ugd.ssr.mapper.FlatAppointmentDayScheduleMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.service.AbstractRestCipDayScheduleService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDayScheduleDocumentService;

/**
 * DefaultRestFlatAppointmentDayScheduleService.
 */
@Service
public class DefaultRestFlatAppointmentDayScheduleService
    extends AbstractRestCipDayScheduleService<FlatAppointmentDayScheduleDocument> {

    public DefaultRestFlatAppointmentDayScheduleService(
        final FlatAppointmentDayScheduleDocumentService documentService,
        final FlatAppointmentDayScheduleMapper dayScheduleMapper,
        final CipService cipService,
        final DayScheduleEventMapper dayScheduleEventMapper
    ) {
        super(documentService, dayScheduleMapper, cipService, dayScheduleEventMapper);
    }

    @Override
    protected String getBookedDocumentId(final WindowType windowType) {
        return windowType.getOfferLetterId();
    }

    @Override
    protected long getSlotMinutes() {
        //TODO move to configs
        return 30;
    }

    @Override
    protected CipDocument addCipEvent(final CipDocument cipDocument, final DayScheduleEvents.DayScheduleEvent event) {
        final CipType cip = cipDocument.getDocument().getCipData();

        if (cip.getFlatAppointmentDayScheduleEvents() == null) {
            cip.setFlatAppointmentDayScheduleEvents(new DayScheduleEvents());
        }
        cip.getFlatAppointmentDayScheduleEvents().getDayScheduleEvent().add(event);

        return cipDocument;
    }
}
