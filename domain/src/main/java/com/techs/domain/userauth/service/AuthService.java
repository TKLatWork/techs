package com.techs.domain.userauth.service;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Session;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.SessionRepository;
import com.techs.domain.userauth.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;
    private final UserValidator userValidator;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       SessionRepository sessionRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorizationService = authorizationService;
        this.userValidator = new UserValidator(userRepository);
    }

    public LoginResult login(String username, String password) {
        userValidator.validateCredentials(username, password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Instant now = Instant.now();
        Session session = Session.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .createdAt(now)
                .expiresAt(now.plusSeconds(7L * 24 * 60 * 60))
                .build();
        sessionRepository.save(session);

        List<Right> effectiveRights = authorizationService.getEffectiveRights(user.getId());
        List<Right> individualRights = authorizationService.getIndividualRights(user.getId());

        return new LoginResult(session.getToken(), session.getExpiresAt(), user, effectiveRights, individualRights);
    }

    public User register(String username, String password, String displayName) {
        userValidator.validateForRegistration(username, displayName, password);

        var visitorRole = roleRepository.findByName("Visitor")
                .orElseThrow(() -> new IllegalStateException("Visitor role not found"));

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .displayName(displayName)
                .passwordHash(passwordEncoder.encode(password))
                .roleId(visitorRole.getId())
                .createdAt(Instant.now())
                .build();

        return userRepository.save(user);
    }

    public CurrentUserResult getCurrentUser(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Invalid session"));

        if (session.getExpiresAt().isBefore(Instant.now())) {
            sessionRepository.deleteByToken(token);
            throw new AuthenticationException("Session expired");
        }

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        List<Right> effectiveRights = authorizationService.getEffectiveRights(user.getId());
        List<Right> individualRights = authorizationService.getIndividualRights(user.getId());

        return new CurrentUserResult(user, effectiveRights, individualRights);
    }

    public record LoginResult(String token, Instant expiresAt, User user,
                               List<Right> effectiveRights, List<Right> individualRights) {}

    public record CurrentUserResult(User user, List<Right> effectiveRights, List<Right> individualRights) {}

    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}
