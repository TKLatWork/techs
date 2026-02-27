package me.domainService.domains.tasking;

import lombok.Data;

@Data
public class TaskEnv {

    private String name;
    private Integer status;

    public static TaskEnv getEnv(String targetEnvName) {
        TaskEnv env = new TaskEnv();
        env.setName(targetEnvName);
        return env;
    }
}
