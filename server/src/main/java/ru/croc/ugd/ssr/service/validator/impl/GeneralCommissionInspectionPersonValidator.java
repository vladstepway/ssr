package ru.croc.ugd.ssr.service.validator.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.exception.PersonNotValidForCommissionInspectionException;
import ru.croc.ugd.ssr.service.validator.ChainedExceptionValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.IsAdultPerson18;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.function.Supplier;

/**
 * Общие проверки жильца для комиссионного осмотра.
 */
@Service
@Slf4j
public class GeneralCommissionInspectionPersonValidator<T extends ValidatablePersonData>
    extends ChainedExceptionValidator<T> {

    protected static final String NOT_RELOCATING = "validate.commissionInspection.person.notIncludedInRelocation";

    @Autowired
    public GeneralCommissionInspectionPersonValidator(MessageSource messageSource) {
        super(messageSource);
        initValidatorChain(messageSource);
    }

    public void initValidatorChain(MessageSource messageSource) {
        this.registerValidatorInChain(new IsAdultPerson18<>(messageSource));
    }

    @Override
    public Supplier<RuntimeException> getNoObjectExceptionSupplier() {
        return () -> new PersonNotValidForCommissionInspectionException(getMessage(NOT_RELOCATING));
    }

}
