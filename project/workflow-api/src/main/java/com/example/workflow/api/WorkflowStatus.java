package com.example.workflow.api;

/**
 * Lifecycle status of a workflow instance.
 *
 * <p>Allowed transitions: {@link #RUNNING} to {@link #RUNNING} on a
 * non-terminal advance, {@link #RUNNING} to {@link #COMPLETED} when the last
 * step is advanced, and {@link #RUNNING} to {@link #CANCELLED} on cancel.
 * {@link #COMPLETED} and {@link #CANCELLED} are terminal.</p>
 */
public enum WorkflowStatus {

    /** The instance has started and can still be advanced or cancelled. */
    RUNNING,

    /** The last step was advanced; terminal status. */
    COMPLETED,

    /** The instance was cancelled while running; terminal status. */
    CANCELLED
}