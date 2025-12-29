package me.domainService.application.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class Env {

    public static String STATUS_IDLE = "Idle";
    public static String STATUS_BOOKED = "Booked";
    public static String STATUS_RUNNING = "Running";

    private String name;
    private String status = STATUS_IDLE;
    private User bookedBy;
    private List<String> works = new ArrayList<>();

    public void bookBy(User user){
        log.info("Attempting to book environment {} by user {}", this.name, user.getName());
        if(!this.status.equals(STATUS_IDLE)){
            throw new IllegalStateException("Environment is not idle and cannot be booked.");
        }
        this.status = STATUS_BOOKED;
        this.bookedBy = user;
    }

    public void runTask(User user, Task task){
        log.info("Attempting to run task {} on environment {}", task.getName(), this.name);
        if(!this.status.equals(STATUS_BOOKED) || !user.equals(bookedBy)){
            throw new IllegalStateException("Environment must be booked before running a task.");
        }
        this.status = STATUS_RUNNING;
        task.execute(this);
        this.status = STATUS_IDLE;
        this.bookedBy = null;
    }
}
