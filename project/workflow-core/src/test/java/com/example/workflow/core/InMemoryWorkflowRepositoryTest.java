package com.example.workflow.core;

import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryWorkflowRepositoryTest {

    private final InMemoryWorkflowRepository repository = new InMemoryWorkflowRepository();

    private static WorkflowInstance instance(String id, WorkflowStatus status) {
        Instant now = Instant.now();
        return new WorkflowInstance(
                id,
                "order-processing",
                List.of("validate", "pay"),
                0,
                status,
                Map.of("orderId", "order-1"),
                now,
                now);
    }

    @Test
    void saveStoresInstanceAndReturnsIt() {
        WorkflowInstance instance = instance("wf-1", WorkflowStatus.RUNNING);

        assertThat(repository.save(instance)).isSameAs(instance);
        assertThat(repository.findById("wf-1")).get().isSameAs(instance);
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(repository.findById("missing")).isEmpty();
    }

    @Test
    void findByIdReturnsEmptyForNullId() {
        assertThat(repository.findById(null)).isEmpty();
    }

    @Test
    void saveReplacesInstanceWithSameId() {
        WorkflowInstance original = instance("wf-1", WorkflowStatus.RUNNING);
        WorkflowInstance replacement = instance("wf-1", WorkflowStatus.CANCELLED);
        repository.save(original);

        repository.save(replacement);

        Optional<WorkflowInstance> stored = repository.findById("wf-1");
        assertThat(stored).get().isSameAs(replacement);
        assertThat(stored.orElseThrow().status()).isEqualTo(WorkflowStatus.CANCELLED);
    }

    @Test
    void saveRejectsNullInstance() {
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
    }
}