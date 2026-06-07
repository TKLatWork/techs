package com.techs.app.controller;

import com.techs.api.userauth.model.*;
import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.app.service.SessionService;
import com.techs.domain.userauth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RoleRepository roleRepository;
    private final SessionService sessionService;

    public AuthController(AuthService authService, RoleRepository roleRepository, SessionService sessionService) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder()
                                .message("Username and password are required")
                                .code("BLANK_CREDENTIALS")
                                .timestamp(Instant.now())
                                .build());
            }

            AuthService.LoginResult result = authService.login(request.getUsername(), request.getPassword());
            String roleName = roleRepository.findById(result.user().getRoleId())
                    .map(Role::getName).orElse("Unknown");

            LoginResponse response = LoginResponse.builder()
                    .token(result.token())
                    .expiresAt(result.expiresAt())
                    .user(toUserDto(result.user(), roleName, result.effectiveRights(), result.individualRights()))
                    .build();
            return ResponseEntity.ok(response);
        } catch (AuthService.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponse.builder()
                            .message("Invalid username or password")
                            .code("INVALID_CREDENTIALS")
                            .timestamp(Instant.now())
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .message(e.getMessage())
                            .code("BLANK_CREDENTIALS")
                            .timestamp(Instant.now())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getUsername(), request.getPassword(), request.getDisplayName());
            String roleName = roleRepository.findById(user.getRoleId())
                    .map(Role::getName).orElse("Visitor");

            RegisterResponse response = RegisterResponse.builder()
                    .user(toUserDto(user, roleName, List.of(), List.of()))
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            String code = e.getMessage().contains("already taken") ? "USERNAME_TAKEN" : "BLANK_FIELD";
            HttpStatus status = code.equals("USERNAME_TAKEN") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ErrorResponse.builder()
                            .message(e.getMessage())
                            .code(code)
                            .timestamp(Instant.now())
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            sessionService.invalidateSession(token);
        }
        return ResponseEntity.noContent().build();
    }

    private UserDto toUserDto(User user, String roleName, List<Right> effectiveRights, List<Right> individualRights) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .roleName(roleName)
                .createdAt(user.getCreatedAt())
                .effectiveRights(effectiveRights.stream().map(this::toRightDto).toList())
                .individualRights(individualRights.stream().map(this::toRightDto).toList())
                .build();
    }

    private RightDto toRightDto(Right right) {
        return RightDto.builder()
                .id(right.getId())
                .name(right.getName())
                .type(right.getType().name())
                .urlPattern(right.getUrlPattern())
                .action(right.getAction() != null ? right.getAction().name() : null)
                .description(right.getDescription())
                .source(right.getSource())
                .featureId(right.getFeatureId())
                .build();
    }
}
