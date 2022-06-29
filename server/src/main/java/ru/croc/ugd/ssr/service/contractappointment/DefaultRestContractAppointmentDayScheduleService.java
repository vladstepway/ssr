package ru.croc.ugd.ssr.service.contractappointment;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.DayScheduleEvents;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.mapper.ContractAppointmentDayScheduleMapper;
import ru.croc.ugd.ssr.mapper.DayScheduleEventMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDayScheduleDocument;
import ru.croc.ugd.ssr.service.AbstractRestCipDayScheduleService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDayScheduleDocumentService;

/**
 * DefaultRestContractAppointmentDayScheduleService.
 */
@Service
public class DefaultRestContractAppointmentDayScheduleService
    extends AbstractRestCipDayScheduleService<ContractAppointmentDayScheduleDocument> {

    public DefaultRestContractAppointmentDayScheduleService(
        final ContractAppointmentDayScheduleDocumentService documentService,
        final ContractAppointmentDayScheduleMapper dayScheduleMapper,
        final CipService cipService,
        final DayScheduleEventMapper dayScheduleEventMapper
    ) {
        super(documentService, dayScheduleMapper, cipService, dayScheduleEventMapper);
    }

    @Override
    protected String getBookedDocumentId(final WindowType windowType) {
        return windowType.getContractOrderId();
    }

    @Override
    protected long getSlotMinutes() {
        //TODO move to configs
        return 30;
    }


    @Override
    protected CipDocument addCipEvent(final CipDocument cipDocument, final DayScheduleEvents.DayScheduleEvent event) {
        final CipType cip = cipDocument.getDocument().getCipData();

        if (cip.getContractAppointmentDayScheduleEvents() == null) {
            cip.setContractAppointmentDayScheduleEvents(new DayScheduleEvents());
        }
        cip.getContractAppointmentDayScheduleEvents().getDayScheduleEvent().add(event);

        return cipDocument;
    }
}
