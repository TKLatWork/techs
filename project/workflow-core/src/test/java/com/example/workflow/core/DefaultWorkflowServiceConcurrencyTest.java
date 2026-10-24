package com.example.workflow.core;

import com.example.workflow.api.StartWorkflowCommand;
import com.example.workflow.api.WorkflowInstance;
import com.example.workflow.api.WorkflowStateException;
import com.example.workflow.api.WorkflowStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultWorkflowServiceConcurrencyTest {

    private static final int THREADS = 16;
    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final InMemoryWorkflowRepository repository = new InMemoryWorkflowRepository();
    private final DefaultWorkflowService service = new DefaultWorkflowService(repository);
    private final ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    private static StartWorkflowCommand command(String... steps) {
        return new StartWorkflowCommand(
                "order-processing",
                List.of(steps),
                Map.of("orderId", "order-1"));
    }

    private <T> List<T> runConcurrently(List<Callable<T>> tasks) {
        CountDownLatch gate = new CountDownLatch(1);
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            futures.add(executor.submit(() -> {
                gate.await();
                return task.call();
            }));
        }
        gate.countDown();

        List<T> results = new ArrayList<>(tasks.size());
        for (Future<T> future : futures) {
            try {
                results.add(future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                throw new AssertionError("concurrent task failed", e);
            }
        }
        return results;
    }

    @Test
    void concurrentStartStoresEveryInstance() {
        int total = 200;
        List<Callable<WorkflowInstance>> tasks = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            tasks.add(() -> service.start(command("validate", "pay")));
        }

        List<WorkflowInstance> instances = runConcurrently(tasks);

        Set<String> ids = new HashSet<>();
        for (WorkflowInstance instance : instances) {
            assertThat(instance.id()).isNotBlank();
            assertThat(ids.add(instance.id())).as("duplicate id %s", instance.id()).isTrue();
            assertThat(service.get(instance.id())).isEqualTo(instance);
        }
        assertThat(ids).hasSize(total);
    }

    @Test
    void concurrentAdvanceAcrossDifferentInstancesKeepsEveryInstanceConsistent() {
        int total = 100;
        List<WorkflowInstance> started = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            started.add(service.start(command("validate", "pay", "ship")));
        }

        List<Callable<WorkflowInstance>> tasks = new ArrayList<>(total);
        for (WorkflowInstance instance : started) {
            tasks.add(() -> service.advance(instance.id()));
        }

        List<WorkflowInstance> advanced = runConcurrently(tasks);

        for (WorkflowInstance instance : advanced) {
            assertThat(instance.currentStep()).isEqualTo(1);
            assertThat(instance.status()).isEqualTo(WorkflowStatus.RUNNING);
            assertThat(repository.findById(instance.id())).contains(instance);
        }
    }

    @Test
    void concurrentAdvanceOnSameInstanceAppliesEachTransitionOnce() {
        WorkflowInstance started = service.start(command("validate", "pay"));
        int attempts = 16;
        CountDownLatch gate = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger conflicts = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>(attempts);
        for (int i = 0; i < attempts; i++) {
            futures.add(executor.submit(() -> {
                gate.await();
                try {
                    service.advance(started.id());
                    successes.incrementAndGet();
                } catch (WorkflowStateException expected) {
                    conflicts.incrementAndGet();
                }
                return null;
            }));
        }
        gate.countDown();
        for (Future<?> future : futures) {
            try {
                future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new AssertionError("concurrent advance failed", e);
            }
        }

        WorkflowInstance stored = service.get(started.id());
        assertThat(successes.get()).isEqualTo(2);
        assertThat(conflicts.get()).isEqualTo(attempts - 2);
        assertThat(stored.status()).isEqualTo(WorkflowStatus.COMPLETED);
        assertThat(stored.currentStep()).isEqualTo(1);
    }

    @Test
    void concurrentCancelOnSameInstanceIsIdempotentForEveryCaller() {
        WorkflowInstance started = service.start(command("validate", "pay"));
        int attempts = 16;
        CountDownLatch gate = new CountDownLatch(1);

        List<Future<WorkflowInstance>> futures = new ArrayList<>(attempts);
        for (int i = 0; i < attempts; i++) {
            futures.add(executor.submit(() -> {
                gate.await();
                return service.cancel(started.id());
            }));
        }
        gate.countDown();

        for (Future<WorkflowInstance> future : futures) {
            try {
                assertThat(future.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS).status())
                        .isEqualTo(WorkflowStatus.CANCELLED);
            } catch (Exception e) {
                throw new AssertionError("concurrent cancel failed", e);
            }
        }
        assertThat(service.get(started.id()).status()).isEqualTo(WorkflowStatus.CANCELLED);
    }
}