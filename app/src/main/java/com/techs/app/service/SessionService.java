package com.techs.app.service;

import com.techs.domain.userauth.entity.Session;
import com.techs.domain.userauth.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Optional<Session> validateSession(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Optional<Session> session = sessionRepository.findByToken(token);
        if (session.isPresent() && session.get().getExpiresAt().isAfter(Instant.now())) {
            return session;
        }
        if (session.isPresent()) {
            sessionRepository.deleteByToken(token);
        }
        return Optional.empty();
    }

    public void invalidateSession(String token) {
        sessionRepository.deleteByToken(token);
    }
}
