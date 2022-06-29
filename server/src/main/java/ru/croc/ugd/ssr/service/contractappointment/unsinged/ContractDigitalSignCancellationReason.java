package ru.croc.ugd.ssr.service.contractappointment.unsinged;

import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

public interface ContractDigitalSignCancellationReason {

    ContractDigitalSignFlowStatus getFlowStatus();

    ContractAppointmentDocument getContractAppointmentDocument();

    String getStatusText();
}
