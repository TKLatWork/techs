package com.example.workflow.core;

import com.example.workflow.api.WorkflowInstance;

import java.util.Optional;

/**
 * Storage port for workflow instances.
 */
public interface WorkflowRepository {

    WorkflowInstance save(WorkflowInstance instance);

    Optional<WorkflowInstance> findById(String id);
}