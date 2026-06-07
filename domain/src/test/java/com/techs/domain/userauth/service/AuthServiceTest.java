package com.techs.domain.userauth.service;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.Session;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.SessionRepository;
import com.techs.domain.userauth.repository.UserRepository;
import com.techs.domain.userauth.valueobject.RightAction;
import com.techs.domain.userauth.valueobject.RightType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock SessionRepository sessionRepository;
    @Mock PasswordEncoder passwordEncoder;

    private AuthorizationService authorizationService;
    private AuthService authService;

    private User testUser;
    private Role visitorRole;
    private Right readProfileRight;

    @BeforeEach
    void setUp() {
        readProfileRight = Right.builder()
                .id("right-1")
                .name("read:/profile")
                .type(RightType.BASE)
                .urlPattern("/profile")
                .action(RightAction.READ)
                .source("BUILT_IN")
                .builtIn(true)
                .build();

        visitorRole = Role.builder()
                .id("role-visitor")
                .name("Visitor")
                .description("Limited access")
                .rightIds(Set.of("right-1"))
                .builtIn(true)
                .immutable(false)
                .build();

        testUser = User.builder()
                .id("user-1")
                .username("testuser")
                .displayName("Test User")
                .passwordHash("$2a$10$hashedpassword")
                .roleId("role-visitor")
                .individualRightIds(new HashSet<>())
                .createdAt(Instant.now())
                .build();

        authorizationService = new AuthorizationService(userRepository, roleRepository,
                mock(com.techs.domain.userauth.repository.RightRepository.class));

        lenient().when(roleRepository.findById("role-visitor")).thenReturn(Optional.of(visitorRole));
        lenient().when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        authService = new AuthService(userRepository, roleRepository, sessionRepository,
                passwordEncoder, authorizationService);
    }

    @Test
    void loginWithValidCredentialsReturnsToken() {
        when(passwordEncoder.matches("password", "$2a$10$hashedpassword")).thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));
        var rightRepo = mock(com.techs.domain.userauth.repository.RightRepository.class);
        when(rightRepo.findById("right-1")).thenReturn(Optional.of(readProfileRight));
        authorizationService = new AuthorizationService(userRepository, roleRepository, rightRepo);
        authService = new AuthService(userRepository, roleRepository, sessionRepository,
                passwordEncoder, authorizationService);

        AuthService.LoginResult result = authService.login("testuser", "password");

        assertNotNull(result.token());
        assertNotNull(result.expiresAt());
        assertEquals("testuser", result.user().getUsername());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void loginWithInvalidUsernameThrows() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(AuthService.AuthenticationException.class,
                () -> authService.login("unknown", "password"));
    }

    @Test
    void loginWithWrongPasswordThrows() {
        when(passwordEncoder.matches("wrong", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(AuthService.AuthenticationException.class,
                () -> authService.login("testuser", "wrong"));
    }

    @Test
    void loginWithBlankUsernameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login("", "password"));
    }

    @Test
    void loginWithBlankPasswordThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login("testuser", ""));
    }

    @Test
    void registerWithUniqueUsernameCreatesUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findByName("Visitor")).thenReturn(Optional.of(visitorRole));
        when(passwordEncoder.encode("secret")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register("newuser", "secret", "New User");

        assertEquals("newuser", result.getUsername());
        assertEquals("New User", result.getDisplayName());
        assertEquals("role-visitor", result.getRoleId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerWithDuplicateUsernameThrows() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register("testuser", "secret", "Dup User"));
    }

    @Test
    void registerWithBlankFieldsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.register("", "secret", "User"));
        assertThrows(IllegalArgumentException.class,
                () -> authService.register("user", "", "User"));
        assertThrows(IllegalArgumentException.class,
                () -> authService.register("user", "secret", ""));
    }

    @Test
    void getCurrentUserWithValidTokenReturnsUser() {
        Session session = Session.builder()
                .token("valid-token")
                .userId("user-1")
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();
        when(sessionRepository.findByToken("valid-token")).thenReturn(Optional.of(session));

        AuthService.CurrentUserResult result = authService.getCurrentUser("valid-token");

        assertEquals("testuser", result.user().getUsername());
    }

    @Test
    void getCurrentUserWithInvalidTokenThrows() {
        when(sessionRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(AuthService.AuthenticationException.class,
                () -> authService.getCurrentUser("invalid"));
    }

    @Test
    void getCurrentUserWithExpiredTokenThrows() {
        Session session = Session.builder()
                .token("expired-token")
                .userId("user-1")
                .createdAt(Instant.now().minusSeconds(86400 * 8))
                .expiresAt(Instant.now().minusSeconds(86400))
                .build();
        when(sessionRepository.findByToken("expired-token")).thenReturn(Optional.of(session));

        assertThrows(AuthService.AuthenticationException.class,
                () -> authService.getCurrentUser("expired-token"));
    }
}
