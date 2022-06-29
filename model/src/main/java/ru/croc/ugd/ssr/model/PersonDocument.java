package ru.croc.ugd.ssr.model;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.reinform.cdp.document.model.DocumentAbstract;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;

/**
 * Класс-обертка основного бизнес-документа системы с root-элементом.
 */
@Getter
@Setter
public class PersonDocument extends DocumentAbstract<Person> {

    @Nonnull
    @JsonProperty("Person")  //должно соответствовать корневому элементу XSD
    private Person document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String id) {
        document.setDocumentID(id);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String id) {
        document.setFolderGUID(id);
    }

    public void addFlatDemoItem(final PersonType.FlatDemo.FlatDemoItem flatDemoItem) {
        final PersonType person = document.getPersonData();
        final PersonType.FlatDemo flatDemo = ofNullable(person.getFlatDemo())
            .orElseGet(PersonType.FlatDemo::new);
        flatDemo.getFlatDemoItem().add(flatDemoItem);

        person.setFlatDemo(flatDemo);
    }

    public void addResettlementHistory(final String eventId, final String dataId, final String annotation) {
        final ResettlementHistory resettlementHistory = createResettlementHistory(eventId, dataId, annotation);

        addResettlementHistory(resettlementHistory);
    }

    public void addResettlementHistory(final ResettlementHistory resettlementHistory) {
        document.getPersonData().getResettlementHistory().add(resettlementHistory);
    }

    private ResettlementHistory createResettlementHistory(
        final String eventId, final String dataId, final String annotation
    ) {
        final ResettlementHistory resettlementHistory = new ResettlementHistory();

        resettlementHistory.setEventDateTime(LocalDateTime.now());
        resettlementHistory.setAnnotation(annotation);
        resettlementHistory.setDataId(dataId);
        resettlementHistory.setEventId(eventId);

        return resettlementHistory;
    }
}
