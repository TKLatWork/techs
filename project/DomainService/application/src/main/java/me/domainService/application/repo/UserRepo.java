package me.domainService.application.repo;

import me.domainService.application.domain.User;


public interface UserRepo {

    User get(String name);
}
