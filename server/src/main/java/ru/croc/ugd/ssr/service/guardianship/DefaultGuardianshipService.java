package ru.croc.ugd.ssr.service.guardianship;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.GuardianshipRequestDocumentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultGuardianshipService implements GuardianshipService {
    private static final String PROCESS_KEY_HANDLE_GUARDIANSHIP_REQUEST = "ugdssrGuardianship_enterDetails";

    private final BpmService bpmService;
    private final GuardianshipRequestDocumentService guardianshipRequestDocumentService;

    @Override
    public void startHandleGuardianshipRequestTask(
        final GuardianshipRequestDocument guardianshipRequestDocument
    ) {
        final Map<String, String> variablesMap = new HashMap<>();

        variablesMap.put(BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, guardianshipRequestDocument.getId());

        final String processInstanceId = bpmService
            .startNewProcess(PROCESS_KEY_HANDLE_GUARDIANSHIP_REQUEST, variablesMap);

        guardianshipRequestDocument
            .getDocument()
            .getGuardianshipRequestData()
            .setProcessInstanceId(processInstanceId);

        guardianshipRequestDocumentService.updateDocument(
            guardianshipRequestDocument.getId(),
            guardianshipRequestDocument,
            true,
            true,
            "Старт задачи ugdssrGuardianship_enterDetails"
        );
    }

    @Override
    public void forceFinishProcess(final GuardianshipRequestDocument guardianshipRequestDocument) {
        final GuardianshipRequestData guardianshipRequest = guardianshipRequestDocument
            .getDocument()
            .getGuardianshipRequestData();

        ofNullable(guardianshipRequest.getProcessInstanceId())
            .ifPresent(bpmService::deleteProcessInstance);
    }

    @Override
    public void checkGuardianshipRequestAndStartHandle(final String affairId) {
        final List<GuardianshipRequestDocument> guardianshipRequestDocuments =
            guardianshipRequestDocumentService.fetchByAffairIdAndProcessInstanceIdIsNull(affairId);
        if (guardianshipRequestDocuments.size() > 1) {
            log.error("Found more than one active guardianship request for affair id {}", affairId);
        } else {
            guardianshipRequestDocuments.stream()
                .findFirst()
                .ifPresent(this::startHandleGuardianshipRequestTask);
        }
    }
}
