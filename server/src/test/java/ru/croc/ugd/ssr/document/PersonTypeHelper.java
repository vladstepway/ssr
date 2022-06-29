package ru.croc.ugd.ssr.document;

import static ru.croc.ugd.ssr.TestUtils.readFromPath;

import ru.croc.ugd.ssr.JsonMapperUtil;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PersonTypeHelper {
    private final JsonMapper jsonMapper = JsonMapperUtil.getJsonMapper();
    private final PersonDocumentUtils personDocumentUtils = new PersonDocumentUtils();

    public PersonType getPersonType() {
        return new PersonType();
    }

    public PersonType getPersonTypeUnder18() {
        final PersonType personType = getPersonType();
        personType.setBirthDate(LocalDate.now());
        return personType;
    }

    public PersonType getPersonTypeAdult() {
        final PersonType personType = getPersonType();
        personType.setBirthDate(LocalDate.now().minus(19, ChronoUnit.YEARS));
        return personType;
    }

    public PersonDocument readPersonDocumentFromFile(final String testPersonFile) {
        final String jsonString = readFromPath(testPersonFile);
        return parseDocumentJson(jsonString);
    }

    public PersonType readPersonTypeFromFile(final String testPersonFile) {
        final PersonDocument parseDocument = readPersonDocumentFromFile(testPersonFile);
        return personDocumentUtils.getPersonDataFromDocumentNullSafe(parseDocument).orElse(null);
    }

    public PersonDocument wrapToDocument(final PersonType personType) {
        final PersonDocument personDocument = new PersonDocument();
        final Person person = new Person();
        person.setPersonData(personType);
        personDocument.setDocument(person);
        return personDocument;
    }

    private PersonDocument parseDocumentJson(final String json) {
        return jsonMapper.readObject(json, PersonDocument.class);
    }
}
