package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static ru.croc.ugd.ssr.service.guardianship.DefaultRestGuardianshipService.POSITIVE_GUARDIANSHIP_DECISION_TYPE;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequest;
import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;
import ru.croc.ugd.ssr.service.document.GuardianshipRequestDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.Objects;

@AllArgsConstructor
public class HasPositiveGuardianshipRequest<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;
    private final GuardianshipRequestDocumentService guardianshipRequestDocumentService;

    @Override
    public ValidatorUnitResult validate(T object) {
        final String affairId = object.getPerson().getAffairId();
        return hasAnyPositiveGuardianshipRequest(affairId)
            ? ValidatorUnitResult.ok()
            : ValidatorUnitResult.fail(nonValidMessage);
    }

    private boolean hasAnyPositiveGuardianshipRequest(final String affairId) {
        return guardianshipRequestDocumentService.fetchByAffairIdAndSkipInactive(affairId, false)
            .stream()
            .map(GuardianshipRequestDocument::getDocument)
            .map(GuardianshipRequest::getGuardianshipRequestData)
            .anyMatch(guardianshipRequestData ->
                Objects.equals(POSITIVE_GUARDIANSHIP_DECISION_TYPE, guardianshipRequestData.getDecisionType())
            );
    }
}
