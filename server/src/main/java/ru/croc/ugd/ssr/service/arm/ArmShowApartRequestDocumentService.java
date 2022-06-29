package ru.croc.ugd.ssr.service.arm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ugd.ssr.ArmShowApartRequest;
import ru.croc.ugd.ssr.ArmShowApartRequestType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.model.ArmShowApartRequestDocument;
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
 * Сервис по работе с сущностью "показ квартиры".
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArmShowApartRequestDocumentService extends AbstractDocumentService<ArmShowApartRequestDocument> {

    private final RoomService roomService;
    private final FlatService flatService;
    private final PersonDocumentService personDocumentService;
    private final JsonMapper jsonMapper;
    private final BpmRuntimeService bpmRuntimeService;

    private ArmShowApartRequestDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private ArmShowApartRequestDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, ArmShowApartRequestDocument.class);
    }

    @Nonnull
    @Override
    public DocumentType<ArmShowApartRequestDocument> getDocumentType() {
        return SsrDocumentTypes.ARM_SHOW_APART_REQUEST;
    }

    /**
     * Создание нового документа по id пользователя.
     *
     * @param personId айди пользователя
     * @return новый документ
     */
    @Nonnull
    @Transactional
    public ArmShowApartRequestDocument createDocumentWithProcess(String personId) {
        PersonDocument personDocument = personDocumentService.fetchDocument(personId);
        PersonType personData = personDocument.getDocument().getPersonData();
        RoomType room = roomService.fetchRoom(personData.getRoomID());

        ArmShowApartRequestDocument armShowApartRequestDocument = new ArmShowApartRequestDocument();
        ArmShowApartRequest armShowApartRequest = new ArmShowApartRequest();
        armShowApartRequestDocument.setDocument(armShowApartRequest);
        ArmShowApartRequestType main = new ArmShowApartRequestType();
        armShowApartRequest.setMain(main);

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

        List<ArmShowApartRequestType.History> history = main.getHistory();
        for (ResettlementHistory resettlementHistory : personData.getResettlementHistory()) {
            if (resettlementHistory.getStatus().equals("Выполнен показ квартиры")) {
                ArmShowApartRequestType.History tillHistory = new ArmShowApartRequestType.History();
                tillHistory.setDate(resettlementHistory.getDate());
                tillHistory.setAnnotation(resettlementHistory.getAnnotationShowApart());
                history.add(tillHistory);
            }
        }

        ArmShowApartRequestDocument requestDocument = createDocument(
            armShowApartRequestDocument, false, "bpm create armShowApartRequestDocument"
        );

        LocalDateTime dueDate = LocalDateTime.now().withHour(20).truncatedTo(ChronoUnit.HOURS);

        return startDocumentProcess(requestDocument, dueDate);
    }

    @Nonnull
    private ArmShowApartRequestDocument startDocumentProcess(
        @Nonnull ArmShowApartRequestDocument document,
        LocalDateTime dueDate
    ) {
        try {
            List<FormProperty> variables = new ArrayList<>();
            variables.add(new FormProperty("EntityIdVar", document.getId()));
            variables.add(new FormProperty("dueDate", dueDate.toString()));
            ProcessInstance process = bpmRuntimeService.startProcessViaForm(
                "ugdssrArm_showApartRequest",
                variables
            );

            return document;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0}(notes: {1})", RIExceptionUtils.method(), "")
                .withUserMessage("Ошибка создания документа-заявки.");
        }
    }
}
