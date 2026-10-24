package com.example.workflow.api;

import java.util.List;
import java.util.Map;

/**
 * Immutable command used to start a new workflow instance.
 *
 * <p>{@code steps} is an ordered list of step names. The first step sits at
 * index {@code 0}, which is also the {@code currentStep} of a freshly started
 * instance. {@code variables} is an optional, untyped bag of workflow-scoped
 * values.</p>
 *
 * <p>Both collections are defensively copied on construction and exposed as
 * unmodifiable collections: later changes to the caller's {@link List} or
 * {@link Map} do not affect this command. A {@code null} {@code variables} map
 * is normalized to an empty map. A {@code null} or empty {@code steps} list,
 * a blank step name, and a {@code null} key or value in {@code variables} are
 * rejected.</p>
 *
 * @param workflowType non-blank workflow type
 * @param steps ordered, non-empty list of non-blank step names
 * @param variables workflow variables; {@code null} is normalized to an empty map
 * @throws WorkflowValidationException if any invariant above is violated
 */
public record StartWorkflowCommand(
        String workflowType,
        List<String> steps,
        Map<String, Object> variables
) {

    public StartWorkflowCommand {
        if (workflowType == null || workflowType.isBlank()) {
            throw new WorkflowValidationException("workflowType must not be blank");
        }
        steps = copySteps(steps);
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