package me.domainService.application.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Task {
    private String name;
    private String taskId;

    public void execute(Env env){
        log.info("Default task execute method called for task: {}", name);
        env.getWorks().add(env.getName() + " executed by task " + name);
    }
}
