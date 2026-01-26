package me.domainService.application.repo;

import me.domainService.application.entity.Env;

public interface EnvRepo {

    Env get(String name);
}
