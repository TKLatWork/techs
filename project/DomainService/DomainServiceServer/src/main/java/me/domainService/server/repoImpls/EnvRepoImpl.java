package me.domainService.server.repoImpls;

import me.domainService.application.entity.Env;
import me.domainService.application.repo.EnvRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EnvRepoImpl implements EnvRepo {

    final Map<String, Env> envMap = Map.of(
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
