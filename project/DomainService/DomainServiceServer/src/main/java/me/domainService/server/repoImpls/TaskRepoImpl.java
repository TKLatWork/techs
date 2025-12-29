package me.domainService.server.repoImpls;

import me.domainService.application.domain.Task;
import me.domainService.application.repo.TaskRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskRepoImpl implements TaskRepo {

    final Map<String, Task> envMap = Map.of(
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
