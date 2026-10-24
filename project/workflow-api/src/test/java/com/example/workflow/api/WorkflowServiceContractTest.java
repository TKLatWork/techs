package com.example.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkflowServiceContractTest {

    @Test
    void serviceIsAnInterfaceWithTheFourFrozenOperations() throws NoSuchMethodException {
        assertThat(WorkflowService.class.isInterface()).isTrue();
        assertThat(WorkflowService.class.getDeclaredMethods()).hasSize(4);

        assertThat(WorkflowService.class.getMethod("start", StartWorkflowCommand.class).getReturnType())
                .isEqualTo(WorkflowInstance.class);
        assertThat(WorkflowService.class.getMethod("advance", String.class).getReturnType())
                .isEqualTo(WorkflowInstance.class);
        assertThat(WorkflowService.class.getMethod("get", String.class).getReturnType())
                .isEqualTo(WorkflowInstance.class);
        assertThat(WorkflowService.class.getMethod("cancel", String.class).getReturnType())
                .isEqualTo(WorkflowInstance.class);
    }

    @Test
    void commandExposesFrozenRecordComponentsInOrder() {
        assertThat(StartWorkflowCommand.class.isRecord()).isTrue();
        assertThat(StartWorkflowCommand.class.getRecordComponents())
                .extracting(RecordComponent::getName)
                .containsExactly("workflowType", "steps", "variables");
        assertThat(StartWorkflowCommand.class.getRecordComponents())
                .extracting(RecordComponent::getType)
                .containsExactly(String.class, List.class, Map.class);
    }

    @Test
    void instanceExposesFrozenRecordComponentsInOrder() {
        assertThat(WorkflowInstance.class.isRecord()).isTrue();
        assertThat(WorkflowInstance.class.getRecordComponents())
                .extracting(RecordComponent::getName)
                .containsExactly("id", "workflowType", "steps", "currentStep",
                        "status", "variables", "createdAt", "updatedAt");
        assertThat(WorkflowInstance.class.getRecordComponents())
                .extracting(RecordComponent::getType)
                .containsExactly(String.class, String.class, List.class, int.class,
                        WorkflowStatus.class, Map.class, Instant.class, Instant.class);
    }
}