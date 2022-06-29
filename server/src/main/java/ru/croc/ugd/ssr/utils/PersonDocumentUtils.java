package ru.croc.ugd.ssr.utils;

import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Утилита для извлечения данных.
 */
//TODO replace with general document util.
@Service
public class PersonDocumentUtils {

    //TODO Document Type can be extracted using reflection.

    /**
     * Извлекает данные PersonType из PersonDocument.
     *
     * @param personDocument данные жильца.
     * @return PersonType
     */
    public Optional<PersonType> getPersonDataFromDocumentNullSafe(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData);
    }

    /**
     * Извлекает данные PersonType из PersonDocument.
     *
     * @param personDocument данные жильца.
     * @return PersonType
     */
    public PersonType getPersonDataFromDocument(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .get();
    }

    /**
     * Extracts list of person types.
     * @param personDocuments person documents.
     * @return list of extracted person types.
     */
    public List<PersonType> getPersonsDataFromDocuments(final List<PersonDocument> personDocuments) {
        return personDocuments.stream()
            .map(this::getPersonDataFromDocumentNullSafe)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    /**
     * Map personId to person.
     * @param personDocumentDataList personDocumentDataList.
     * @return personId to person map
     */
    public Map<String, PersonType> mapPersonIdToPerson(final List<PersonDocument> personDocumentDataList) {
        return personDocumentDataList
            .stream()
            .collect(Collectors.toMap(PersonDocument::getId, this::getPersonDataFromDocument));
    }

    /**
     * mapPersonsToExpandedPersonTypes.
     * @param personDocumentDataList personDocumentDataList.
     * @return personId to person map
     */
    public List<ValidatablePersonData> mapPersonsToExpandedPersonTypes(
        final List<PersonDocument> personDocumentDataList
    ) {
        return mapPersonIdToPerson(personDocumentDataList)
            .entrySet()
            .stream()
            .map(stringPersonTypeEntry -> ValidatablePersonData.builder()
                .person(stringPersonTypeEntry.getValue())
                .personId(stringPersonTypeEntry.getKey())
                .build())
            .collect(Collectors.toList());
    }
}
