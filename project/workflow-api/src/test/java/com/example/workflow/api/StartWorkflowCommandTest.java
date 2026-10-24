package com.example.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StartWorkflowCommandTest {

    @Test
    void rejectsNullSteps() {
        assertThatThrownBy(() -> new StartWorkflowCommand("type", null, Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("steps");
    }

    @Test
    void rejectsEmptySteps() {
        assertThatThrownBy(() -> new StartWorkflowCommand("type", List.of(), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("steps");
    }

    @Test
    void rejectsNullStepElement() {
        assertThatThrownBy(() -> new StartWorkflowCommand("type", Arrays.asList("first", null), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("step");
    }

    @Test
    void rejectsBlankStepElement() {
        assertThatThrownBy(() -> new StartWorkflowCommand("type", List.of("first", "  "), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("step");
    }

    @Test
    void rejectsNullWorkflowType() {
        assertThatThrownBy(() -> new StartWorkflowCommand(null, List.of("step"), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("workflowType");
    }

    @Test
    void rejectsBlankWorkflowType() {
        assertThatThrownBy(() -> new StartWorkflowCommand("  ", List.of("step"), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("workflowType");
    }

    @Test
    void normalizesNullVariablesToEmptyMap() {
        StartWorkflowCommand command = new StartWorkflowCommand("type", List.of("step"), null);

        assertThat(command.variables()).isEmpty();
    }

    @Test
    void rejectsNullVariableKey() {
        Map<String, Object> variables = new HashMap<>();
        variables.put(null, "value");

        assertThatThrownBy(() -> new StartWorkflowCommand("type", List.of("step"), variables))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("variables");
    }

    @Test
    void rejectsNullVariableValue() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key", null);

        assertThatThrownBy(() -> new StartWorkflowCommand("type", List.of("step"), variables))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("variables");
    }

    @Test
    void defensivelyCopiesAndFreezesSteps() {
        List<String> steps = new ArrayList<>(List.of("first", "second"));

        StartWorkflowCommand command = new StartWorkflowCommand("type", steps, Map.of());
        steps.add("third");

        assertThat(command.steps()).containsExactly("first", "second");
        assertThatThrownBy(() -> command.steps().add("third"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void defensivelyCopiesAndFreezesVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key", "value");

        StartWorkflowCommand command = new StartWorkflowCommand("type", List.of("step"), variables);
        variables.put("other", "value");

        assertThat(command.variables()).containsOnlyKeys("key");
        assertThatThrownBy(() -> command.variables().put("other", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}