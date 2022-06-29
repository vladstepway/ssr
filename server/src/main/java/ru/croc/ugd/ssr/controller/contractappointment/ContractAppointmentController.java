package ru.croc.ugd.ssr.controller.contractappointment;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.contractappointment.RestCancelContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestContractAppointmentDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestEmployeeSignatureDto;
import ru.croc.ugd.ssr.dto.contractappointment.RestOwnerSignatureDto;
import ru.croc.ugd.ssr.service.contractappointment.RestContractAppointmentService;

import java.util.List;

/**
 * Контроллер для работы с карточками заявлений на заключение договора.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/internal/contract-appointments")
public class ContractAppointmentController {

    private final RestContractAppointmentService restContractAppointmentService;

    /**
     * Получить карточку.
     *
     * @param id Ид карточки
     * @return карточка
     */
    @GetMapping(value = "/{id}")
    public RestContractAppointmentDto fetchById(@PathVariable("id") String id) {
        return restContractAppointmentService.fetchById(id);
    }

    /**
     * Получить список карточек.
     *
     * @param personDocumentId id жителя
     * @param includeInactive includeInactive
     * @return список карточек
     */
    @GetMapping
    public List<RestContractAppointmentDto> fetchAll(
        @RequestParam("personId") final String personDocumentId,
        @RequestParam(value = "includeInactive", defaultValue = "true") final boolean includeInactive
    ) {
        return restContractAppointmentService.fetchAll(personDocumentId, includeInactive);
    }

    /**
     * Отменить запись.
     *
     * @param id Ид карточки
     */
    @PostMapping(value = "/{id}/cancel")
    public void cancelAppointmentByOperator(
        @PathVariable("id") final String id,
        @RequestBody final RestCancelContractAppointmentDto dto
    ) {
        restContractAppointmentService.cancelAppointmentByOperator(id, dto);
    }

    /**
     * Закрыть возможность отмены записи на осмотр.
     *
     * @param id Ид карточки
     */
    @PostMapping(value = "/{id}/close-cancellation")
    public void closeCancellation(
        @PathVariable("id") final String id
    ) {
        restContractAppointmentService.closeCancellation(id);
    }

    /**
     * Получить список данных об электронных подписях правообладателей, участвующих в подписании договора.
     *
     * @param id ИД документа записи на заключение договора
     * @return список данных об электронных подписях правообладателей
     */
    @GetMapping(value = "/{id}/owner-signatures")
    public List<RestOwnerSignatureDto> fetchOwnerSignatures(@PathVariable("id") final String id) {
        return restContractAppointmentService.fetchOwnerSignatures(id);
    }

    /**
     * Получить сведения об ЭП сотрудника ДГИ.
     *
     * @param id ИД документа записи на заключение договора
     * @return данные об ЭП сотрудника ДГИ
     */
    @GetMapping(value = "/{id}/employee-signature")
    public RestEmployeeSignatureDto fetchEmployeeSignature(@PathVariable("id") final String id) {
        return restContractAppointmentService.fetchEmployeeSignature(id);
    }
}
