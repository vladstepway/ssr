package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.reinform.cdp.mdm.service.BusinessCalendarRemoteService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SsrBusinessCalendarService {

    private final BusinessCalendarRemoteService businessCalendarRemoteService;

    @Cacheable(value = CacheConfig.BUSINESS_CALENDAR_GET_DATE)
    public LocalDate addWorkDays(final LocalDate fromDate, final int workDays) {
        return ofNullable(fromDate)
            .map(date -> businessCalendarRemoteService
                .addWorkDays(date.atStartOfDay(), workDays)
            )
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    public boolean isWorkDay(final LocalDate date) {
        return businessCalendarRemoteService.isWorkDay(date);
    }
}
