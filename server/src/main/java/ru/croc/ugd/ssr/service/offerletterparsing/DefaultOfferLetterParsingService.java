package ru.croc.ugd.ssr.service.offerletterparsing;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsingType;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.OfferLetterParsingDocumentService;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultOfferLetterParsingService implements OfferLetterParsingService {

    private final BpmService bpmService;
    private final OfferLetterParsingDocumentService offerLetterParsingDocumentService;

    @Override
    public void finishBpmProcessIfNeeded(final OfferLetterParsingDocument offerLetterParsingDocument) {
        final OfferLetterParsingType offerLetterParsingData =
            offerLetterParsingDocument.getDocument().getOfferLetterParsingData();

        if (isNull(offerLetterParsingData.getCompletionDateTime())) {
            finishBpmProcess(offerLetterParsingData.getProcessInstanceId());

            offerLetterParsingData.setCompletionDateTime(LocalDateTime.now());
            offerLetterParsingDocumentService.updateDocument(
                offerLetterParsingDocument.getId(), offerLetterParsingDocument, true, true, null
            );
        }
    }

    private void finishBpmProcess(final String processInstanceId) {
        if (isNotBlank(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn("Unable to finish bpm process for offer letter parsing due to: {}", e.getMessage(), e);
            }
        }
    }
}
