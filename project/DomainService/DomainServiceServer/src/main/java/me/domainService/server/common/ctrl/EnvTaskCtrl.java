package me.domainService.server.common.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.service.EnvService;
import me.domainService.server.common.service.EnvTaskService;
import me.domainService.server.common.service.TaskService;
import me.domainService.server.common.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/envTask")
public class EnvTaskCtrl {

    private EnvTaskService envTaskService;
    private EnvService envService;
    private UserService userService;
    private TaskService taskService;

    @RequestMapping("/bookEnv/{username}/{envName}")
    public void bookEnv(@PathVariable String username, @PathVariable String envName) {
        log.info("Request book env: {} for user: {}", envName, username);
        var user = userService.get(username);
        var env = envService.get(envName);
        envTaskService.bookEnv(user, env);
    }

    @RequestMapping("/runTask/{taskName}/{envName}")
    public void runTask(@PathVariable String taskName, @PathVariable String envName) {
        log.info("Request run task: {} on env: {}", taskName, envName);
        var task = taskService.get(taskName);
        var env = envService.get(envName);
        envTaskService.runTask(task, env);
    }

}
