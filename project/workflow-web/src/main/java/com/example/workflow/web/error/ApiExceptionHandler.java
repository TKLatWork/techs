package com.example.workflow.web.error;

import com.example.workflow.api.WorkflowException;
import com.example.workflow.api.WorkflowNotFoundException;
import com.example.workflow.api.WorkflowStateException;
import com.example.workflow.api.WorkflowValidationException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(WorkflowValidationException.class)
    public ResponseEntity<ApiError> handleValidation(WorkflowValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "WORKFLOW_VALIDATION", ex.getMessage());
    }

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(WorkflowNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "WORKFLOW_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(WorkflowStateException.class)
    public ResponseEntity<ApiError> handleState(WorkflowStateException ex) {
        return error(HttpStatus.CONFLICT, "WORKFLOW_STATE_CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(WorkflowException.class)
    public ResponseEntity<ApiError> handleWorkflow(WorkflowException ex) {
        log.error("Unhandled workflow exception", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "WORKFLOW_INTERNAL", "workflow internal error");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex) {
        WorkflowValidationException validation = findValidation(ex);
        String message = validation != null ? validation.getMessage() : "malformed request body";
        return error(HttpStatus.BAD_REQUEST, "WORKFLOW_VALIDATION", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "unexpected server error");
    }

    private static WorkflowValidationException findValidation(Throwable throwable) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current instanceof WorkflowValidationException validation) {
                return validation;
            }
            if (current.getCause() == current) {
                break;
            }
        }
        return null;
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(code, message, Instant.now()));
    }
}