package ru.croc.ugd.ssr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Сервис по сдаче/выдаче ключей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultKeysService implements KeysService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final PersonDocumentService personDocumentService;

    @Override
    @Async
    public void processUpdateKeysByUnoms(List<String> unoms) {
        final List<PersonDocument> personDocumentList =
            personDocumentService.fetchByUnoms(unoms);

        personDocumentList.forEach(personDocument -> {

            final PersonType person = personDocument.getDocument().getPersonData();

            personDocumentService
                .getContractSignDate(person)
                .ifPresent(contractSignDate -> {
                    if (person.getKeysIssue() == null && person.getReleaseFlat() == null) {
                        final String dataId = UUID.randomUUID().toString();

                        person.setKeysIssue(new PersonType.KeysIssue());
                        person.getKeysIssue().setDataId(dataId);
                        person.getKeysIssue().setActDate(contractSignDate);
                        person.getKeysIssue().setActNum("Б/Н");

                        person.setReleaseFlat(new PersonType.ReleaseFlat());
                        person.getReleaseFlat().setActDate(contractSignDate);
                        person.getReleaseFlat().setActNum("Б/Н");

                        personDocument.addResettlementHistory(
                            "19",
                            dataId,
                            "Номер акта Б/Н от " + contractSignDate.format(DATE_FORMATTER)
                        );

                        personDocument.addResettlementHistory(
                            "20",
                            null,
                            "Отселяемая квартира освобождена жителем"
                        );

                        person.setRelocationStatus("15");

                        personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
                    }
                });
        });
    }

    @Override
    @Async
    public void processUpdateRelocationStatus() {
        final List<PersonDocument> personDocumentList =
            personDocumentService.fetchByIncorrectRelocationStatus();

        personDocumentList.forEach(personDocument -> {
            final PersonType person = personDocument.getDocument().getPersonData();

            String newRelocationStatus = null;

            if (person.getKeysIssue() != null) {
                newRelocationStatus = "14";
            }

            if (person.getReleaseFlat() != null) {
                newRelocationStatus = "15";
            }

            if (newRelocationStatus != null && !newRelocationStatus.equals(person.getRelocationStatus())) {
                person.setRelocationStatus(newRelocationStatus);

                personDocumentService.updateDocument(personDocument.getId(), personDocument, true, true, "");
            }
        });
    }
}
