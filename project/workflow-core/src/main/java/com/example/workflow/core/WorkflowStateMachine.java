package com.example.workflow.core;

import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowStateException;
import com.example.workflow.api.WorkflowStatus;

import java.time.Instant;

/**
 * Centralises the legal workflow lifecycle transitions.
 *
 * <p>Only {@link WorkflowStatus#RUNNING} instances can be advanced or cancelled.
 * Cancelling an already cancelled instance is treated as idempotent by the caller.</p>
 */
final class WorkflowStateMachine {

    private WorkflowStateMachine() {
    }

    static WorkflowInstance advance(WorkflowInstance instance) {
        if (instance.status() != WorkflowStatus.RUNNING) {
            throw new WorkflowStateException(
                    "workflow instance " + instance.id()
                            + " cannot be advanced in state " + instance.status());
        }

        int lastStep = instance.steps().size() - 1;
        if (instance.currentStep() < lastStep) {
            return copyOf(instance, instance.currentStep() + 1, WorkflowStatus.RUNNING);
        }
        return copyOf(instance, lastStep, WorkflowStatus.COMPLETED);
    }

    static WorkflowInstance cancel(WorkflowInstance instance) {
        if (instance.status() == WorkflowStatus.CANCELLED) {
            return instance;
        }
        if (instance.status() != WorkflowStatus.RUNNING) {
            throw new WorkflowStateException(
                    "workflow instance " + instance.id()
                            + " cannot be cancelled in state " + instance.status());
        }
        return copyOf(instance, instance.currentStep(), WorkflowStatus.CANCELLED);
    }

    private static WorkflowInstance copyOf(WorkflowInstance instance, int currentStep, WorkflowStatus status) {
        return new WorkflowInstance(
                instance.id(),
                instance.workflowType(),
                instance.steps(),
                currentStep,
                status,
                instance.variables(),
                instance.createdAt(),
                Instant.now());
    }
}