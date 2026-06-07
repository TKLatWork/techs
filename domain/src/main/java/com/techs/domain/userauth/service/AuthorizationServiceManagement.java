package com.techs.domain.userauth.service;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RightRepository;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.UserRepository;
import com.techs.domain.userauth.valueobject.RightAction;
import com.techs.domain.userauth.valueobject.RightType;

import java.util.*;
import java.util.stream.Collectors;

public class AuthorizationServiceManagement {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;

    public AuthorizationServiceManagement(UserRepository userRepository,
                                           RoleRepository roleRepository,
                                           RightRepository rightRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
    }

    public Role createRole(String name, String description, Set<String> rightIds) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name must not be blank");
        }
        if (roleRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Role name '" + name + "' already exists");
        }
        for (String rightId : rightIds) {
            if (rightRepository.findById(rightId).isEmpty()) {
                throw new IllegalArgumentException("Right not found: " + rightId);
            }
        }
        Role role = Role.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .rightIds(new HashSet<>(rightIds))
                .builtIn(false)
                .immutable(false)
                .build();
        return roleRepository.save(role);
    }

    public Role updateRole(String roleId, String name, String description, Set<String> rightIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        if (role.isImmutable()) {
            throw new RoleImmutableException("Cannot modify immutable role: " + role.getName());
        }
        if (name != null && !name.isBlank()) {
            role.setName(name);
        }
        if (description != null) {
            role.setDescription(description);
        }
        if (rightIds != null) {
            role.setRightIds(new HashSet<>(rightIds));
        }
        return roleRepository.save(role);
    }

    public void deleteRole(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        if (role.isBuiltIn()) {
            throw new IllegalArgumentException("Cannot delete built-in role: " + role.getName());
        }

        Role visitorRole = roleRepository.findByName("Visitor")
                .orElseThrow(() -> new IllegalStateException("Visitor role not found"));

        for (User user : userRepository.findAll()) {
            if (roleId.equals(user.getRoleId())) {
                user.setRoleId(visitorRole.getId());
                userRepository.save(user);
            }
        }
        roleRepository.deleteById(roleId);
    }

    public User assignRole(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        user.setRoleId(roleId);
        return userRepository.save(user);
    }

    public User addIndividualRight(String userId, String rightId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        rightRepository.findById(rightId)
                .orElseThrow(() -> new IllegalArgumentException("Right not found: " + rightId));
        user.getIndividualRightIds().add(rightId);
        return userRepository.save(user);
    }

    public User removeIndividualRight(String userId, String rightId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.getIndividualRightIds().remove(rightId);
        return userRepository.save(user);
    }

    public void disableFeature(String featureId) {
        Set<String> rightIds = rightRepository.findIdsByFeatureId(featureId);
        if (rightIds.isEmpty()) {
            throw new IllegalArgumentException("No rights registered for feature: " + featureId);
        }

        for (Role role : roleRepository.findAll()) {
            role.getRightIds().removeAll(rightIds);
            roleRepository.save(role);
        }
        for (User user : userRepository.findAll()) {
            user.getIndividualRightIds().removeAll(rightIds);
            userRepository.save(user);
        }
        rightRepository.deleteByFeatureId(featureId);
    }

    public void deleteRight(String rightId) {
        Right right = rightRepository.findById(rightId)
                .orElseThrow(() -> new IllegalArgumentException("Right not found: " + rightId));
        if (right.isBuiltIn()) {
            throw new IllegalArgumentException("Cannot delete built-in right: " + right.getName());
        }

        for (Role role : roleRepository.findAll()) {
            role.getRightIds().remove(rightId);
            roleRepository.save(role);
        }
        for (User user : userRepository.findAll()) {
            user.getIndividualRightIds().remove(rightId);
            userRepository.save(user);
        }
        rightRepository.deleteById(rightId);
    }

    public Map<String, Object> registerPermissions(String featureId, List<PermissionInput> permissions) {
        int registered = 0;
        int updated = 0;
        List<Right> result = new ArrayList<>();

        for (PermissionInput input : permissions) {
            Optional<Right> existing = rightRepository.findByName(input.name());
            if (existing.isPresent()) {
                Right right = existing.get();
                right.setDescription(input.description());
                if (input.urlPattern() != null) right.setUrlPattern(input.urlPattern());
                if (input.action() != null) right.setAction(RightAction.valueOf(input.action()));
                rightRepository.save(right);
                result.add(right);
                updated++;
            } else {
                Right right = Right.builder()
                        .id(UUID.randomUUID().toString())
                        .name(input.name())
                        .type(RightType.valueOf(input.type()))
                        .urlPattern(input.urlPattern())
                        .action(input.action() != null ? RightAction.valueOf(input.action()) : null)
                        .description(input.description())
                        .source("FEATURE_REGISTERED")
                        .featureId(featureId)
                        .builtIn(false)
                        .build();
                rightRepository.save(right);
                result.add(right);
                registered++;
            }
        }

        return Map.of("registered", registered, "updated", updated, "rights", result);
    }

    public record PermissionInput(String name, String type, String urlPattern, String action, String description) {}

    public static class RoleImmutableException extends RuntimeException {
        public RoleImmutableException(String message) {
            super(message);
        }
    }
}
