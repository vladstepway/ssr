package ru.croc.ugd.ssr.service.shipping;

import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_COMPLETE;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_REJECTED;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.dto.shipping.ShippingResultDto;
import ru.croc.ugd.ssr.exception.NoProcessException;
import ru.croc.ugd.ssr.integration.service.notification.ShippingElkNotificationService;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.model.Variable;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Периодический поиск документов, по которым необходимо зафиксировать результаты переезда,
 * старт бизнес-процессов, фиксация результатов.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShippingResultService {

    private static final String PROCESS_KEY_FINISH_SHIPPING = "ugdssrShipping_finishShipping";

    private final BpmService bpmService;
    private final ShippingApplicationDao shippingApplicationDao;
    private final ShippingApplicationDocumentService shippingApplicationDocumentService;
    private final ShippingElkNotificationService shippingElkNotificationService;

    /**
     * Поиск подходящих документов, старт процессов.
     */
    @Scheduled(cron = "${schedulers.shipping-check.cron:50 * * * * ?}")
    public void startBusinessProcesses() {

        log.info("Processing started");

        List<DocumentData> dataList = shippingApplicationDao
            .findApplicationsForBusinessProcessStart();

        log.info("Found {} items for processing", dataList.size());

        dataList.forEach(this::processApplication);

        log.info("Processing finished");

    }

    /**
     * Обрабатываем конкретный документ - запускаем процесс, фиксируем его id в json.
     *
     * @param data - документ.
     */
    public void processApplication(DocumentData data) {

        log.debug("About to process: {}", data);

        try {

            ShippingApplicationDocument application = shippingApplicationDocumentService
                .fetchDocument(data.getId());
            application
                .getDocument()
                .getShippingApplicationData()
                .setProcessInstanceId(
                    bpmService.startNewProcess(
                        PROCESS_KEY_FINISH_SHIPPING,
                        Collections.singletonMap(
                            BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, data.getId()
                        )
                    )
                );

            shippingApplicationDocumentService.updateDocument(
                data.getId(),
                application,
                true,
                true,
                null
            );

            log.debug("After processing: {}", data);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Фиксация результатов выполнения переезда.
     *
     * @param dto - результаты.
     *
     */
    public void setShippingResult(ShippingResultDto dto) {

        final ProcessInstance instance = bpmService.getProcessInstance(dto.getProcessInstanceId());
        if (instance == null) {
            throw new NoProcessException();
        }
        Optional<Variable> id = instance.getVariables()
            .stream()
            .filter(
                k -> k.getName().equals(
                    BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR
                )
            ).findFirst();

        if (id.isPresent()) {
            ShippingApplicationDocument document = shippingApplicationDocumentService
                .fetchDocument(id.get().getValue().toString());
            ShippingApplicationType data = document.getDocument()
                .getShippingApplicationData();
            data.setStatus(
                dto.isResult() ? SHIPPING_COMPLETE.getDescription() : SHIPPING_REJECTED.getDescription()
            );
            if (!dto.isResult()) {
                data.setDeclineDateTime(LocalDateTime.now());
                data.setDeclineReason(dto.getComment());
            }
            shippingApplicationDocumentService.updateDocument(
                id.get().getValue().toString(),
                document,
                true,
                true,
                null
            );

            shippingElkNotificationService.sendStatus(
                dto.isResult() ? SHIPPING_COMPLETE : SHIPPING_REJECTED,
                document
            );
        } else {
            log.error("Can't find documentId from processInstance {}", dto.getProcessInstanceId());
        }

        List<FormProperty> props = new ArrayList<>();
        props.add(new FormProperty("shippingResult", String.valueOf(dto.isResult())));
        if (dto.getComment() != null) {
            props.add(new FormProperty("shippingResultComment", dto.getComment()));
        }

        bpmService.completeTaskViaForm(dto.getTaskId(), props);

    }

}
