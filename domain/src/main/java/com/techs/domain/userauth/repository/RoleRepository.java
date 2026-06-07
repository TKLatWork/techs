package com.techs.domain.userauth.repository;

import com.techs.domain.userauth.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(String id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    void deleteById(String id);
}
