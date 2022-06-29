package ru.croc.ugd.ssr.service.validator;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@AllArgsConstructor
public abstract class ChainedExceptionValidator<VALIDATABLE_OBJECT>
    implements ExceptionBasedValidator<VALIDATABLE_OBJECT> {

    private final List<ExceptionBasedValidatorUnit<VALIDATABLE_OBJECT>> abstractValidatorUnits = new ArrayList<>();

    protected final MessageSource messageSource;

    protected void registerValidatorInChain(
        final ExceptionBasedValidatorUnit<VALIDATABLE_OBJECT> abstractValidatorUnit
    ) {
        abstractValidatorUnits.add(abstractValidatorUnit);
    }

    public void validate(final VALIDATABLE_OBJECT validatableObject) {
        ofNullable(validatableObject)
            .orElseThrow(getNoObjectExceptionSupplier());

        abstractValidatorUnits
            .forEach(validator -> validator.validate(validatableObject));
    }

    protected String getMessage(final String messageKey) {
        return messageSource.getMessage(messageKey, null, Locale.getDefault());
    }
}

