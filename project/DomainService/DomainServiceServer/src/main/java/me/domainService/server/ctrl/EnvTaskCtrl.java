package me.domainService.server.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.domainService.application.service.EnvTaskService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/envTask")
public class EnvTaskCtrl {

    private EnvTaskService envTaskService;


    @RequestMapping("/runTask/{userName}/{taskName}/{envName}")
    public void runTask(@PathVariable String userName, @PathVariable String taskName, @PathVariable String envName) {
        log.info("Request run task: {} on env: {} for user: {}", taskName, envName, userName);
        envTaskService.runTask(userName, envName, taskName);
    }

}
