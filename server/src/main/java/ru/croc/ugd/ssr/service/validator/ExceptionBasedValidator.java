package ru.croc.ugd.ssr.service.validator;

import java.util.function.Supplier;

/**
 * General validator that throws exceptions.
 * @param <VALIDATABLE_OBJECT> Validatable object
 */
public interface ExceptionBasedValidator<VALIDATABLE_OBJECT> {

    Supplier<RuntimeException> getNoObjectExceptionSupplier();

    void validate(final VALIDATABLE_OBJECT validatableObject);

}
