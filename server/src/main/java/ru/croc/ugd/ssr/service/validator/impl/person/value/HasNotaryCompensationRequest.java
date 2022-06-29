package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.service.document.NotaryCompensationDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.List;

@AllArgsConstructor
public class HasNotaryCompensationRequest<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;
    private final NotaryCompensationDocumentService notaryCompensationDocumentService;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        final String affairId = validatablePersonData.getPerson().getAffairId();
        return hasNotaryCompensationRequest(affairId)
            ? ValidatorUnitResult.fail(nonValidMessage)
            : ValidatorUnitResult.ok();
    }

    private boolean hasNotaryCompensationRequest(final String affairId) {
        final List<NotaryCompensationDocument> notaryCompensationDocuments =
            notaryCompensationDocumentService.findOpenApplicationsByAffairId(affairId);
        return !CollectionUtils.isEmpty(notaryCompensationDocuments);
    }
}
