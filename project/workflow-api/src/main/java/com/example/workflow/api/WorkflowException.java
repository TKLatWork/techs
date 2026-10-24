package com.example.workflow.api;

/**
 * Base unchecked exception for workflow errors.
 *
 * <p>All workflow-specific failures extend this type so callers can handle
 * them uniformly. The concrete subclasses map to distinct transport-level
 * errors in the web layer.</p>
 *
 * @see WorkflowValidationException
 * @see WorkflowNotFoundException
 * @see WorkflowStateException
 */
public class WorkflowException extends RuntimeException {

    /**
     * Creates an exception with the given message.
     *
     * @param message detail message
     */
    public WorkflowException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}