package ru.croc.ugd.ssr.service.validator.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedExceptionValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.IsAdultPerson16;
import ru.croc.ugd.ssr.service.validator.impl.person.IsContractSigned;
import ru.croc.ugd.ssr.service.validator.impl.person.IsEquivalentCompensation;
import ru.croc.ugd.ssr.service.validator.impl.person.IsOwnerOrTenantOrLiver;
import ru.croc.ugd.ssr.service.validator.impl.person.RequestLessThenBeforeThreeDaysOfShipping;
import ru.croc.ugd.ssr.service.validator.impl.person.ShippingAlreadyRequestedAndServiceProvided;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.function.Supplier;

/**
 * Валидатор жильца.
 */
@Service
@Slf4j
public class PersonShippingValidator<T extends ValidatablePersonData> extends ChainedExceptionValidator<T> {

    public static final String NOT_RELOCATING = "validate.person.notIncludedInRelocation";
    public static final String NOT_RELOCATING_CODE = "user_not_in_renovation";

    @Autowired
    public PersonShippingValidator(MessageSource messageSource,
                                   PersonDocumentService personDocumentService,
                                   TradeAdditionDocumentService tradeAdditionDocumentService,
                                   ShippingApplicationDocumentService shippingApplicationDocumentService) {
        super(messageSource);
        initValidatorChain(messageSource,
            personDocumentService,
            tradeAdditionDocumentService,
            shippingApplicationDocumentService);
    }

    public void initValidatorChain(
        MessageSource messageSource,
        PersonDocumentService personDocumentService,
        TradeAdditionDocumentService tradeAdditionDocumentService,
        ShippingApplicationDocumentService shippingApplicationDocumentService
    ) {
        // 1
        this.registerValidatorInChain(new IsAdultPerson16<>(messageSource));
        // 3
        this.registerValidatorInChain(new IsOwnerOrTenantOrLiver<>(messageSource));
        // 7
        this.registerValidatorInChain(new IsContractSigned<>(messageSource, personDocumentService));
        // 6
        this.registerValidatorInChain(new IsEquivalentCompensation<>(messageSource));
        // 8, 5
        this.registerValidatorInChain(new ShippingAlreadyRequestedAndServiceProvided<T>(
            messageSource, personDocumentService, shippingApplicationDocumentService, true
        ));
        // 4
        this.registerValidatorInChain(new RequestLessThenBeforeThreeDaysOfShipping<>(
            messageSource, personDocumentService, tradeAdditionDocumentService));
    }

    @Override
    public Supplier<RuntimeException> getNoObjectExceptionSupplier() {
        return () -> new PersonNotValidForShippingException(getMessage(NOT_RELOCATING), NOT_RELOCATING_CODE);
    }

}
