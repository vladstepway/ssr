package ru.croc.ugd.ssr.service.validator.impl.notary;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.GuardianshipRequestDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasPositiveGuardianshipRequest;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsContractSigned;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsNotaryApplicationCreated;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsOwnerOrTenant;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class InternalNotaryPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    private static final String NOT_OWNER_MESSAGE = "validate.notary.person.notOwnerOrTenant";
    private static final String CONTRACT_SIGNED_MESSAGE = "validate.notary.person.contractSigned";
    private static final String APPLICATION_CREATED_MESSAGE = "validate.notary.alreadyCreated";
    private static final String NO_POSITIVE_GUARDIANSHIP_REQUEST_MESSAGE = "validate.notary.noAdministrativeDocs";

    private final MessageSource messageSource;
    private final PersonDocumentService personDocumentService;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final GuardianshipRequestDocumentService guardianshipRequestDocumentService;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsNotaryApplicationCreated<>(
            APPLICATION_CREATED_MESSAGE,
            messageSource,
            notaryApplicationDocumentService,
            personDocumentService
        ));
        this.registerValidatorInChain(
            new HasPositiveGuardianshipRequest<>(
                NO_POSITIVE_GUARDIANSHIP_REQUEST_MESSAGE,
                guardianshipRequestDocumentService)
        );
        this.registerValidatorInChain(new IsContractSigned<>(CONTRACT_SIGNED_MESSAGE, personDocumentService));
        this.registerValidatorInChain(new IsOwnerOrTenant<>(T -> NOT_OWNER_MESSAGE));
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }

}
