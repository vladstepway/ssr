package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.FlatAppointmentDao;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.DocumentWithFolder;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FlatAppointmentDocumentService.
 */
@Service
@AllArgsConstructor
public class FlatAppointmentDocumentService extends DocumentWithFolder<FlatAppointmentDocument> {

    private final FlatAppointmentDao flatAppointmentDao;
    private final FlatAppointmentDayScheduleDocumentService flatAppointmentDayScheduleDocumentService;

    @NotNull
    @Override
    public DocumentType<FlatAppointmentDocument> getDocumentType() {
        return SsrDocumentTypes.FLAT_APPOINTMENT;
    }

    @Override
    public void afterDelete(@NotNull FlatAppointmentDocument document) {
        Optional.ofNullable(document.getDocument().getFlatAppointmentData())
            .map(FlatAppointmentData::getBookingId)
            .ifPresent(flatAppointmentDayScheduleDocumentService::cancelBooking);
    }

    public boolean existsByEno(final String eno) {
        return flatAppointmentDao.existsByEno(eno);
    }

    /**
     * Получение заявления на осмотр квартиры по номеру заявления.
     *
     * @param eno номер заявления.
     * @return заявление на осмотр квартиры
     */
    public Optional<FlatAppointmentDocument> findByEno(final String eno) {
        return flatAppointmentDao.findByEno(eno)
            .map(this::parseDocumentData);
    }

    public List<FlatAppointmentDocument> findAll(final String personDocumentId, final boolean includeInactive) {
        return flatAppointmentDao.findAll(personDocumentId, includeInactive)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<FlatAppointmentDocument> findAll() {
        return flatAppointmentDao.findAll()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public boolean isAppointmentRegisteredByBookingId(final String bookingId) {
        return flatAppointmentDao.existsByBookingIdAndStatusId(bookingId, FlatAppointmentFlowStatus.REGISTERED.getId());
    }

    /**
     * Получение заявлений на осмотр квартиры, по которым не внесен результат, по дате и времени записи на прием.
     * @param appointmentDate дата, до которой должен был произойти осмотр
     * @return заявления на осмотр квартиры
     */
    public List<FlatAppointmentDocument> findRegisteredAppointmentsByAppointmentDateTimeBefore(
        final LocalDate appointmentDate
    ) {
        return flatAppointmentDao.findRegisteredAppointmentsByAppointmentDateTimeBefore(appointmentDate)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
