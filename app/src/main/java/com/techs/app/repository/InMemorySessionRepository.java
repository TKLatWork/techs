package com.techs.app.repository;

import com.techs.domain.userauth.entity.Session;
import com.techs.domain.userauth.repository.SessionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySessionRepository implements SessionRepository {

    private final ConcurrentHashMap<String, Session> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> userIdIndex = new ConcurrentHashMap<>();

    @Override
    public Session save(Session session) {
        store.put(session.getToken(), session);
        userIdIndex.computeIfAbsent(session.getUserId(), k -> ConcurrentHashMap.newKeySet())
                .add(session.getToken());
        return session;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return Optional.ofNullable(store.get(token));
    }

    @Override
    public Set<String> findTokensByUserId(String userId) {
        return userIdIndex.getOrDefault(userId, Set.of());
    }

    @Override
    public void deleteByToken(String token) {
        Session session = store.remove(token);
        if (session != null) {
            Set<String> tokens = userIdIndex.get(session.getUserId());
            if (tokens != null) {
                tokens.remove(token);
                if (tokens.isEmpty()) {
                    userIdIndex.remove(session.getUserId());
                }
            }
        }
    }
}
