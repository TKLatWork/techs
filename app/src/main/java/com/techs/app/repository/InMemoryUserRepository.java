package com.techs.app.repository;

import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, User> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> usernameIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        usernameIndex.put(user.getUsername(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String id = usernameIndex.get(username);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String id) {
        User user = store.remove(id);
        if (user != null) {
            usernameIndex.remove(user.getUsername());
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return usernameIndex.containsKey(username);
    }
}
