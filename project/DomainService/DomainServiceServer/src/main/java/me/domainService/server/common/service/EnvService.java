package me.domainService.server.common.service;

import me.domainService.server.common.model.Env;
import me.domainService.server.common.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EnvService {

    Map<String, Env> envMap = Map.of(
        "DEV", new Env(){{
            setName("DEV");
            setStatus(STATUS_IDLE);
        }},
        "PROD", new Env(){{
            setName("PROD");
            setStatus(STATUS_IDLE);
        }}
    );

    public Env get(String name){
        return envMap.get(name);
    }

}
