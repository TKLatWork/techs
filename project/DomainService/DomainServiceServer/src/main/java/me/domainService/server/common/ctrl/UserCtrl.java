package me.domainService.server.common.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.model.User;
import me.domainService.server.common.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserCtrl {

    private UserService userService;

    @RequestMapping("/get/{username}")
    public User get(@PathVariable String username) {
        log.info("Getting user: {}", username);
        return userService.get(username);
    }

}
