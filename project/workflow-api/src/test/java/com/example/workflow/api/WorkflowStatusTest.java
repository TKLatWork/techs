package com.example.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkflowStatusTest {

    @Test
    void exposesExactlyTheThreeLifecycleValues() {
        assertThat(WorkflowStatus.values()).containsExactly(
                WorkflowStatus.RUNNING,
                WorkflowStatus.COMPLETED,
                WorkflowStatus.CANCELLED);
    }

    @Test
    void resolvesValuesByName() {
        assertThat(WorkflowStatus.valueOf("RUNNING")).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(WorkflowStatus.valueOf("COMPLETED")).isEqualTo(WorkflowStatus.COMPLETED);
        assertThat(WorkflowStatus.valueOf("CANCELLED")).isEqualTo(WorkflowStatus.CANCELLED);
    }
}