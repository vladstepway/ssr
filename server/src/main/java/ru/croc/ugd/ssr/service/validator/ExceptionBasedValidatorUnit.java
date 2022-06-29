package ru.croc.ugd.ssr.service.validator;

public interface ExceptionBasedValidatorUnit<VALIDATABLE_OBJECT> {

    void validate(final VALIDATABLE_OBJECT object);

    RuntimeException getValidationException();
}
