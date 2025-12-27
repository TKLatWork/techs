package me.domainService.server.common.service;

import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.model.Env;
import me.domainService.server.common.model.Task;
import me.domainService.server.common.model.User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EnvTaskService {

    public void bookEnv(User user, Env env){
        if(env.getStatus().equals(Env.STATUS_IDLE)){
            env.setStatus(Env.STATUS_BOOKED);
            log.info("Env {} booked by user {}", env.getName(), user.getName());
        }else{
            throw new RuntimeException("Env is already booked");
        }
    }

    public void runTask(Task task, Env env){
        if(env.getStatus().equals(Env.STATUS_BOOKED)){
            log.info("Running task {} on env {}", task.getName(), env.getName());
            env.setStatus(Env.STATUS_RUNNING);
            task.execute(env);
            env.setStatus(Env.STATUS_IDLE);
            log.info("Task {} completed on env {}", task.getName(), env.getName());
        }else{
            throw new RuntimeException("Env is not booked");
        }
    }

}
