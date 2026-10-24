package com.example.workflow.web.controller;

import com.example.workflow.api.StartWorkflowCommand;
import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<WorkflowInstance> start(@RequestBody StartWorkflowCommand command) {
        WorkflowInstance instance = workflowService.start(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(instance);
    }

    @PostMapping("/{id}/advance")
    public WorkflowInstance advance(@PathVariable("id") String id) {
        return workflowService.advance(id);
    }

    @GetMapping("/{id}")
    public WorkflowInstance get(@PathVariable("id") String id) {
        return workflowService.get(id);
    }

    @PostMapping("/{id}/cancel")
    public WorkflowInstance cancel(@PathVariable("id") String id) {
        return workflowService.cancel(id);
    }
}