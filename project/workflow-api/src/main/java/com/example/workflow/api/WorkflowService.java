package com.example.workflow.api;

/**
 * Application port for managing workflow instances.
 *
 * <p>Implementations are responsible for generating instance ids (UUID
 * strings) and for enforcing the lifecycle documented on
 * {@link WorkflowStatus}. All returned {@link WorkflowInstance} values are
 * immutable snapshots.</p>
 */
public interface WorkflowService {

    /**
     * Starts a new workflow instance.
     *
     * <p>The returned instance has {@link WorkflowStatus#RUNNING} status and
     * {@code currentStep == 0}.</p>
     *
     * @param command start command, must not be {@code null}
     * @return the created instance
     * @throws WorkflowValidationException if the command is invalid
     */
    WorkflowInstance start(StartWorkflowCommand command);

    /**
     * Advances a running workflow instance by one step.
     *
     * <p>If another step follows, {@code currentStep} is incremented. If the
     * current step is the last one, the instance becomes
     * {@link WorkflowStatus#COMPLETED} and {@code currentStep} stays on that
     * last step.</p>
     *
     * @param instanceId workflow instance id
     * @return the updated instance
     * @throws WorkflowNotFoundException if the instance does not exist
     * @throws WorkflowStateException if the instance is not {@code RUNNING}
     */
    WorkflowInstance advance(String instanceId);

    /**
     * Returns the current snapshot of a workflow instance.
     *
     * @param instanceId workflow instance id
     * @return the instance
     * @throws WorkflowNotFoundException if the instance does not exist
     */
    WorkflowInstance get(String instanceId);

    /**
     * Cancels a running workflow instance.
     *
     * <p>Cancelling an already {@link WorkflowStatus#CANCELLED} instance is
     * idempotent and returns the current snapshot. Cancelling a
     * {@link WorkflowStatus#COMPLETED} instance is rejected.</p>
     *
     * @param instanceId workflow instance id
     * @return the cancelled instance
     * @throws WorkflowNotFoundException if the instance does not exist
     * @throws WorkflowStateException if the instance is {@code COMPLETED}
     */
    WorkflowInstance cancel(String instanceId);
}