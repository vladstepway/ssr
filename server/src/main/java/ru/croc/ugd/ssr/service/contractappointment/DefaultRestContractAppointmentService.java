package ru.croc.ugd.ssr.service.contractappointment;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.Employee;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractappointment.RestCancelContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentApplicantDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestEmployeeSignatureDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestOwnerSignatureDto;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.ContractAppointmentElkNotificationService;
import ru.croc.ugd.ssr.mapper.ContractAppointmentMapper;
import ru.croc.ugd.ssr.mapper.ContractDigitalSignMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultContractAppointmentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestContractAppointmentService implements RestContractAppointmentService {

    private final CipService cipService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final ContractAppointmentMapper contractAppointmentMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;
    private final ContractAppointmentService contractAppointmentService;
    private final ContractAppointmentElkNotificationService contractAppointmentElkNotificationService;
    private final SsrFilestoreService ssrFilestoreService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final ContractDigitalSignMapper contractDigitalSignMapper;
    private final UserService userService;

    @Override
    public RestContractAppointmentDto fetchById(final String id) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchDocument(id);

        final CipDocument cipDocument = retrieveCipDocument(contractAppointmentDocument);
        final RestContractAppointmentApplicantDto applicant = toApplicantDto(contractAppointmentDocument);

        final RestContractAppointmentDto restContractAppointmentDto =
            contractAppointmentMapper.toRestContractAppointmentDto(
                contractAppointmentDocument,
                cipDocument,
                applicant
            );
        log.info("Определён список доступных возможностей для договора: contractAppointmentId = {}", id);
        return restContractAppointmentDto;
    }

    @Override
    public List<RestContractAppointmentDto> fetchAll(final String personDocumentId, final boolean includeInactive) {
        final List<ContractAppointmentDocument> contractAppointmentDocuments = contractAppointmentDocumentService
            .fetchAll(personDocumentId, includeInactive);

        final PersonDocument personDocument = personDocumentService.fetchById(personDocumentId).orElse(null);
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .orElse(null);

        return contractAppointmentDocuments.stream()
            .map(contractAppointmentDocument ->
                toRestContractAppointmentDto(contractAppointmentDocument, personDocument, realEstateDataAndFlatInfoDto)
            )
            .collect(Collectors.toList());
    }

    private RestContractAppointmentDto toRestContractAppointmentDto(
        final ContractAppointmentDocument contractAppointmentDocument,
        final PersonDocument personDocument,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        final CipDocument cipDocument = retrieveCipDocument(contractAppointmentDocument);
        final RestContractAppointmentApplicantDto applicantDto = contractAppointmentMapper
            .toRestContractAppointmentApplicantDto(
                contractAppointment,
                personDocument,
                realEstateDataAndFlatInfoDto
            );

        return contractAppointmentMapper
            .toRestContractAppointmentDto(contractAppointmentDocument, cipDocument, applicantDto);
    }

    @Override
    public void cancelAppointmentByOperator(final String id, final RestCancelContractAppointmentDto dto) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchDocument(id);

        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (!contractAppointmentService.canCancelAppointment(contractAppointment, false)) {
            throw new SsrException("Нельзя перенести запись менее чем за сутки до приема");
        }

        contractAppointment.setCancelDate(dto.getCancelDate());
        contractAppointment.setCancelReason(dto.getCancelReason());
        contractAppointmentService.setAndSendActualStatus(
            contractAppointmentDocument,
            ContractAppointmentFlowStatus.CANCELED_BY_OPERATOR,
            ContractDigitalSignFlowStatus.CANCELED_BY_OPERATOR
        );

        contractAppointmentService.cancelBookingIfRequired(contractAppointmentDocument);

        ofNullable(contractAppointment.getProcessInstanceId())
            .ifPresent(contractAppointmentService::finishBpmProcess);

        contractAppointmentDocumentService
            .updateDocument(contractAppointmentDocument.getId(), contractAppointmentDocument, true, true, null);
    }

    @Override
    public void closeCancellation(final String id) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchDocument(id);

        contractAppointmentElkNotificationService.sendStatus(
            ContractAppointmentFlowStatus.UNABLE_TO_CANCEL, contractAppointmentDocument
        );
    }

    @Override
    public byte[] getIcsFileContent(final String id) {
        final ContractAppointmentDocument contractAppointmentDocument =
            contractAppointmentDocumentService.fetchDocument(id);

        return retrieveIcsFileStoreId(contractAppointmentDocument);
    }

    @Override
    public List<RestOwnerSignatureDto> fetchOwnerSignatures(final String id) {
        final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignDocumentService
            .findByContractAppointmentId(id)
            .orElseThrow(() -> new SsrException(
                "Не найдено многостороннее подписание договора с использованием УКЭП для заявления " + id
            ));

        final List<Owner> owners = of(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList());

        return contractDigitalSignMapper.toRestOwnerSignatureDtos(owners);
    }

    @Override
    public RestEmployeeSignatureDto fetchEmployeeSignature(final String id) {
        final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignDocumentService
            .findByContractAppointmentId(id)
            .orElseThrow(() -> new SsrException(
                "Не найдено многостороннее подписание договора с использованием УКЭП для заявления " + id
            ));

        final Employee employee = of(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .orElse(null);

        final String employeeFullName = ofNullable(employee)
            .map(Employee::getLogin)
            .map(userService::getUserBeanByLogin)
            .map(UserBean::getDisplayName)
            .orElse(null);

        return contractDigitalSignMapper.toRestEmployeeSignatureDto(employee, employeeFullName);
    }

    private byte[] retrieveIcsFileStoreId(final ContractAppointmentDocument contractAppointmentDocument) {
        return of(contractAppointmentDocument.getDocument().getContractAppointmentData())
            .map(ContractAppointmentData::getIcsFileStoreId)
            .map(ssrFilestoreService::getFile)
            .orElse(null);
    }

    private CipDocument retrieveCipDocument(final ContractAppointmentDocument contractAppointmentDocument) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        return ofNullable(contractAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .orElse(null);
    }

    private RestContractAppointmentApplicantDto toApplicantDto(
        final ContractAppointmentDocument contractAppointmentDocument
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        final PersonDocument personDocument = ofNullable(contractAppointment.getApplicant())
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElse(null);

        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .orElse(null);

        return contractAppointmentMapper.toRestContractAppointmentApplicantDto(
            contractAppointment,
            personDocument,
            realEstateDataAndFlatInfoDto
        );
    }
}
