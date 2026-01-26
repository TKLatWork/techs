package me.domainService.application.service;

import lombok.AllArgsConstructor;
import me.domainService.application.repo.EnvRepo;
import me.domainService.application.repo.TaskRepo;
import me.domainService.application.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EnvTaskService {

    UserRepo userRepo;
    EnvRepo envRepo;
    TaskRepo taskRepo;

    public void runTask(String userName, String envName, String taskName){
        var task = taskRepo.get(taskName);
        var env = envRepo.get(envName);
        var user = userRepo.get(userName);
        env.runTask(user, task);
    }

}
