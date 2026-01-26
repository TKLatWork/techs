package me.domainService.application.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class Env {

    public static String STATUS_IDLE = "Idle";
    public static String STATUS_RUNNING = "Running";

    private String name;
    private String status = STATUS_IDLE;
    private List<String> works = new ArrayList<>();


    public void runTask(User user, Task task) {
        log.info("Attempting to run task {} on environment {}", task.getName(), this.name);
        if (!this.status.equals(STATUS_IDLE)) {
            throw new IllegalStateException("Environment must be idle before running a task.");
        }
        this.status = STATUS_RUNNING;
        task.execute(this);
        this.status = STATUS_IDLE;
    }
}
