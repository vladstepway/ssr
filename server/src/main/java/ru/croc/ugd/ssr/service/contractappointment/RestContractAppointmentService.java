package ru.croc.ugd.ssr.service.contractappointment;

import ru.croc.ugd.ssr.dto.contractappointment.RestCancelContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestEmployeeSignatureDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestOwnerSignatureDto;

import java.util.List;

/**
 * Сервис для работы с карточками заявлений на заключение договора.
 */
public interface RestContractAppointmentService {

    RestContractAppointmentDto fetchById(final String id);

    List<RestContractAppointmentDto> fetchAll(final String personDocumentId, final boolean includeInactive);

    void cancelAppointmentByOperator(final String id, final RestCancelContractAppointmentDto dto);

    void closeCancellation(final String id);

    /**
     * Получить содержимое ics файла.
     *
     * @param id ИД документа
     * @return содержимое ics файла
     */
    byte[] getIcsFileContent(final String id);

    /**
     * Получить список данных об электронных подписях правообладателей, участвующих в подписании договора.
     *
     * @param id ИД документа записи на заключение договора
     * @return список данных об электронных подписях правообладателей
     */
    List<RestOwnerSignatureDto> fetchOwnerSignatures(final String id);

    /**
     * Получить сведения об ЭП сотрудника ДГИ.
     *
     * @param id ИД документа записи на заключение договора
     * @return сведения об ЭП сотрудника ДГИ
     */
    RestEmployeeSignatureDto fetchEmployeeSignature(final String id);
}
