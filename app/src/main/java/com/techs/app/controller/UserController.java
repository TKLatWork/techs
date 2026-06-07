package com.techs.app.controller;

import com.techs.api.userauth.model.RightDto;
import com.techs.api.userauth.model.UserDto;
import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final AuthService authService;
    private final RoleRepository roleRepository;

    public UserController(AuthService authService, RoleRepository roleRepository) {
        this.authService = authService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = (String) auth.getCredentials();
        try {
            AuthService.CurrentUserResult result = authService.getCurrentUser(token);
            User user = result.user();
            String roleName = roleRepository.findById(user.getRoleId())
                    .map(Role::getName).orElse("Unknown");

            UserDto dto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .displayName(user.getDisplayName())
                    .roleName(roleName)
                    .createdAt(user.getCreatedAt())
                    .effectiveRights(result.effectiveRights().stream().map(this::toRightDto).toList())
                    .individualRights(result.individualRights().stream().map(this::toRightDto).toList())
                    .build();
            return ResponseEntity.ok(dto);
        } catch (AuthService.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
