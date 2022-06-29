package ru.croc.ugd.ssr.service.contractappointment.unsinged;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;

import java.util.List;

@Value
@Builder
public class UnsignedByOwnersCancellationReason implements ContractDigitalSignCancellationReason {

    private final ContractDigitalSignFlowStatus flowStatus;
    private final ContractAppointmentDocument contractAppointmentDocument;
    private final List<String> personNames;

    @Override
    public String getStatusText() {
        return String.format(flowStatus.getStatusText(), compileStatusText());
    }

    private String compileStatusText() {
        if (personNames.size() > 1) {
            return String.format("правообладатели %s не подписали", compilePersonNames());
        }
        return String.format("правообладатель %s не подписал", compilePersonNames());
    }

    private String compilePersonNames() {
        return String.join(", ", personNames);
    }
}
