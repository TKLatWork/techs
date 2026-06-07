package com.techs.domain.userauth.service;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RightRepository;
import com.techs.domain.userauth.repository.RoleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock RightRepository rightRepository;

    private AuthorizationService authorizationService;

    private Right readProfile;
    private Right readAdmin;
    private Right manageUsers;

    @BeforeEach
    void setUp() {
        readProfile = Right.builder()
                .id("r1").name("read:/profile").type(RightType.BASE)
                .urlPattern("/profile").action(RightAction.READ)
                .source("BUILT_IN").builtIn(true).build();
        readAdmin = Right.builder()
                .id("r2").name("read:/admin").type(RightType.BASE)
                .urlPattern("/admin").action(RightAction.READ)
                .source("BUILT_IN").builtIn(true).build();
        manageUsers = Right.builder()
                .id("r3").name("manage_users").type(RightType.FEATURE)
                .source("BUILT_IN").builtIn(true).build();

        authorizationService = new AuthorizationService(userRepository, roleRepository, rightRepository);
    }

    @Test
    void effectiveRightsReturnsUnionOfRoleAndIndividualRights() {
        User user = User.builder()
                .id("u1").username("user1").roleId("role1")
                .individualRightIds(Set.of("r3"))
                .createdAt(Instant.now()).build();
        Role role = Role.builder()
                .id("role1").name("User").rightIds(Set.of("r1")).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(roleRepository.findById("role1")).thenReturn(Optional.of(role));
        when(rightRepository.findById("r1")).thenReturn(Optional.of(readProfile));
        when(rightRepository.findById("r3")).thenReturn(Optional.of(manageUsers));

        List<Right> rights = authorizationService.getEffectiveRights("u1");

        assertEquals(2, rights.size());
    }

    @Test
    void adminRoleReturnsAllRights() {
        User admin = User.builder()
                .id("u1").username("admin").roleId("admin-role")
                .individualRightIds(Set.of())
                .createdAt(Instant.now()).build();
        Role adminRole = Role.builder()
                .id("admin-role").name("Admin").rightIds(Set.of("r1", "r2", "r3"))
                .builtIn(true).immutable(true).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));
        when(roleRepository.findById("admin-role")).thenReturn(Optional.of(adminRole));
        when(rightRepository.findAll()).thenReturn(List.of(readProfile, readAdmin, manageUsers));

        List<Right> rights = authorizationService.getEffectiveRights("u1");

        assertEquals(3, rights.size());
    }

    @Test
    void hasRightReturnsTrueWhenUserHasRight() {
        User user = User.builder()
                .id("u1").username("user1").roleId("role1")
                .individualRightIds(Set.of())
                .createdAt(Instant.now()).build();
        Role role = Role.builder()
                .id("role1").name("User").rightIds(Set.of("r1")).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(roleRepository.findById("role1")).thenReturn(Optional.of(role));
        when(rightRepository.findById("r1")).thenReturn(Optional.of(readProfile));

        assertTrue(authorizationService.hasRight("u1", "read:/profile"));
    }

    @Test
    void hasRightReturnsFalseWhenUserLacksRight() {
        User user = User.builder()
                .id("u1").username("user1").roleId("role1")
                .individualRightIds(Set.of())
                .createdAt(Instant.now()).build();
        Role role = Role.builder()
                .id("role1").name("User").rightIds(Set.of("r1")).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(roleRepository.findById("role1")).thenReturn(Optional.of(role));
        when(rightRepository.findById("r1")).thenReturn(Optional.of(readProfile));

        assertFalse(authorizationService.hasRight("u1", "read:/admin"));
    }
}
