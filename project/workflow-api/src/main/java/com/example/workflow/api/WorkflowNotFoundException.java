package com.example.workflow.api;

/**
 * Thrown when a workflow instance cannot be found by its id.
 */
public class WorkflowNotFoundException extends WorkflowException {

    /**
     * Creates an exception with the given message.
     *
     * @param message detail message
     */
    public WorkflowNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public WorkflowNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}