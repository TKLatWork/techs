package me.domainService.application.repo;

import me.domainService.application.domain.Task;

public interface TaskRepo {
    Task get(String name);
}
