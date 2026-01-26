package me.domainService.application.repo;

import me.domainService.application.entity.User;


public interface UserRepo {

    User get(String name);
}
