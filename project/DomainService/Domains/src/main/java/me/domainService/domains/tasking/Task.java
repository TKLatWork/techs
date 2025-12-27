package me.domainService.domains.tasking;

import lombok.Data;

import java.util.Date;

@Data
public class Task {

    private String id;
    private String name;
    private TaskEnv targetEnv;
    private String result;

//    static public Task create(String name, String targetEnvName){
//        TaskEnv env = TaskEnv.getEnv(targetEnvName);
//    }

    public void runTask() {
        this.result = "Task " + name + " completed successfully, Date: " + new Date();
    }


}
