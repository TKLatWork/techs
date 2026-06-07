package com.techs.domain.userauth.repository;

import com.techs.domain.userauth.entity.Session;

import java.util.Optional;
import java.util.Set;

public interface SessionRepository {
    Session save(Session session);
    Optional<Session> findByToken(String token);
    Set<String> findTokensByUserId(String userId);
    void deleteByToken(String token);
}
