package me.domainService.server.common.service;

import me.domainService.server.common.model.Env;
import me.domainService.server.common.model.Task;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskService {

    Map<String, Task> envMap = Map.of(
        "TASK1", new Task(){{
            setName("TASK1");
            setTaskId("1");
        }},
        "TASK2", new Task(){{
            setName("TASK2");
            setTaskId("2");
        }}
    );

    public Task get(String name){
        return envMap.get(name);
    }



}
