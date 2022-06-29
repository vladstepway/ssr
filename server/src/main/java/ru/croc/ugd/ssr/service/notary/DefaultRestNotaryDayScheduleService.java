package ru.croc.ugd.ssr.service.notary;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dayschedule.WindowType;
import ru.croc.ugd.ssr.dto.RestDaySchedulePeriodDto;
import ru.croc.ugd.ssr.mapper.NotaryDayScheduleMapper;
import ru.croc.ugd.ssr.model.NotaryDayScheduleDocument;
import ru.croc.ugd.ssr.service.AbstractRestDayScheduleService;
import ru.croc.ugd.ssr.service.document.NotaryDayScheduleDocumentService;

/**
 * DefaultRestNotaryDayScheduleService.
 */
@Service
public class DefaultRestNotaryDayScheduleService
    extends AbstractRestDayScheduleService<NotaryDayScheduleDocument> {

    public DefaultRestNotaryDayScheduleService(
        NotaryDayScheduleDocumentService documentService,
        NotaryDayScheduleMapper dayScheduleMapper
    ) {
        super(documentService, dayScheduleMapper);
    }

    @Override
    protected String getParentDocumentId(final RestDaySchedulePeriodDto periodDto) {
        return periodDto.getNotaryId();
    }

    @Override
    protected String getBookedDocumentId(final WindowType windowType) {
        return windowType.getEno();
    }

    @Override
    protected long getSlotMinutes() {
        //TODO move to configs
        return 60;
    }
}
