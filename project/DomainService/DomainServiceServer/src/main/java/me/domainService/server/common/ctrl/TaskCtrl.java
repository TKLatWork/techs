package me.domainService.server.common.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.model.Task;
import me.domainService.server.common.model.User;
import me.domainService.server.common.service.TaskService;
import me.domainService.server.common.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/task")
public class TaskCtrl {

    private TaskService taskService;

    @RequestMapping("/get/{taskName}")
    public Task get(@PathVariable String taskName) {
        log.info("Getting task: {}", taskName);
        return taskService.get(taskName);
    }

}
