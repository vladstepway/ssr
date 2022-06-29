package ru.croc.ugd.ssr.service.contractappointment.unsinged;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

@Value
@Builder
public class UnsignedByEmployeeCancellationReason implements ContractDigitalSignCancellationReason {

    private final ContractDigitalSignFlowStatus flowStatus;
    private final ContractAppointmentDocument contractAppointmentDocument;

    @Override
    public String getStatusText() {
        return flowStatus.getStatusText();
    }
}
