package ru.croc.ugd.ssr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDaySchedule;
import ru.croc.ugd.ssr.dayschedule.notary.NotaryDayScheduleType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NotaryDayScheduleDocument extends DayScheduleDocumentAbstract<NotaryDaySchedule> {

    @Getter
    @Setter
    @JsonProperty("notaryDaySchedule")
    private NotaryDaySchedule document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String s) {
        document.setDocumentID(s);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String s) {
        document.setFolderGUID(s);
    }

    @Override
    public List<SlotType> getSlotList() {
        return Optional.ofNullable(document.getNotaryDayScheduleData())
            .map(NotaryDayScheduleType::getSlots)
            .map(NotaryDayScheduleType.Slots::getSlot)
            .orElseGet(Collections::emptyList);
    }

    @Override
    public List<SlotType> createSlotList() {
        final NotaryDayScheduleType notaryDayScheduleType = document.getNotaryDayScheduleData();

        if (notaryDayScheduleType.getSlots() == null) {
            notaryDayScheduleType.setSlots(new NotaryDayScheduleType.Slots());
        }

        return notaryDayScheduleType.getSlots().getSlot();
    }

    @Override
    public void setDate(LocalDate date) {
        document.getNotaryDayScheduleData().setDate(date);
    }

    @Override
    public String getParentDocumentId() {
        return document.getNotaryDayScheduleData().getNotaryId();
    }

    @Override
    public LocalDate getDate() {
        return document.getNotaryDayScheduleData().getDate();
    }
}
