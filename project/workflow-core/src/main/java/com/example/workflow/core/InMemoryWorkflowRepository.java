package com.example.workflow.core;

import com.example.workflow.api.WorkflowInstance;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory workflow repository.
 */
@Repository
public class InMemoryWorkflowRepository implements WorkflowRepository {

    private final ConcurrentMap<String, WorkflowInstance> store = new ConcurrentHashMap<>();

    @Override
    public WorkflowInstance save(WorkflowInstance instance) {
        Objects.requireNonNull(instance, "instance must not be null");
        store.put(instance.id(), instance);
        return instance;
    }

    @Override
    public Optional<WorkflowInstance> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }
}