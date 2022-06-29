package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

@AllArgsConstructor
public class IsApartmentInspectionStarted<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;
    private final ApartmentInspectionService apartmentInspectionService;

    @Override
    public ValidatorUnitResult validate(T validatablePersonData) {
        final ApartmentInspectionDocument apartmentInspectionDocument = apartmentInspectionService
            .findDocumentWithStartedProcessByPersonId(validatablePersonData.getPersonId());

        if (apartmentInspectionDocument != null) {
            return ValidatorUnitResult.fail(nonValidMessage);
        }
        return ValidatorUnitResult.ok();
    }
}
