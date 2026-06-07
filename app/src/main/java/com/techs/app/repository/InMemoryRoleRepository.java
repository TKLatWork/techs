package com.techs.app.repository;

import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRoleRepository implements RoleRepository {

    private final ConcurrentHashMap<String, Role> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> nameIndex = new ConcurrentHashMap<>();

    @Override
    public Role save(Role role) {
        store.put(role.getId(), role);
        nameIndex.put(role.getName(), role.getId());
        return role;
    }

    @Override
    public Optional<Role> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Role> findByName(String name) {
        String id = nameIndex.get(name);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String id) {
        Role role = store.remove(id);
        if (role != null) {
            nameIndex.remove(role.getName());
        }
    }
}
