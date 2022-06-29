package ru.croc.ugd.ssr.service.notary;

import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApplicationDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryBookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryListDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotRequestDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPrebookSlotResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryTimetableDto;

import java.time.LocalDate;


public interface RestExternalNotaryService {

    RestNotaryListDto fetchAll();

    RestNotaryApplicationDto fetchNotaryApplication(final String eno);

    RestNotaryPersonCheckDto check(final String snils, final String ssoId);

    RestNotaryTimetableDto fetchTimetable(final String notaryId, final LocalDate startFrom, final Integer days);

    RestNotaryPrebookSlotResponseDto prebookSlot(final RestNotaryPrebookSlotRequestDto body);

    void bookSlot(final RestNotaryBookSlotRequestDto body, final Boolean isSsrBooking);
}
