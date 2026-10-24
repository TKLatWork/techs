package com.example.workflow.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkflowExceptionTest {

    @Test
    void subclassesShareWorkflowExceptionHierarchy() {
        assertThat(WorkflowException.class).isAssignableTo(RuntimeException.class);
        assertThat(WorkflowValidationException.class).isAssignableTo(WorkflowException.class);
        assertThat(WorkflowNotFoundException.class).isAssignableTo(WorkflowException.class);
        assertThat(WorkflowStateException.class).isAssignableTo(WorkflowException.class);
    }

    @Test
    void baseExceptionCarriesMessageAndCause() {
        Throwable cause = new IllegalStateException("boom");

        WorkflowException exception = new WorkflowException("failed", cause);

        assertThat(exception).hasMessage("failed");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void subclassesSupportMessageConstructor() {
        assertThat(new WorkflowValidationException("invalid")).hasMessage("invalid");
        assertThat(new WorkflowNotFoundException("missing")).hasMessage("missing");
        assertThat(new WorkflowStateException("conflict")).hasMessage("conflict");
    }

    @Test
    void subclassesSupportMessageAndCauseConstructor() {
        Throwable cause = new IllegalStateException("boom");

        assertThat(new WorkflowValidationException("invalid", cause)).hasMessage("invalid");
        assertThat(new WorkflowValidationException("invalid", cause).getCause()).isSameAs(cause);

        assertThat(new WorkflowNotFoundException("missing", cause)).hasMessage("missing");
        assertThat(new WorkflowNotFoundException("missing", cause).getCause()).isSameAs(cause);

        assertThat(new WorkflowStateException("conflict", cause)).hasMessage("conflict");
        assertThat(new WorkflowStateException("conflict", cause).getCause()).isSameAs(cause);
    }
}