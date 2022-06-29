package ru.croc.ugd.ssr.model.flatAppointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.flatappointment.FlatAppointmentDayScheduleData;
import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FlatAppointmentDayScheduleDocument
    extends DayScheduleDocumentAbstract<FlatAppointmentDaySchedule> {

    @Getter
    @Setter
    @JsonProperty("flatAppointmentDaySchedule")
    private FlatAppointmentDaySchedule document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String id) {
        document.setDocumentID(id);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String id) {
        document.setFolderGUID(id);
    }

    @Override
    public List<SlotType> getSlotList() {
        return Optional.ofNullable(document.getFlatAppointmentDayScheduleData())
            .map(FlatAppointmentDayScheduleData::getSlots)
            .map(FlatAppointmentDayScheduleData.Slots::getSlot)
            .orElseGet(Collections::emptyList);
    }

    @Override
    public List<SlotType> createSlotList() {
        final FlatAppointmentDayScheduleData flatAppointmentDayScheduleData =
            document.getFlatAppointmentDayScheduleData();

        if (flatAppointmentDayScheduleData.getSlots() == null) {
            flatAppointmentDayScheduleData.setSlots(new FlatAppointmentDayScheduleData.Slots());
        }

        return flatAppointmentDayScheduleData.getSlots().getSlot();
    }

    @Override
    public void setDate(LocalDate date) {
        document.getFlatAppointmentDayScheduleData().setDate(date);
    }

    @Override
    public String getParentDocumentId() {
        return document.getFlatAppointmentDayScheduleData().getCipId();
    }

    @Override
    public LocalDate getDate() {
        return document.getFlatAppointmentDayScheduleData().getDate();
    }
}
