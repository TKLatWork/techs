package com.example.workflow.api;

/**
 * Thrown when an operation is not allowed in the instance's current state,
 * such as advancing a completed or cancelled instance, or cancelling a
 * completed instance.
 */
public class WorkflowStateException extends WorkflowException {

    /**
     * Creates an exception with the given message.
     *
     * @param message detail message
     */
    public WorkflowStateException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public WorkflowStateException(String message, Throwable cause) {
        super(message, cause);
    }
}