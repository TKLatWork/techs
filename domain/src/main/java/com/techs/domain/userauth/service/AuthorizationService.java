package com.techs.domain.userauth.service;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RightRepository;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class AuthorizationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;

    public AuthorizationService(UserRepository userRepository,
                                RoleRepository roleRepository,
                                RightRepository rightRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
    }

    public List<Right> getEffectiveRights(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + user.getRoleId()));

        if ("Admin".equals(role.getName())) {
            return rightRepository.findAll();
        }

        Set<String> allRightIds = new HashSet<>(role.getRightIds());
        allRightIds.addAll(user.getIndividualRightIds());

        return allRightIds.stream()
                .map(rightRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Right> getIndividualRights(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return user.getIndividualRightIds().stream()
                .map(rightRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean hasRight(String userId, String rightName) {
        List<Right> effectiveRights = getEffectiveRights(userId);
        return effectiveRights.stream().anyMatch(r -> r.getName().equals(rightName));
    }
}
