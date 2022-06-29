package ru.croc.ugd.ssr.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.reinform.cdp.document.model.DocumentAbstract;

import java.time.LocalDate;
import java.util.List;

public abstract class DayScheduleDocumentAbstract<T> extends DocumentAbstract<T> {

    @JsonIgnore
    abstract public List<SlotType> getSlotList();

    abstract public List<SlotType> createSlotList();

    @JsonIgnore
    abstract public void setDate(final LocalDate date);

    @JsonIgnore
    public long getBookedSlotsCount() {
        return getSlotList()
            .stream()
            .map(SlotType::getWindows)
            .map(SlotType.Windows::getWindow)
            .flatMap(List::stream)
            .filter(window -> nonNull(window.getPreBookingId())
                && isNull(window.getPreBookedUntil())
                || !window.getPrematurelyCompletedApplicationIds().isEmpty()
            )
            .count();
    }

    @JsonIgnore
    abstract public String getParentDocumentId();

    @JsonIgnore
    abstract public LocalDate getDate();
}
