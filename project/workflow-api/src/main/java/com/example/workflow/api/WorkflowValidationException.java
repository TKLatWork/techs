package com.example.workflow.api;

/**
 * Thrown when workflow input fails validation, for example a blank workflow
 * type, an empty step list, or an out-of-range step index.
 */
public class WorkflowValidationException extends WorkflowException {

    /**
     * Creates an exception with the given message.
     *
     * @param message detail message
     */
    public WorkflowValidationException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public WorkflowValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}