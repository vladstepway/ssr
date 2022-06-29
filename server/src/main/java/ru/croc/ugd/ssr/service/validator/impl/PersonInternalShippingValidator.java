package ru.croc.ugd.ssr.service.validator.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedExceptionValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.IsContractSigned;
import ru.croc.ugd.ssr.service.validator.impl.person.ShippingAlreadyRequestedAndServiceProvided;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.function.Supplier;

/**
 * Валидатор жильца.
 */
@Service
@Slf4j
public class PersonInternalShippingValidator<T extends ValidatablePersonData> extends ChainedExceptionValidator<T> {

    @Autowired
    public PersonInternalShippingValidator(
        final MessageSource messageSource,
        final PersonDocumentService personDocumentService,
        final ShippingApplicationDocumentService shippingApplicationDocumentService
    ) {
        super(messageSource);
        initValidatorChain(messageSource, personDocumentService, shippingApplicationDocumentService);
    }

    public void initValidatorChain(MessageSource messageSource,
                                   PersonDocumentService personDocumentService,
                                   ShippingApplicationDocumentService shippingApplicationDocumentService) {
        this.registerValidatorInChain(new IsContractSigned<>(messageSource, personDocumentService));
        this.registerValidatorInChain(new ShippingAlreadyRequestedAndServiceProvided<T>(
            messageSource, personDocumentService, shippingApplicationDocumentService, false
        ));
    }

    @Override
    public Supplier<RuntimeException> getNoObjectExceptionSupplier() {
        return () -> new PersonNotValidForShippingException(
            getMessage(PersonShippingValidator.NOT_RELOCATING),
            PersonShippingValidator.NOT_RELOCATING_CODE
        );
    }
}
