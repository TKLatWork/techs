package com.techs.domain.userauth.repository;

import com.techs.domain.userauth.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void deleteById(String id);
    boolean existsByUsername(String username);
}
