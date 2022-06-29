package ru.croc.ugd.ssr.service.validator.impl.notarycompensation;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryCompensationDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasNotaryApplicationWithSignedContract;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasNotaryCompensationRequest;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasPerformedNotaryCompensation;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasRegisteredContract;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class NotaryCompensationPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    private static final String NOT_ADULT_MESSAGE = "validate.notary.compensation.person.notAdult18";
    private static final String NO_NOTARY_APPLICATION_WITH_SIGNED_CONTRACT =
        "validate.notary.compensation.person.noNotaryApplicationWithSignedContract";
    private static final String NO_REGISTERED_CONTRACT = "validate.notary.compensation.person.noRegisteredContract";
    private static final String NOTARY_COMPENSATION_REQUESTED = "validate.notary.compensation.alreadyRequested";
    private static final String NOTARY_COMPENSATION_PERFORMED = "validate.notary.compensation.alreadyPerformed";

    private final MessageSource messageSource;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final NotaryCompensationDocumentService notaryCompensationDocumentService;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        this.registerValidatorInChain(new HasNotaryApplicationWithSignedContract<>(
            NO_NOTARY_APPLICATION_WITH_SIGNED_CONTRACT, notaryApplicationDocumentService
        ));
        this.registerValidatorInChain(new HasRegisteredContract<>(NO_REGISTERED_CONTRACT));
        this.registerValidatorInChain(new HasNotaryCompensationRequest<>(
            NOTARY_COMPENSATION_REQUESTED, notaryCompensationDocumentService
        ));
        this.registerValidatorInChain(new HasPerformedNotaryCompensation<>(
            NOTARY_COMPENSATION_PERFORMED, notaryCompensationDocumentService
        ));
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }
}
