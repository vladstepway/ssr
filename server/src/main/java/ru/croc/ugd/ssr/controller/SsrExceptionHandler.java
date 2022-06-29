package ru.croc.ugd.ssr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.croc.ugd.ssr.dto.ErrorDto;
import ru.croc.ugd.ssr.exception.PersonNotFoundException;
import ru.croc.ugd.ssr.exception.PersonNotValidForCommissionInspectionException;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.service.document.BookingNotExists;
import ru.reinform.cdp.exception.RIException;
import ru.reinform.cdp.exception.RINotFoundException;
import ru.reinform.cdp.exception.RIValidationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.validation.ConstraintViolationException;

/**
 * SSR exception handler.
 */
@Slf4j
@ControllerAdvice
public class SsrExceptionHandler {

    private final boolean showStacktrace;

    /**
     * Creates SsrExceptionHandler.
     * @param showStacktrace showStacktrace
     */
    public SsrExceptionHandler(
        @Value("${exception-handler.show-stacktrace:false}") final boolean showStacktrace
    ) {
        this.showStacktrace = showStacktrace;
    }

    /**
     * Handle AccessDeniedException.
     * @param exception exception
     * @return response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(final AccessDeniedException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorDto.builder()
                .possible(false)
                .reason(exception.getMessage())
                .stackTrace(showStacktrace ? retrieveStackTrace(exception) : null)
                .build()
            );
    }

    /**
     * Handle ConstraintViolationException.
     * @param exception exception
     * @return response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handlePersonCheckExceptions(final ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .legacyReason(exception.getMessage())
                .reason(exception.getMessage())
                .build()
            );
    }

    /**
     * Handle MissingServletRequestParameterException.
     * @param exception exception
     * @return response
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handlePersonCheckExceptions(
        final MissingServletRequestParameterException exception
    ) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .legacyReason(exception.getMessage())
                .reason(exception.getMessage())
                .build()
            );
    }

    /**
     * Handle PersonNotFoundException and PersonNotValidForShippingException.
     * @param exception exception
     * @return response with ReasonDto
     */
    @ExceptionHandler({PersonNotFoundException.class,
        PersonNotValidForShippingException.class,
        PersonNotValidForCommissionInspectionException.class})
    public ResponseEntity<ErrorDto> handlePersonCheckExceptions(final SsrException exception) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .code(exception.getErrorCode())
                .legacyReason(exception.getMessage())
                .reason(exception.getMessage())
                .build()
            );
    }

    /**
     * Handles RIValidationException.
     * @param exception exception
     * @return response with ErrorDto
     */
    @ExceptionHandler(RIValidationException.class)
    public ResponseEntity<ErrorDto> handleRiValidationException(final RIValidationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .message(exception.getMessage())
                .legacyReason(exception.getUserMessage())
                .reason(exception.getUserMessage())
                .stackTrace(showStacktrace ? retrieveStackTrace(exception) : null)
                .build()
            );
    }

    /**
     * Handles RINotFoundException.
     * @param exception exception
     * @return response with ErrorDto
     */
    @ExceptionHandler(RINotFoundException.class)
    public ResponseEntity<ErrorDto> handleRiNotFoundException(final RINotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .message(exception.getMessage())
                .legacyReason(exception.getUserMessage())
                .reason(exception.getUserMessage())
                .stackTrace(showStacktrace ? retrieveStackTrace(exception) : null)
                .build()
            );
    }

    /**
     * Handles SsrException.
     * @param exception exception
     * @return response with ErrorDto
     */
    @ExceptionHandler(SsrException.class)
    public ResponseEntity<ErrorDto> handleSsrException(final SsrException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .code(exception.getErrorCode())
                .legacyReason(exception.getMessage())
                .reason(exception.getMessage())
                .stackTrace(showStacktrace ? retrieveStackTrace(exception) : null)
                .build()
            );
    }

    /**
     * Handles BookingNotExists.
     *
     * @param exception BookingNotExists
     * @return response with ErrorDto
     */
    @ExceptionHandler(BookingNotExists.class)
    public ResponseEntity<ErrorDto> handleBookingNotExists(final BookingNotExists exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .legacyReason(exception.getMessage())
                .reason(exception.getMessage())
                .stackTrace(showStacktrace ? retrieveStackTrace(exception) : null)
                .build()
            );
    }

    /**
     * Handles RIException.
     *
     * @param exception RIException
     * @return response with ErrorDto
     */
    @ExceptionHandler(RIException.class)
    public ResponseEntity<ErrorDto> handleRiException(final RIException exception) {
        log.error(exception.getMessage(), exception);
        if (exception.getCause() != null) {
            if (exception.getCause() instanceof BookingNotExists) {
                return handleBookingNotExists((BookingNotExists) exception.getCause());
            }
        }
        return handleThrowable(exception);
    }

    /**
     * Handles Throwable.
     * @param throwable throwable
     * @return response with ErrorDto
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handleThrowable(final Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorDto.builder()
                .legacyPossible(false)
                .possible(false)
                .message(throwable.getMessage())
                .legacyReason("Проблема на сервере. Попробуйте еще раз или обратитесь в техническую поддержку.")
                .reason("Проблема на сервере. Попробуйте еще раз или обратитесь в техническую поддержку.")
                .stackTrace(showStacktrace ? retrieveStackTrace(throwable) : null)
                .build()
            );
    }

    private static String retrieveStackTrace(final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }
}
