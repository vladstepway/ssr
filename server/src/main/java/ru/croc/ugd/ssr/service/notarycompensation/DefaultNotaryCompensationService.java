package ru.croc.ugd.ssr.service.notarycompensation;

import static java.util.Objects.isNull;
import static java.util.Optional.of;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.NotaryCompensationElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.mq.listener.notarycompensation.EtpNotaryCompensationMapper;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensation;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationData;
import ru.croc.ugd.ssr.service.document.NotaryCompensationDocumentService;

/**
 * DefaultNotaryCompensationService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultNotaryCompensationService implements NotaryCompensationService {
    private final MessageUtils messageUtils;
    private EtpNotaryCompensationMapper etpNotaryCompensationMapper;
    private NotaryCompensationDocumentService notaryCompensationDocumentService;
    private final NotaryCompensationElkNotificationService notaryCompensationElkNotificationService;

    @Override
    public void processRegistration(final CoordinateMessage coordinateMessage) {
        try {
            final NotaryCompensationDocument notaryCompensationDocument = etpNotaryCompensationMapper
                .toNotaryCompensationDocument(coordinateMessage);

            if (isNull(notaryCompensationDocument)) {
                throw new SsrException("Unable to parse notary compensation request: " + coordinateMessage);
            }
            if (!isDuplicate(notaryCompensationDocument)) {
                processRegistration(notaryCompensationDocument);
            } else {
                log.warn("Notary compensation has been already registered: {}", coordinateMessage);
            }
        } catch (Exception e) {
            log.warn("Unable to save notary compensation due to: {}", e.getMessage(), e);
            notaryCompensationElkNotificationService.sendStatus(
                NotaryCompensationFlowStatus.TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );
        }
    }

    private void processRegistration(final NotaryCompensationDocument notaryCompensationDocument) {
        //TODO: Alexander: Konstantin: возможно пригодится
        // final List<ru.mos.gu.service._091201.ServiceProperties.RightHolderList.RightHolder> rightHolders =
        //    etpNotaryCompensationMapper.toRightHoldersList(coordinateMessage);

        notaryCompensationDocumentService.createDocument(notaryCompensationDocument, true, "processRegistration");

        notaryCompensationElkNotificationService.sendStatus(
            NotaryCompensationFlowStatus.ACCEPTED, notaryCompensationDocument
        );
    }

    private boolean isDuplicate(final NotaryCompensationDocument notaryCompensationDocument) {
        return of(notaryCompensationDocument.getDocument())
            .map(NotaryCompensation::getNotaryCompensationData)
            .map(NotaryCompensationData::getEno)
            .filter(notaryCompensationDocumentService::existsByEno)
            .isPresent();
    }
}
