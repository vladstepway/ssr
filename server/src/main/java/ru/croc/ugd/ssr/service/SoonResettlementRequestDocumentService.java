package ru.croc.ugd.ssr.service;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.croc.ugd.ssr.SoonResettlementRequest;
import ru.croc.ugd.ssr.SoonResettlementRequestType;
import ru.croc.ugd.ssr.model.SoonResettlementRequestDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.service.BpmRuntimeService;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис по работе с сущностью "скорое расселение".
 */
@Service
@AllArgsConstructor
public class SoonResettlementRequestDocumentService extends AbstractDocumentService<SoonResettlementRequestDocument> {

    private final BpmRuntimeService bpmRuntimeService;

    @Nonnull
    @Override
    public DocumentType<SoonResettlementRequestDocument> getDocumentType() {
        return SsrDocumentTypes.SOON_RESETTLEMENT_REQUEST;
    }

    /**
     * Создание нового документа из процесса.
     *
     * @param notes комментарий к операции
     * @return новый документ
     */
    @Nonnull
    @Transactional
    public SoonResettlementRequestDocument createDocumentWithProcess(
        @ApiParam(value = "Комментарий к операции") @RequestParam(required = false) String notes
    ) {
        SoonResettlementRequestDocument document = new SoonResettlementRequestDocument();
        SoonResettlementRequest soonResettlementRequest = new SoonResettlementRequest();
        SoonResettlementRequestType main = new SoonResettlementRequestType();
        soonResettlementRequest.setMain(main);
        document.setDocument(soonResettlementRequest);
        SoonResettlementRequestDocument requestDocument = createDocument(document, true, notes);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return startDocumentProcess(requestDocument, username, notes);
    }

    @Nonnull
    private SoonResettlementRequestDocument startDocumentProcess(
        @Nonnull SoonResettlementRequestDocument document,
        String applicant,
        @Nullable String notes
    ) {
        try {
            List<FormProperty> variables = new ArrayList<>();
            variables.add(new FormProperty("EntityIdVar", document.getId()));
            variables.add(new FormProperty("applicant", applicant));
            ProcessInstance process = bpmRuntimeService.startProcessViaForm(
                "ugdssr_objectSoonResettlementRequest",
                variables
            );

            return document;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}(notes: {1})", RIExceptionUtils.method(), notes)
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }

}
