package com.example.workflow.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.workflow.api.StartWorkflowCommand;
import com.example.workflow.api.WorkflowException;
import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowNotFoundException;
import com.example.workflow.api.WorkflowService;
import com.example.workflow.api.WorkflowStateException;
import com.example.workflow.api.WorkflowStatus;
import com.example.workflow.api.WorkflowValidationException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-22T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-10-22T00:05:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkflowService workflowService;

    @Test
    void startReturnsCreatedInstance() throws Exception {
        given(workflowService.start(any(StartWorkflowCommand.class)))
                .willReturn(instance("wf-1", 0, WorkflowStatus.RUNNING));

        mockMvc.perform(post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"workflowType":"order","steps":["validate","charge"],"variables":{"amount":42}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("wf-1"))
                .andExpect(jsonPath("$.workflowType").value("order"))
                .andExpect(jsonPath("$.steps[0]").value("validate"))
                .andExpect(jsonPath("$.steps[1]").value("charge"))
                .andExpect(jsonPath("$.currentStep").value(0))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.variables.amount").value(42))
                .andExpect(jsonPath("$.createdAt").value("2026-10-22T00:00:00Z"));

        ArgumentCaptor<StartWorkflowCommand> captor = ArgumentCaptor.forClass(StartWorkflowCommand.class);
        then(workflowService).should().start(captor.capture());
        assertThat(captor.getValue().workflowType()).isEqualTo("order");
        assertThat(captor.getValue().steps()).containsExactly("validate", "charge");
        assertThat(captor.getValue().variables()).containsEntry("amount", 42);
    }

    @Test
    void advanceReturnsUpdatedInstance() throws Exception {
        given(workflowService.advance("wf-1"))
                .willReturn(instance("wf-1", 1, WorkflowStatus.RUNNING));

        mockMvc.perform(post("/api/workflows/wf-1/advance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wf-1"))
                .andExpect(jsonPath("$.currentStep").value(1))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void getReturnsInstance() throws Exception {
        given(workflowService.get("wf-1"))
                .willReturn(instance("wf-1", 1, WorkflowStatus.RUNNING));

        mockMvc.perform(get("/api/workflows/wf-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wf-1"))
                .andExpect(jsonPath("$.workflowType").value("order"));
    }

    @Test
    void cancelReturnsCancelledInstance() throws Exception {
        given(workflowService.cancel("wf-1"))
                .willReturn(instance("wf-1", 0, WorkflowStatus.CANCELLED));

        mockMvc.perform(post("/api/workflows/wf-1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void startValidationFailureReturnsBadRequest() throws Exception {
        given(workflowService.start(any(StartWorkflowCommand.class)))
                .willThrow(new WorkflowValidationException("steps must not be empty"));

        mockMvc.perform(post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"workflowType":"order","steps":["validate"],"variables":{}}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WORKFLOW_VALIDATION"))
                .andExpect(jsonPath("$.message").value("steps must not be empty"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void invalidRequestBodyReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"workflowType":"  ","steps":["validate"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WORKFLOW_VALIDATION"))
                .andExpect(jsonPath("$.message").value("workflowType must not be blank"));
    }

    @Test
    void getMissingInstanceReturnsNotFound() throws Exception {
        given(workflowService.get("missing"))
                .willThrow(new WorkflowNotFoundException("workflow instance not found: missing"));

        mockMvc.perform(get("/api/workflows/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKFLOW_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("workflow instance not found: missing"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void advanceCompletedInstanceReturnsConflict() throws Exception {
        given(workflowService.advance("wf-1"))
                .willThrow(new WorkflowStateException("workflow instance is not running: wf-1"));

        mockMvc.perform(post("/api/workflows/wf-1/advance"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKFLOW_STATE_CONFLICT"))
                .andExpect(jsonPath("$.message").value("workflow instance is not running: wf-1"));
    }

    @Test
    void genericWorkflowExceptionReturnsInternalError() throws Exception {
        given(workflowService.get("wf-1"))
                .willThrow(new WorkflowException("domain failure"));

        mockMvc.perform(get("/api/workflows/wf-1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("WORKFLOW_INTERNAL"))
                .andExpect(jsonPath("$.message").value("workflow internal error"));
    }

    @Test
    void unexpectedExceptionReturnsInternalError() throws Exception {
        given(workflowService.get("wf-1"))
                .willThrow(new IllegalStateException("boom"));

        mockMvc.perform(get("/api/workflows/wf-1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("unexpected server error"));
    }

    private static WorkflowInstance instance(String id, int currentStep, WorkflowStatus status) {
        return new WorkflowInstance(
                id,
                "order",
                List.of("validate", "charge"),
                currentStep,
                status,
                Map.of("amount", 42),
                CREATED_AT,
                UPDATED_AT);
    }
}