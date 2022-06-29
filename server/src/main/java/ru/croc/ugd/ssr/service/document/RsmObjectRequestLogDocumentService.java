package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.rsm.RsmRequestDto;
import ru.croc.ugd.ssr.dto.rsm.RsmResponseDto;
import ru.croc.ugd.ssr.mapper.RsmObjectRequestLogMapper;
import ru.croc.ugd.ssr.model.rsm.RsmObjectRequestLogDocument;
import ru.croc.ugd.ssr.rsm.RsmObjectRequestLogData;
import ru.croc.ugd.ssr.rsm.RsmObjectRequestLogStatus;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDateTime;

/**
 * RsmObjectRequestLogDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class RsmObjectRequestLogDocumentService extends SsrAbstractDocumentService<RsmObjectRequestLogDocument> {

    private final RsmObjectRequestLogMapper rsmObjectRequestLogMapper;

    @NotNull
    @Override
    public DocumentType<RsmObjectRequestLogDocument> getDocumentType() {
        return SsrDocumentTypes.RSM_OBJECT_REQUEST_LOG;
    }

    /**
     * Save log when process request started.
     *
     * @param request request
     * @return RsmObjectRequestLogDocument
     */
    public RsmObjectRequestLogDocument startProcessRequest(final RsmRequestDto request) {
        final RsmObjectRequestLogDocument document = rsmObjectRequestLogMapper.toRsmObjectRequestLogDocument(request);
        return createDocument(document, false, "startProcessRequest");
    }

    /**
     * Update log when process ended.
     *
     * @param id log id
     */
    public void endProcessRequest(final String id, final RsmResponseDto response) {
        final RsmObjectRequestLogDocument rsmObjectRequestLogDocument = fetchDocument(id);

        final RsmObjectRequestLogData rsmObjectRequestLogData = rsmObjectRequestLogDocument
            .getDocument()
            .getRsmObjectRequestLogData();

        rsmObjectRequestLogData.setEndDateTime(LocalDateTime.now());
        rsmObjectRequestLogData.setStatus(RsmObjectRequestLogStatus.PROCESSED);
        rsmObjectRequestLogData.setResponseEno(response.getResponseEno());
        rsmObjectRequestLogData.setResponseEtpmvMessageId(response.getEtpmvMessageId());
        rsmObjectRequestLogData.setResponse(rsmObjectRequestLogMapper.toRsmResponse(response.getRsmObjectResponse()));

        updateDocument(id, rsmObjectRequestLogDocument, true, false, "endProcessRequest");
    }

    /**
     * Update log when process failed.
     *
     * @param id log id
     */
    public void failProcessRequest(final String id) {
        final RsmObjectRequestLogDocument rsmObjectRequestLogDocument = fetchDocument(id);

        final RsmObjectRequestLogData rsmObjectRequestLogData = rsmObjectRequestLogDocument
            .getDocument()
            .getRsmObjectRequestLogData();

        rsmObjectRequestLogData.setEndDateTime(LocalDateTime.now());
        rsmObjectRequestLogData.setStatus(RsmObjectRequestLogStatus.FAILED);

        updateDocument(id, rsmObjectRequestLogDocument, true, false, "failedProcessRequest");
    }
}
