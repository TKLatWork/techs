package me.task.domain.app;


import me.task.domain.domain.Health;

public class HealthApp {
    public Health checkHealth() {
        return Health.up("");
    }
}
