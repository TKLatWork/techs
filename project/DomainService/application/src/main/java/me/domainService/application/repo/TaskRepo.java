package me.domainService.application.repo;

import me.domainService.application.entity.Task;

public interface TaskRepo {
    Task get(String name);
}
