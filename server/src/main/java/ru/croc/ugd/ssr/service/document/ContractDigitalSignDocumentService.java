package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ContractDigitalSignDao;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ContractDigitalSignDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ContractDigitalSignDocumentService extends SsrAbstractDocumentService<ContractDigitalSignDocument> {

    private final ContractDigitalSignDao contractDigitalSignDao;

    @NotNull
    @Override
    public DocumentType<ContractDigitalSignDocument> getDocumentType() {
        return SsrDocumentTypes.CONTRACT_DIGITAL_SIGN;
    }

    /**
     * Получение документа многостороннего подписания договора с использованием УКЭП.
     *
     * @param contractAppointmentId ИД заявления
     * @return многостороннее подписание договора с использованием УКЭП
     */
    public Optional<ContractDigitalSignDocument> findByContractAppointmentId(final String contractAppointmentId) {
        final List<DocumentData> contractDigitalSignList = contractDigitalSignDao
            .findByContractAppointmentId(contractAppointmentId);

        if (contractDigitalSignList.size() > 1) {
            log.warn(
                "More than one contract digital sign have been found; contractAppointmentId {}",
                contractAppointmentId
            );
        }

        return contractDigitalSignList
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Получение действующего многостороннего подписания договора с использованием УКЭП по ИД договора.
     *
     * @param contractOrderId ИД договора
     * @return многостороннее подписание договора с использованием УКЭП
     */
    public Optional<ContractDigitalSignDocument> findActiveByContractOrderId(final String contractOrderId) {
        return contractDigitalSignDao.findActiveByContractOrderId(contractOrderId)
            .map(this::parseDocumentData);
    }

    /**
     * Получение документа многостороннего подписания договора с использованием УКЭП по ЕНО уведомления.
     *
     * @param notificationEno ЕНО уведомления
     * @return многостороннее подписание договора с использованием УКЭП
     */
    public Optional<ContractDigitalSignDocument> findByNotificationEno(final String notificationEno) {
        final List<DocumentData> contractDigitalSignList = contractDigitalSignDao
            .findByNotificationEno(notificationEno);

        if (contractDigitalSignList.size() > 1) {
            log.warn("More than one contract digital sign have been found; notification eno {}", notificationEno);
        }

        return contractDigitalSignList
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    public List<ContractDigitalSignDocument> findActualByEmployeeLogin(final String login) {
        return contractDigitalSignDao.findActualByEmployeeLogin(login)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<ContractDigitalSignDocument> findUnassignedOrActualByEmployeeLogin(final String login) {
        return contractDigitalSignDao.findUnassignedOrActualByEmployeeLogin(login)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<ContractDigitalSignDocument> fetchAllForToday() {
        return contractDigitalSignDao.fetchAllForToday()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }
}
