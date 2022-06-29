package ru.croc.ugd.ssr.model.contractappointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.dayschedule.SlotType;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDaySchedule;
import ru.croc.ugd.ssr.dayschedule.contractappointment.ContractAppointmentDayScheduleData;
import ru.croc.ugd.ssr.model.DayScheduleDocumentAbstract;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ContractAppointmentDayScheduleDocument
    extends DayScheduleDocumentAbstract<ContractAppointmentDaySchedule> {

    @Getter
    @Setter
    @JsonProperty("contractAppointmentDaySchedule")
    private ContractAppointmentDaySchedule document;

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
        return Optional.ofNullable(document.getContractAppointmentDayScheduleData())
            .map(ContractAppointmentDayScheduleData::getSlots)
            .map(ContractAppointmentDayScheduleData.Slots::getSlot)
            .orElseGet(Collections::emptyList);
    }

    @Override
    public List<SlotType> createSlotList() {
        final ContractAppointmentDayScheduleData contractAppointmentDayScheduleData = document
            .getContractAppointmentDayScheduleData();

        if (contractAppointmentDayScheduleData.getSlots() == null) {
            contractAppointmentDayScheduleData.setSlots(new ContractAppointmentDayScheduleData.Slots());
        }

        return contractAppointmentDayScheduleData.getSlots().getSlot();
    }

    @Override
    public void setDate(final LocalDate date) {
        document.getContractAppointmentDayScheduleData().setDate(date);
    }

    @Override
    public String getParentDocumentId() {
        return document.getContractAppointmentDayScheduleData().getCipId();
    }

    @Override
    public LocalDate getDate() {
        return document.getContractAppointmentDayScheduleData().getDate();
    }
}
