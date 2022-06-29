package ru.croc.ugd.ssr.service.validator.impl.notary;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.GuardianshipRequestDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
public class ExternalNotaryPersonValidator extends InternalNotaryPersonValidator<ValidatablePersonData> {

    private static final String NOT_ADULT_MESSAGE = "validate.notary.person.notAdult18";

    public ExternalNotaryPersonValidator(
        final MessageSource messageSource,
        final PersonDocumentService personDocumentService,
        final NotaryApplicationDocumentService notaryApplicationDocumentService,
        final GuardianshipRequestDocumentService guardianshipRequestDocumentService
    ) {
        super(
            messageSource,
            personDocumentService,
            notaryApplicationDocumentService,
            guardianshipRequestDocumentService
        );
    }

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        super.init();
    }

}
