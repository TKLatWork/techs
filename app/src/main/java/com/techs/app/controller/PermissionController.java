package com.techs.app.controller;

import com.techs.api.userauth.model.*;
import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RightRepository;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.UserRepository;
import com.techs.domain.userauth.service.AuthorizationService;
import com.techs.domain.userauth.service.AuthorizationServiceManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final AuthorizationServiceManagement management;
    private final AuthorizationService authorizationService;
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;
    private final UserRepository userRepository;

    public PermissionController(AuthorizationServiceManagement management,
                                 AuthorizationService authorizationService,
                                 RoleRepository roleRepository,
                                 RightRepository rightRepository,
                                 UserRepository userRepository) {
        this.management = management;
        this.authorizationService = authorizationService;
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> listRoles() {
        List<RoleDto> roles = roleRepository.findAll().stream()
                .map(this::toRoleDto)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody RoleDto request) {
        try {
            Role role = management.createRole(request.getName(), request.getDescription(),
                    request.getRightIds() != null ? request.getRightIds() : Set.of());
            return ResponseEntity.status(HttpStatus.CREATED).body(toRoleDto(role));
        } catch (IllegalArgumentException e) {
            String code = e.getMessage().contains("already exists") ? "ROLE_NAME_TAKEN" :
                    e.getMessage().contains("Right not found") ? "RIGHT_NOT_FOUND" : "BLANK_FIELD";
            HttpStatus status = code.equals("ROLE_NAME_TAKEN") ? HttpStatus.CONFLICT :
                    code.equals("RIGHT_NOT_FOUND") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorResponse(e.getMessage(), code));
        }
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable("roleId") String roleId, @RequestBody RoleDto request) {
        try {
            Role role = management.updateRole(roleId, request.getName(), request.getDescription(),
                    request.getRightIds());
            return ResponseEntity.ok(toRoleDto(role));
        } catch (AuthorizationServiceManagement.RoleImmutableException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse(e.getMessage(), "ROLE_IMMUTABLE"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage(), "ROLE_NOT_FOUND"));
        }
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable("roleId") String roleId) {
        try {
            management.deleteRole(roleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            String code = e.getMessage().contains("built-in") ? "ROLE_BUILT_IN" : "ROLE_NOT_FOUND";
            HttpStatus status = code.equals("ROLE_BUILT_IN") ? HttpStatus.FORBIDDEN : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(errorResponse(e.getMessage(), code));
        }
    }

    @GetMapping("/rights")
    public ResponseEntity<List<RightDto>> listRights() {
        List<RightDto> rights = rightRepository.findAll().stream()
                .map(this::toRightDto)
                .toList();
        return ResponseEntity.ok(rights);
    }

    @DeleteMapping("/rights/{rightId}")
    public ResponseEntity<?> deleteRight(@PathVariable("rightId") String rightId) {
        try {
            management.deleteRight(rightId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            String code = e.getMessage().contains("built-in") ? "RIGHT_BUILT_IN" : "RIGHT_NOT_FOUND";
            HttpStatus status = code.equals("RIGHT_BUILT_IN") ? HttpStatus.FORBIDDEN : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(errorResponse(e.getMessage(), code));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserPermissionsDto>> listUsers() {
        List<UserPermissionsDto> users = userRepository.findAll().stream()
                .map(this::toUserPermissionsDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> assignRole(@PathVariable("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String roleId = body.get("roleId");
            User user = management.assignRole(userId, roleId);
            return ResponseEntity.ok(toUserPermissionsDto(user));
        } catch (IllegalArgumentException e) {
            String code = e.getMessage().contains("User not found") ? "USER_NOT_FOUND" : "ROLE_NOT_FOUND";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage(), code));
        }
    }

    @PostMapping("/users/{userId}/rights")
    public ResponseEntity<?> addIndividualRight(@PathVariable("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String rightId = body.get("rightId");
            User user = management.addIndividualRight(userId, rightId);
            return ResponseEntity.ok(toUserPermissionsDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage(), "NOT_FOUND"));
        }
    }

    @DeleteMapping("/users/{userId}/rights/{rightId}")
    public ResponseEntity<?> removeIndividualRight(@PathVariable("userId") String userId, @PathVariable("rightId") String rightId) {
        try {
            User user = management.removeIndividualRight(userId, rightId);
            return ResponseEntity.ok(toUserPermissionsDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage(), "USER_NOT_FOUND"));
        }
    }

    @GetMapping("/features")
    public ResponseEntity<List<Map<String, Object>>> listFeatures() {
        Map<String, List<Right>> byFeature = rightRepository.findAll().stream()
                .filter(r -> r.getFeatureId() != null)
                .collect(Collectors.groupingBy(Right::getFeatureId));

        List<Map<String, Object>> features = byFeature.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> feature = new LinkedHashMap<>();
                    feature.put("featureId", entry.getKey());
                    feature.put("rightCount", entry.getValue().size());
                    feature.put("rights", entry.getValue().stream().map(this::toRightDto).toList());
                    return feature;
                })
                .toList();
        return ResponseEntity.ok(features);
    }

    @PostMapping("/features/{featureId}/disable")
    public ResponseEntity<?> disableFeature(@PathVariable("featureId") String featureId) {
        try {
            management.disableFeature(featureId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage(), "FEATURE_NOT_FOUND"));
        }
    }

    private RoleDto toRoleDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .rightIds(role.getRightIds())
                .builtIn(role.isBuiltIn())
                .immutable(role.isImmutable())
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

    private UserPermissionsDto toUserPermissionsDto(User user) {
        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        List<Right> effectiveRights = authorizationService.getEffectiveRights(user.getId());
        List<Right> individualRights = authorizationService.getIndividualRights(user.getId());

        return UserPermissionsDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(role != null ? RoleDto.builder().id(role.getId()).name(role.getName()).build() : null)
                .effectiveRights(effectiveRights.stream().map(this::toRightDto).toList())
                .individualRights(individualRights.stream().map(this::toRightDto).toList())
                .build();
    }

    private ErrorResponse errorResponse(String message, String code) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .timestamp(Instant.now())
                .build();
    }
}
