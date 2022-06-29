package ru.croc.ugd.ssr.service.affaircollation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.affaircollation.AffairCollationData;
import ru.croc.ugd.ssr.db.dao.AffairCollationDao;
import ru.croc.ugd.ssr.dto.affaircollation.RestAffairCollationDto;
import ru.croc.ugd.ssr.integration.service.flows.AffairCollationFlowService;
import ru.croc.ugd.ssr.integration.service.flows.integrationflowqueue.IntegrationFlowQueueService;
import ru.croc.ugd.ssr.mapper.AffairCollationMapper;
import ru.croc.ugd.ssr.model.affairCollation.AffairCollationDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationReportType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAffairCollationType;
import ru.croc.ugd.ssr.service.document.AffairCollationDocumentService;

import java.time.LocalDateTime;
import javax.transaction.Transactional;

@Slf4j
@Service
public class DefaultAffairCollationService implements AffairCollationService {

    private final AffairCollationDocumentService affairCollationDocumentService;
    private final AffairCollationMapper affairCollationMapper;
    private final AffairCollationFlowService affairCollationFlowService;
    private final AffairCollationDao affairCollationDao;
    private final AffairCollationEmailNotificationService affairCollationEmailNotificationService;
    private final IntegrationFlowQueueService integrationFlowQueueService;

    @Value("${ugd.ssr.affair-collation.enabled:false}")
    private boolean isAffairCollationEnabled;

    public DefaultAffairCollationService(
        final AffairCollationDocumentService affairCollationDocumentService,
        final AffairCollationMapper affairCollationMapper,
        final AffairCollationFlowService affairCollationFlowService,
        final AffairCollationDao affairCollationDao,
        final AffairCollationEmailNotificationService affairCollationEmailNotificationService,
        @Lazy
        final IntegrationFlowQueueService integrationFlowQueueService
    ) {
        this.affairCollationDocumentService = affairCollationDocumentService;
        this.affairCollationMapper = affairCollationMapper;
        this.affairCollationFlowService = affairCollationFlowService;
        this.affairCollationDao = affairCollationDao;
        this.affairCollationEmailNotificationService = affairCollationEmailNotificationService;
        this.integrationFlowQueueService = integrationFlowQueueService;
    }

    @Override
    public void create(final RestAffairCollationDto restAffairCollationDto, final boolean isOperatorRequest) {
        if (!isAffairCollationEnabled) {
            log.info("Affair collation request sending is disabled");
            return;
        }

        if (!isOperatorRequest) {
            final boolean existsActiveRequest = affairCollationDao.existsByAffairIdAndRequestDateTimeAfter(
                restAffairCollationDto.getAffairId(),
                LocalDateTime.now().minusDays(1)
            );

            if (existsActiveRequest) {
                log.info(
                    "Skip duplicate affair collation request: affairId = {}", restAffairCollationDto.getAffairId()
                );
                return;
            }
        }

        final AffairCollationDocument affairCollationDocument = affairCollationMapper
            .toAffairCollationDocument(restAffairCollationDto, isOperatorRequest);

        affairCollationDocumentService.createDocument(affairCollationDocument, false, "create");

        final SuperServiceDGPAffairCollationType superServiceDgpAffairCollationType = affairCollationMapper
            .toSuperServiceDgpAffairCollationType(affairCollationDocument);
        final String requestEno = affairCollationFlowService.send(superServiceDgpAffairCollationType);

        final AffairCollationData affairCollationData = affairCollationDocument
            .getDocument()
            .getAffairCollationData();

        affairCollationData.setRequestDateTime(LocalDateTime.now());
        affairCollationData.setRequestServiceNumber(requestEno);

        affairCollationDocumentService.updateDocument(affairCollationDocument, "flowSent");
    }

    @Override
    @Transactional
    public void processReport(
        final String reportEno,
        final SuperServiceDGPAffairCollationReportType superServiceDgpAffairCollationReportType
    ) {
        final AffairCollationDocument affairCollationDocument = affairCollationDocumentService
            .fetchDocument(superServiceDgpAffairCollationReportType.getRequestId());

        final AffairCollationData affairCollation = affairCollationDocument.getDocument().getAffairCollationData();

        affairCollation.setReportServiceNumber(reportEno);
        affairCollation.setResponseDateTime(LocalDateTime.now());
        affairCollation.getResponseServiceNumbers()
            .addAll(superServiceDgpAffairCollationReportType.getServiceNumbers());

        affairCollationDocumentService.updateDocument(affairCollationDocument, "processReport");

        integrationFlowQueueService.enablePostponedFlowMessages(
            superServiceDgpAffairCollationReportType.getServiceNumbers()
        );

        if (affairCollation.isOperatorRequest()) {
            affairCollationEmailNotificationService.sendNotificationEmail(affairCollationDocument);
        }
    }

}
