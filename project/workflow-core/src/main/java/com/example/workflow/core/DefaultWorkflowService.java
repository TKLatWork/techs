package com.example.workflow.core;

import com.example.workflow.api.StartWorkflowCommand;
import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowNotFoundException;
import com.example.workflow.api.WorkflowService;
import com.example.workflow.api.WorkflowStatus;
import com.example.workflow.api.WorkflowValidationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Default workflow service backed by a {@link WorkflowRepository}.
 *
 * <p>Instances are immutable snapshots, so read-modify-write transitions
 * ({@link #advance(String)}, {@link #cancel(String)}) are serialized to keep
 * concurrent callers from overwriting each other.</p>
 */
@Service
public class DefaultWorkflowService implements WorkflowService {

    private final WorkflowRepository repository;

    public DefaultWorkflowService(WorkflowRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    public WorkflowInstance start(StartWorkflowCommand command) {
        validate(command);

        Instant now = Instant.now();
        WorkflowInstance instance = new WorkflowInstance(
                UUID.randomUUID().toString(),
                command.workflowType(),
                command.steps(),
                0,
                WorkflowStatus.RUNNING,
                command.variables(),
                now,
                now);

        return repository.save(instance);
    }

    @Override
    public synchronized WorkflowInstance advance(String instanceId) {
        WorkflowInstance current = requireInstance(instanceId);
        return repository.save(WorkflowStateMachine.advance(current));
    }

    @Override
    public WorkflowInstance get(String instanceId) {
        return requireInstance(instanceId);
    }

    @Override
    public synchronized WorkflowInstance cancel(String instanceId) {
        WorkflowInstance current = requireInstance(instanceId);
        if (current.status() == WorkflowStatus.CANCELLED) {
            return current;
        }
        return repository.save(WorkflowStateMachine.cancel(current));
    }

    private WorkflowInstance requireInstance(String instanceId) {
        if (instanceId == null) {
            throw new WorkflowNotFoundException("workflow instance not found: null");
        }
        return repository.findById(instanceId)
                .orElseThrow(() -> new WorkflowNotFoundException(
                        "workflow instance not found: " + instanceId));
    }

    private static void validate(StartWorkflowCommand command) {
        if (command == null) {
            throw new WorkflowValidationException("command must not be null");
        }
        if (command.workflowType() == null || command.workflowType().isBlank()) {
            throw new WorkflowValidationException("workflowType must not be blank");
        }
        List<String> steps = command.steps();
        if (steps == null || steps.isEmpty()) {
            throw new WorkflowValidationException("steps must not be empty");
        }
        for (String step : steps) {
            if (step == null || step.isBlank()) {
                throw new WorkflowValidationException("step must not be blank");
            }
        }
    }
}