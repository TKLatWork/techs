package com.myapp.backend.service;

import com.myapp.backend.model.User;
import com.myapp.backend.repository.UserRepository;
import com.myapp.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "encodedPassword");
        testUser.setRoles("ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("newuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(new User("newuser", "encodedPassword")));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-token");

        Map<String, String> result = userService.register("newuser", "password");

        assertNotNull(result);
        assertEquals("User registered successfully", result.get("message"));
        assertEquals("test-token", result.get("token"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenUserExists() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.register("existinguser", "password");
        });
        assertEquals("Username already exists", assertThrows(RuntimeException.class, () -> {
            userService.register("existinguser", "password");
        }).getMessage());
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test-token");

        Map<String, String> result = userService.login("testuser", "password");

        assertNotNull(result);
        assertEquals("test-token", result.get("token"));
        assertEquals("testuser", result.get("username"));
    }

    @Test
    void login_shouldThrowExceptionWhenCredentialsInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });
    }

    @Test
    void getProfile_shouldReturnUserProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Map<String, Object> profile = userService.getProfile("testuser");

        assertNotNull(profile);
        assertEquals("testuser", profile.get("username"));
        assertEquals("ROLE_USER", profile.get("roles"));
    }

    @Test
    void getProfile_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getProfile("nonexistent");
        });
    }
}
