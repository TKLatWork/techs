package com.example.workflow.core;

import com.example.workflow.api.StartWorkflowCommand;
import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowNotFoundException;
import com.example.workflow.api.WorkflowStateException;
import com.example.workflow.api.WorkflowStatus;
import com.example.workflow.api.WorkflowValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultWorkflowServiceTest {

    private InMemoryWorkflowRepository repository;
    private DefaultWorkflowService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWorkflowRepository();
        service = new DefaultWorkflowService(repository);
    }

    private static StartWorkflowCommand command(String... steps) {
        return new StartWorkflowCommand(
                "order-processing",
                List.of(steps),
                Map.of("orderId", "order-1"));
    }

    @Test
    void startCreatesRunningInstanceAtFirstStep() {
        Instant before = Instant.now();

        WorkflowInstance instance = service.start(command("validate", "pay", "ship"));

        Instant after = Instant.now();
        assertThat(instance.id()).isNotBlank();
        assertThat(UUID.fromString(instance.id())).isNotNull();
        assertThat(instance.workflowType()).isEqualTo("order-processing");
        assertThat(instance.steps()).containsExactly("validate", "pay", "ship");
        assertThat(instance.currentStep()).isZero();
        assertThat(instance.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(instance.variables()).containsEntry("orderId", "order-1");
        assertThat(instance.createdAt()).isBetween(before, after);
        assertThat(instance.updatedAt()).isEqualTo(instance.createdAt());
        assertThat(repository.findById(instance.id())).contains(instance);
    }

    @Test
    void startAssignsUniqueIds() {
        WorkflowInstance first = service.start(command("only"));
        WorkflowInstance second = service.start(command("only"));

        assertThat(first.id()).isNotEqualTo(second.id());
    }

    @Test
    void startRejectsNullCommand() {
        assertThatThrownBy(() -> service.start(null))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("command");
    }

    @Test
    void startRejectsEmptySteps() {
        assertThatThrownBy(DefaultWorkflowServiceTest::command)
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("steps must not be empty");
    }

    @Test
    void startRejectsBlankStep() {
        assertThatThrownBy(() -> command("validate", "  "))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("step must not be blank");
    }

    @Test
    void startRejectsBlankWorkflowType() {
        assertThatThrownBy(() -> new StartWorkflowCommand(" ", List.of("only"), Map.of()))
                .isInstanceOf(WorkflowValidationException.class)
                .hasMessageContaining("workflowType");
    }

    @Test
    void advanceMovesToNextStepAndStaysRunning() {
        WorkflowInstance started = service.start(command("validate", "pay", "ship"));

        WorkflowInstance advanced = service.advance(started.id());

        assertThat(advanced.id()).isEqualTo(started.id());
        assertThat(advanced.currentStep()).isEqualTo(1);
        assertThat(advanced.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(advanced.createdAt()).isEqualTo(started.createdAt());
        assertThat(advanced.updatedAt()).isAfterOrEqualTo(started.updatedAt());
        assertThat(repository.findById(started.id())).contains(advanced);
    }

    @Test
    void advanceFromLastStepCompletes() {
        WorkflowInstance started = service.start(command("only"));

        WorkflowInstance completed = service.advance(started.id());

        assertThat(completed.status()).isEqualTo(WorkflowStatus.COMPLETED);
        assertThat(completed.currentStep()).isEqualTo(0);
        assertThat(completed.updatedAt()).isAfterOrEqualTo(started.updatedAt());
        assertThat(repository.findById(started.id())).contains(completed);
    }

    @Test
    void advanceThroughEveryStepCompletesAtLastStep() {
        WorkflowInstance started = service.start(command("validate", "pay", "ship"));

        WorkflowInstance afterFirst = service.advance(started.id());
        WorkflowInstance afterSecond = service.advance(started.id());
        WorkflowInstance afterThird = service.advance(started.id());

        assertThat(afterFirst.currentStep()).isEqualTo(1);
        assertThat(afterFirst.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(afterSecond.currentStep()).isEqualTo(2);
        assertThat(afterSecond.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(afterThird.currentStep()).isEqualTo(2);
        assertThat(afterThird.status()).isEqualTo(WorkflowStatus.COMPLETED);
    }

    @Test
    void advanceUnknownInstanceThrowsNotFound() {
        assertThatThrownBy(() -> service.advance("missing"))
                .isInstanceOf(WorkflowNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void advanceNullInstanceThrowsNotFound() {
        assertThatThrownBy(() -> service.advance(null))
                .isInstanceOf(WorkflowNotFoundException.class);
    }

    @Test
    void advanceCompletedInstanceThrowsStateException() {
        WorkflowInstance started = service.start(command("only"));
        service.advance(started.id());

        assertThatThrownBy(() -> service.advance(started.id()))
                .isInstanceOf(WorkflowStateException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void advanceCancelledInstanceThrowsStateException() {
        WorkflowInstance started = service.start(command("validate", "pay"));
        service.cancel(started.id());

        assertThatThrownBy(() -> service.advance(started.id()))
                .isInstanceOf(WorkflowStateException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    void getReturnsStoredInstance() {
        WorkflowInstance started = service.start(command("validate"));

        assertThat(service.get(started.id())).isEqualTo(started);
    }

    @Test
    void getUnknownInstanceThrowsNotFound() {
        assertThatThrownBy(() -> service.get("missing"))
                .isInstanceOf(WorkflowNotFoundException.class)
                .hasMessageContaining("workflow instance not found: missing");
    }

    @Test
    void getNullInstanceThrowsNotFound() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(WorkflowNotFoundException.class);
    }

    @Test
    void cancelRunningInstanceMarksCancelled() {
        WorkflowInstance started = service.start(command("validate", "pay"));

        WorkflowInstance cancelled = service.cancel(started.id());

        assertThat(cancelled.id()).isEqualTo(started.id());
        assertThat(cancelled.status()).isEqualTo(WorkflowStatus.CANCELLED);
        assertThat(cancelled.currentStep()).isEqualTo(started.currentStep());
        assertThat(cancelled.createdAt()).isEqualTo(started.createdAt());
        assertThat(cancelled.updatedAt()).isAfterOrEqualTo(started.updatedAt());
        assertThat(repository.findById(started.id())).contains(cancelled);
    }

    @Test
    void cancelAlreadyCancelledInstanceIsIdempotent() {
        WorkflowInstance started = service.start(command("validate", "pay"));
        WorkflowInstance firstCancel = service.cancel(started.id());

        WorkflowInstance secondCancel = service.cancel(started.id());

        assertThat(secondCancel).isEqualTo(firstCancel);
        assertThat(repository.findById(started.id())).contains(firstCancel);
    }

    @Test
    void cancelCompletedInstanceThrowsStateException() {
        WorkflowInstance started = service.start(command("only"));
        service.advance(started.id());

        assertThatThrownBy(() -> service.cancel(started.id()))
                .isInstanceOf(WorkflowStateException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void cancelUnknownInstanceThrowsNotFound() {
        assertThatThrownBy(() -> service.cancel("missing"))
                .isInstanceOf(WorkflowNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void startedSnapshotsAreDefensiveAndImmutable() {
        List<String> steps = new java.util.ArrayList<>(List.of("validate", "pay"));
        Map<String, Object> variables = new java.util.LinkedHashMap<>(Map.of("orderId", "order-1"));
        WorkflowInstance started = service.start(
                new StartWorkflowCommand("order-processing", steps, variables));

        steps.add("ship");
        variables.put("orderId", "mutated");

        assertThat(started.steps()).containsExactly("validate", "pay");
        assertThat(started.variables()).containsEntry("orderId", "order-1");
        assertThatThrownBy(() -> started.steps().add("ship"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> started.variables().put("other", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}