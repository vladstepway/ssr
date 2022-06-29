package ru.croc.ugd.ssr.service.validator;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

@AllArgsConstructor
public abstract class AbstractExceptionBasedValidatorUnit<T> implements ExceptionBasedValidatorUnit<T> {

    private final MessageSource messageSource;

    protected String getMessage(final String messageKey) {
        return messageSource.getMessage(messageKey, null, Locale.getDefault());
    }
}
