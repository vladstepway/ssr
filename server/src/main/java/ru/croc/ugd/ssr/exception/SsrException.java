package ru.croc.ugd.ssr.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Parent for any exception that occurs at SSR system.
 */
@NoArgsConstructor
@Getter
public class SsrException extends RuntimeException {
    private String errorCode;

    /**
     * Creates SsrException with message.
     * @param message message
     */
    public SsrException(final String message) {
        super(message);
    }

    /**
     * Creates SsrException with message and code.
     * @param message message
     * @param errorCode errorCode
     */
    public SsrException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates SsrException with message and cause.
     * @param message message
     * @param cause cause
     */
    public SsrException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
