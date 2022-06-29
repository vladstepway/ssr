package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Optional.ofNullable;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class ContractDigitalSignNotificationDto {

    private final ContractDigitalSignDocument contractDigitalSignDocument;
    private final ContractAppointmentDocument contractAppointmentDocument;
    private final PersonDocument personDocument;
    private final String contractOrderId;
    private final PersonType.Contracts.Contract contract;
    private final String addressTo;

    public ContractDigitalSignData getContractDigitalSignData() {
        return contractDigitalSignDocument.getDocument().getContractDigitalSignData();
    }

    public List<Owner> getOwners() {
        return ofNullable(getContractDigitalSignData().getOwners())
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList());
    }
}
