package com.example.workflow.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of a workflow instance.
 *
 * <p>{@code currentStep} is a zero-based index into {@code steps} and points
 * to the step the instance is currently on. While {@code status} is
 * {@link WorkflowStatus#RUNNING} the instance may be advanced or cancelled;
 * {@link WorkflowStatus#COMPLETED} and {@link WorkflowStatus#CANCELLED} are
 * terminal statuses. When the last step is advanced the status becomes
 * {@code COMPLETED} and {@code currentStep} keeps pointing at that last step.</p>
 *
 * <p>The {@code steps} list and {@code variables} map are defensively copied
 * and exposed as unmodifiable collections, so mutating the source collections
 * after construction never changes this instance. A {@code null}
 * {@code variables} map is normalized to an empty map.</p>
 *
 * <p>For an instance created by {@code start}, {@code currentStep} is
 * {@code 0}. The value must stay within {@code [0, steps.size())}.</p>
 *
 * @param id unique workflow instance identifier
 * @param workflowType workflow type this instance was started from
 * @param steps ordered, non-empty list of non-blank step names
 * @param currentStep zero-based index of the current step
 * @param status lifecycle status
 * @param variables workflow variables; {@code null} is normalized to an empty map
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 * @throws WorkflowValidationException if {@code steps} is invalid or
 *         {@code currentStep} is outside {@code [0, steps.size())}
 * @throws NullPointerException if {@code id}, {@code workflowType},
 *         {@code status}, {@code createdAt}, or {@code updatedAt} is {@code null}
 */
public record WorkflowInstance(
        String id,
        String workflowType,
        List<String> steps,
        int currentStep,
        WorkflowStatus status,
        Map<String, Object> variables,
        Instant createdAt,
        Instant updatedAt
) {

    public WorkflowInstance {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(workflowType, "workflowType must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        steps = copySteps(steps);
        if (currentStep < 0 || currentStep >= steps.size()) {
            throw new WorkflowValidationException(
                    "currentStep out of range [0, " + steps.size() + "): " + currentStep);
        }
        variables = copyVariables(variables);
    }

    private static List<String> copySteps(List<String> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new WorkflowValidationException("steps must not be empty");
        }
        for (String step : steps) {
            if (step == null || step.isBlank()) {
                throw new WorkflowValidationException("step must not be blank");
            }
        }
        return List.copyOf(steps);
    }

    private static Map<String, Object> copyVariables(Map<String, Object> variables) {
        if (variables == null) {
            return Map.of();
        }
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new WorkflowValidationException("variables must not contain null keys or values");
            }
        }
        return Map.copyOf(variables);
    }
}