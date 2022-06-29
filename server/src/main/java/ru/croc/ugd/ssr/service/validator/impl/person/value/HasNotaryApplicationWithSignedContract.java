package ru.croc.ugd.ssr.service.validator.impl.person.value;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.util.List;

@AllArgsConstructor
public class HasNotaryApplicationWithSignedContract<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private final String nonValidMessage;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;

    @Override
    public ValidatorUnitResult validate(final T validatablePersonData) {
        final String affairId = validatablePersonData.getPerson().getAffairId();
        return hasNotaryApplicationWithSignedContract(affairId)
            ? ValidatorUnitResult.ok()
            : ValidatorUnitResult.fail(nonValidMessage);
    }

    private boolean hasNotaryApplicationWithSignedContract(final String affairId) {
        final List<NotaryApplicationDocument> notaryApplicationDocument =
            notaryApplicationDocumentService.findApplicationsWithSignedContractByAffairId(affairId);
        return !CollectionUtils.isEmpty(notaryApplicationDocument);
    }
}
