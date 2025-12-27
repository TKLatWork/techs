package me.domainService.server.common.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.model.Env;
import me.domainService.server.common.model.User;
import me.domainService.server.common.service.EnvService;
import me.domainService.server.common.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/env")
public class EnvCtrl {

    private EnvService envService;

    @RequestMapping(value = "/get/{envName}")
    public Env get(@PathVariable String envName) {
        log.info("Getting env: {}", envName);
        return envService.get(envName);
    }

}
