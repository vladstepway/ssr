package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationApplicant;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@AllArgsConstructor
public class IsNotaryApplicationCreated<T extends ValidatablePersonData> implements ValueBasedNotEmptyValidatorUnit<T> {
    private final String message;
    private final MessageSource messageSource;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final PersonDocumentService personDocumentService;

    @Override
    public ValidatorUnitResult validate(ValidatablePersonData validatablePersonData) {
        final List<PersonDocument> flatOwners = personDocumentService.getOtherFlatOwners(
            validatablePersonData.getPersonId(),
            validatablePersonData.getPerson(),
            true
        );

        final Optional<String> applicantFullName = flatOwners
            .stream()
            .map(PersonDocument::getId)
            .map(notaryApplicationDocumentService::findNonCancelledApplicationsByPersonDocumentId)
            .flatMap(List::stream)
            .map(NotaryApplicationDocument::getDocument)
            .map(NotaryApplication::getNotaryApplicationData)
            .map(NotaryApplicationType::getApplicant)
            .map(IsNotaryApplicationCreated::toApplicantFullName)
            .findFirst();

        return applicantFullName
            .map(fullName -> ValidatorUnitResult.fail(getMessage(fullName)))
            .orElseGet(ValidatorUnitResult::ok);
    }

    private static String toApplicantFullName(final NotaryApplicationApplicant applicant) {
        return String.join(
            " ",
            getLastNameLetter(applicant.getLastName()),
            applicant.getFirstName(),
            applicant.getMiddleName()
        );
    }

    private static String getLastNameLetter(final String lastName) {
        return ofNullable(lastName)
            .map(name -> String.valueOf(name.charAt(0)))
            .map(name -> name.concat("."))
            .orElse(null);
    }

    private String getMessage(String fio) {
        return String.format(messageSource.getMessage(message, null, Locale.getDefault()), fio);
    }
}
