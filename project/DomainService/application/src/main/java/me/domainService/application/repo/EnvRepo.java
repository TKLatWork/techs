package me.domainService.application.repo;

import me.domainService.application.domain.Env;

public interface EnvRepo {

    Env get(String name);
}
