package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.db.dao.ContractAppointmentDao;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ContractAppointmentDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ContractAppointmentDocumentService extends DocumentWithFolder<ContractAppointmentDocument> {

    private final ContractAppointmentDao contractAppointmentDao;
    private final ContractAppointmentDayScheduleDocumentService contractAppointmentDayScheduleDocumentService;

    @NotNull
    @Override
    public DocumentType<ContractAppointmentDocument> getDocumentType() {
        return SsrDocumentTypes.CONTRACT_APPOINTMENT;
    }

    public Optional<ContractAppointmentDocument> fetchActiveOrEnteredContractAppointment(
        final String contractId
    ) {
        return contractAppointmentDao
            .fetchActiveOrEnteredContractAppointment(contractId)
            .map(this::parseDocumentData);
    }

    @Override
    public void afterDelete(@NotNull ContractAppointmentDocument document) {
        Optional.ofNullable(document.getDocument().getContractAppointmentData())
            .map(ContractAppointmentData::getBookingId)
            .ifPresent(contractAppointmentDayScheduleDocumentService::cancelBooking);
    }

    public boolean existsByEno(final String eno) {
        return contractAppointmentDao.existsByEno(eno);
    }

    /**
     * Получение заявления на подписание договора по номеру заявления.
     *
     * @param eno номер заявления.
     * @return заявление на подписание договора
     */
    public Optional<ContractAppointmentDocument> findByEno(final String eno) {
        return contractAppointmentDao.findByEno(eno)
            .map(this::parseDocumentData);
    }

    public Optional<ContractAppointmentDocument> findByContractDigitalSign(
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        return of(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getContractAppointmentId)
            .flatMap(this::fetchById);
    }

    public ContractAppointmentDocument fetchByContractDigitalSign(
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        return findByContractDigitalSign(contractDigitalSignDocument)
            .orElseThrow(() -> new SsrException(
                "Unable to find ContractAppointmentDocument by contractDigitalSignDocumentId = "
                    + contractDigitalSignDocument.getId()
            ));
    }

    public boolean isAppointmentRegisteredByBookingId(final String bookingId) {
        return contractAppointmentDao
            .existsByBookingIdAndStatusId(bookingId, ContractAppointmentFlowStatus.REGISTERED.getId());
    }

    public List<ContractAppointmentDocument> fetchAll(final String personDocumentId, final boolean includeInactive) {
        return contractAppointmentDao.fetchAll(personDocumentId, includeInactive)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получение заявлений на подписание договора, по которым не внесен результат, по дате и времени записи на прием.
     *
     * @param appointmentDate дата, до которой должен был произойти прием
     * @return заявления на подписание договоров
     */
    public List<ContractAppointmentDocument> findRegisteredAppointmentsByAppointmentDateTimeBefore(
        final LocalDate appointmentDate
    ) {
        return contractAppointmentDao.findRegisteredAppointmentsByAppointmentDateTimeBefore(appointmentDate)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<ContractAppointmentDocument> findUnsignedAppointmentsForYesterday() {
        return contractAppointmentDao.findUnsignedAppointmentsForYesterday()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
