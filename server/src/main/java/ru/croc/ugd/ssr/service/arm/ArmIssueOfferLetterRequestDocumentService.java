package ru.croc.ugd.ssr.service.arm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.ArmIssueOfferLetterRequest;
import ru.croc.ugd.ssr.ArmIssueOfferLetterRequestType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.model.ArmIssueOfferLetterRequestDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RoomService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.bpm.model.FormProperty;
import ru.reinform.cdp.bpm.model.ProcessInstance;
import ru.reinform.cdp.bpm.service.BpmRuntimeService;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Сервис ArmIssueOfferLetterRequestDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArmIssueOfferLetterRequestDocumentService
        extends AbstractDocumentService<ArmIssueOfferLetterRequestDocument> {

    private final RoomService roomService;
    private final FlatService flatService;
    private final PersonDocumentService personDocumentService;
    private final JsonMapper jsonMapper;
    private final BpmRuntimeService bpmRuntimeService;

    private ArmIssueOfferLetterRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private ArmIssueOfferLetterRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, ArmIssueOfferLetterRequestDocument.class);
    }

    @Nonnull
    @Override
    public DocumentType<ArmIssueOfferLetterRequestDocument> getDocumentType() {
        return SsrDocumentTypes.ARM_ISSUE_OFFER_LETTER_REQUEST;
    }

    /**
     * Создание документа по БП.
     *
     * @param personId ид жителя
     * @return ArmIssueOfferLetterRequestDocument
     */
    @Nonnull
    @Transactional
    public ArmIssueOfferLetterRequestDocument createDocumentWithProcess(String personId) {
        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        PersonType personData = personDocument.getDocument().getPersonData();
        RoomType room = roomService.fetchRoom(personData.getRoomID());

        ArmIssueOfferLetterRequestDocument armIssueOfferLetterRequestDoc = new ArmIssueOfferLetterRequestDocument();
        ArmIssueOfferLetterRequest armIssueOfferLetterRequest = new ArmIssueOfferLetterRequest();
        armIssueOfferLetterRequestDoc.setDocument(armIssueOfferLetterRequest);
        ArmIssueOfferLetterRequestType main = new ArmIssueOfferLetterRequestType();
        armIssueOfferLetterRequest.setMain(main);

        main.setPersonId(personId);
        main.setTaskStatus("Не назначена");
        main.setTaskCreationDate(LocalDateTime.now());
        main.setFio(personData.getFIO());

        String from = RealEstateUtils.getFlatAddress(flatService
            .fetchRealEstateAndFlat(personData.getFlatID()));
        if (from != null) {
            if (room != null && room.getRoomL5VALUE() != null && !room.getRoomL5VALUE().isEmpty()) {
                from += ", комната " + room.getRoomL5VALUE();
            }
            main.setFrom(from);
        }
        main.setTo(personData.getSettleAddress());
        main.setResettlementType("равнозначное жилье (ДГИ)");


        ArmIssueOfferLetterRequestDocument requestDocument = createDocument(
                armIssueOfferLetterRequestDoc, false, "bpm create armIssueOfferLetterRequestDoc"
        );

        LocalDateTime dueDate = LocalDateTime.now().withHour(20).truncatedTo(ChronoUnit.HOURS);

        return startDocumentProcess(requestDocument, dueDate);
    }

    @Nonnull
    private ArmIssueOfferLetterRequestDocument startDocumentProcess(
            @Nonnull ArmIssueOfferLetterRequestDocument document,
            LocalDateTime dueDate
    ) {
        try {
            List<FormProperty> variables = new ArrayList<>();
            variables.add(new FormProperty("EntityIdVar", document.getId()));
            variables.add(new FormProperty("dueDate", dueDate.toString()));
            ProcessInstance process = bpmRuntimeService.startProcessViaForm(
                    "ugdssrArm_issueOfferLetterRequest",
                    variables
            );

            return document;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}(notes: {1})", RIExceptionUtils.method(), "")
                    .withUserMessage("Ошибка создания документа-заявки.");
        }
    }
}
