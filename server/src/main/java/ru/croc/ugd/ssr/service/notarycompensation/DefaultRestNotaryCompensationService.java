package ru.croc.ugd.ssr.service.notarycompensation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationMoneyNotPayedDto;
import ru.croc.ugd.ssr.dto.notarycompensation.RestNotaryCompensationRejectDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.NotaryCompensationElkNotificationService;
import ru.croc.ugd.ssr.mapper.NotaryCompensationMapper;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.service.document.NotaryCompensationDocumentService;

@Service
@AllArgsConstructor
public class DefaultRestNotaryCompensationService implements RestNotaryCompensationService {

    private final NotaryCompensationDocumentService notaryCompensationDocumentService;
    private final NotaryCompensationMapper notaryCompensationMapper;
    private final NotaryCompensationElkNotificationService notaryCompensationElkNotificationService;

    @Override
    public RestNotaryCompensationDto fetchById(final String notaryCompensationDocumentId) {
        final NotaryCompensationDocument notaryCompensationDocument =
            notaryCompensationDocumentService.fetchDocument(notaryCompensationDocumentId);

        final NotaryCompensationData notaryCompensationData =
            notaryCompensationDocument.getDocument().getNotaryCompensationData();

        return notaryCompensationMapper.toRestNotaryCompensation(notaryCompensationData);
    }

    @Override
    public void confirm(final String notaryCompensationDocumentId) {
        final NotaryCompensationDocument notaryCompensationDocument =
            notaryCompensationDocumentService.fetchDocument(notaryCompensationDocumentId);

        final NotaryCompensationData notaryCompensationData = notaryCompensationDocument
            .getDocument()
            .getNotaryCompensationData();

        if (!NotaryCompensationFlowStatus.REGISTERED.getId().equals(notaryCompensationData.getStatusId())) {
            throw new SsrException("Заявление на компенсацию находится в некорретном статусе");
        }

        notaryCompensationData.setStatusId(NotaryCompensationFlowStatus.CONFIRMED.getId());

        notaryCompensationDocumentService.updateDocument(
            notaryCompensationDocument,
            "confirm"
        );

        notaryCompensationElkNotificationService.sendStatus(
            NotaryCompensationFlowStatus.CONFIRMED,
            notaryCompensationDocument
        );
    }

    @Override
    public void reject(final String notaryCompensationDocumentId, final RestNotaryCompensationRejectDto rejectDto) {
        final NotaryCompensationDocument notaryCompensationDocument =
            notaryCompensationDocumentService.fetchDocument(notaryCompensationDocumentId);

        final NotaryCompensationData notaryCompensationData = notaryCompensationDocument
            .getDocument()
            .getNotaryCompensationData();

        if (!NotaryCompensationFlowStatus.REGISTERED.getId().equals(notaryCompensationData.getStatusId())) {
            throw new SsrException("Заявление на компенсацию находится в некорретном статусе");
        }

        notaryCompensationData.setStatusId(NotaryCompensationFlowStatus.REJECTED.getId());
        notaryCompensationData.setServiceRejectReason(rejectDto.getReason());

        notaryCompensationDocumentService.updateDocument(
            notaryCompensationDocument,
            "reject"
        );

        notaryCompensationElkNotificationService.sendStatus(
            NotaryCompensationFlowStatus.REJECTED,
            notaryCompensationDocument
        );
    }

    @Override
    public void moneyNotPayed(
        final String notaryCompensationDocumentId,
        final RestNotaryCompensationMoneyNotPayedDto moneyNotPayedDto
    ) {
        final NotaryCompensationDocument notaryCompensationDocument =
            notaryCompensationDocumentService.fetchDocument(notaryCompensationDocumentId);

        final NotaryCompensationData notaryCompensationData = notaryCompensationDocument
            .getDocument()
            .getNotaryCompensationData();

        if (!NotaryCompensationFlowStatus.CONFIRMED.getId().equals(notaryCompensationData.getStatusId())) {
            throw new SsrException("Заявление на компенсацию находится в некорретном статусе");
        }

        notaryCompensationData.setStatusId(NotaryCompensationFlowStatus.MONEY_NOT_PAYED.getId());
        notaryCompensationData.setMoneyIsNotPayedReason(moneyNotPayedDto.getReason());

        notaryCompensationDocumentService.updateDocument(
            notaryCompensationDocument,
            "moneyNotPayed"
        );

        notaryCompensationElkNotificationService.sendStatus(
            NotaryCompensationFlowStatus.MONEY_NOT_PAYED,
            notaryCompensationDocument
        );
    }
}