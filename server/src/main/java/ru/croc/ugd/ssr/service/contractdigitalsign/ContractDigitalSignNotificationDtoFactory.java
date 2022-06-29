package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class ContractDigitalSignNotificationDtoFactory {

    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final PersonDocumentService personDocumentService;

    public Optional<ContractDigitalSignNotificationDto> createDto(
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        try {
            final ContractAppointmentDocument contractAppointmentDocument =
                contractAppointmentDocumentService.fetchByContractDigitalSign(contractDigitalSignDocument);
            final ContractAppointmentData contractAppointment = contractAppointmentDocument
                .getDocument()
                .getContractAppointmentData();
            final PersonDocument personDocument = fetchPersonDocument(contractAppointmentDocument, contractAppointment);
            final String contractOrderId = getContractOrderId(contractAppointmentDocument, contractAppointment);
            final PersonType.Contracts.Contract contract = getContract(personDocument, contractOrderId);
            final String addressTo = getAddressTo(contractAppointmentDocument, contractAppointment);

            return Optional.of(
                ContractDigitalSignNotificationDto.builder()
                    .contractDigitalSignDocument(contractDigitalSignDocument)
                    .contractAppointmentDocument(contractAppointmentDocument)
                    .personDocument(personDocument)
                    .contractOrderId(contractOrderId)
                    .contract(contract)
                    .addressTo(addressTo)
                    .build()
            );
        } catch (Exception e) {
            log.warn("Failed to create DigitalSignNotificationDto: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private PersonDocument fetchPersonDocument(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ContractAppointmentData contractAppointment
    ) {
        return ofNullable(contractAppointment.getApplicant())
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElseThrow(() -> new SsrException(
                String.format(
                    "Person in contractAppointment %s not found",
                    contractAppointmentDocument.getId()
                )
            ));
    }

    private String getContractOrderId(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ContractAppointmentData contractAppointment
    ) {
        return ofNullable(contractAppointment.getContractOrderId())
            .orElseThrow(() -> new SsrException(
                String.format(
                    "ContractOrderId in contractAppointment %s is null",
                    contractAppointmentDocument.getId()
                )
            ));
    }

    private PersonType.Contracts.Contract getContract(
        final PersonDocument personDocument,
        final String contractOrderId
    ) {
        return PersonUtils.getContractByOrderId(personDocument, contractOrderId)
            .orElseThrow(() -> new SsrException(
                String.format(
                    "No contract found with orderId %s",
                    contractOrderId
                )
            ));
    }

    private String getAddressTo(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ContractAppointmentData contractAppointment
    ) {
        return ofNullable(contractAppointment.getAddressTo())
            .orElseThrow(() -> new SsrException(
                String.format(
                    "AddressTo in contractAppointment %s is null",
                    contractAppointmentDocument.getId()
                )
            ));
    }
}
