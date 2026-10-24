package com.example.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkflowInstanceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-22T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-10-22T00:01:00Z");

    @Test
    void acceptsFirstAndLastStepIndex() {
        List<String> steps = List.of("first", "second", "third");

        assertThat(instance(steps, 0).currentStep()).isZero();
        assertThat(instance(steps, 2).currentStep()).isEqualTo(2);
    }

    @Test
    void rejectsNegativeCurrentStep() {
        assertThatThrownBy(() -> instance(List.of("first", "second"), -1))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("currentStep");
    }

    @Test
    void rejectsCurrentStepAtOrBeyondSize() {
        assertThatThrownBy(() -> instance(List.of("first", "second"), 2))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("currentStep");
    }

    @Test
    void rejectsNullSteps() {
        assertThatThrownBy(() -> instance(null, 0))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("steps");
    }

    @Test
    void rejectsEmptySteps() {
        assertThatThrownBy(() -> instance(List.of(), 0))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("steps");
    }

    @Test
    void rejectsBlankStepElement() {
        assertThatThrownBy(() -> instance(Arrays.asList("first", " "), 0))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("step");
    }

    @Test
    void rejectsNullRequiredComponents() {
        List<String> steps = List.of("step");

        assertThatThrownBy(() -> new WorkflowInstance(null, "type", steps, 0,
                WorkflowStatus.RUNNING, Map.of(), CREATED_AT, UPDATED_AT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id");
        assertThatThrownBy(() -> new WorkflowInstance("id", null, steps, 0,
                WorkflowStatus.RUNNING, Map.of(), CREATED_AT, UPDATED_AT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("workflowType");
        assertThatThrownBy(() -> new WorkflowInstance("id", "type", steps, 0,
                null, Map.of(), CREATED_AT, UPDATED_AT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("status");
        assertThatThrownBy(() -> new WorkflowInstance("id", "type", steps, 0,
                WorkflowStatus.RUNNING, Map.of(), null, UPDATED_AT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("createdAt");
        assertThatThrownBy(() -> new WorkflowInstance("id", "type", steps, 0,
                WorkflowStatus.RUNNING, Map.of(), CREATED_AT, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("updatedAt");
    }

    @Test
    void normalizesNullVariablesToEmptyMap() {
        WorkflowInstance instance = new WorkflowInstance("id", "type", List.of("step"), 0,
                WorkflowStatus.RUNNING, null, CREATED_AT, UPDATED_AT);

        assertThat(instance.variables()).isEmpty();
    }

    @Test
    void defensivelyCopiesAndFreezesSteps() {
        List<String> steps = new ArrayList<>(List.of("first", "second"));
        WorkflowInstance instance = instance(steps, 0);

        steps.add("third");

        assertThat(instance.steps()).containsExactly("first", "second");
        assertThatThrownBy(() -> instance.steps().add("third"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void defensivelyCopiesAndFreezesVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key", "value");
        WorkflowInstance instance = new WorkflowInstance("id", "type", List.of("step"), 0,
                WorkflowStatus.RUNNING, variables, CREATED_AT, UPDATED_AT);

        variables.put("other", "value");

        assertThat(instance.variables()).containsOnlyKeys("key");
        assertThatThrownBy(() -> instance.variables().put("other", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static WorkflowInstance instance(List<String> steps, int currentStep) {
        return new WorkflowInstance("id", "type", steps, currentStep,
                WorkflowStatus.RUNNING, Map.of(), CREATED_AT, UPDATED_AT);
    }
}