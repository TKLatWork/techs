package com.techs.app.config;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.entity.Role;
import com.techs.domain.userauth.entity.User;
import com.techs.domain.userauth.repository.RightRepository;
import com.techs.domain.userauth.repository.RoleRepository;
import com.techs.domain.userauth.repository.UserRepository;
import com.techs.domain.userauth.service.PasswordEncoder;
import com.techs.domain.userauth.valueobject.RightAction;
import com.techs.domain.userauth.valueobject.RightType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      RightRepository rightRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findAll().isEmpty()) {
            seedData();
        }
    }

    private void seedData() {
        Right readProfile = createRight("read:/profile", RightType.BASE, "/profile", RightAction.READ, "Read access to profile", true, null);
        Right readAdmin = createRight("read:/admin", RightType.BASE, "/admin", RightAction.READ, "Read access to admin pages", true, null);
        Right writeAdmin = createRight("write:/admin", RightType.BASE, "/admin", RightAction.WRITE, "Write access to admin pages", true, null);
        Right readPermissions = createRight("read:/permissions", RightType.BASE, "/permissions", RightAction.READ, "Read access to permissions", true, null);
        Right manageUsers = createRight("manage_users", RightType.FEATURE, null, null, "Can manage user accounts and roles", true, null);
        Right managePermissions = createRight("manage_permissions", RightType.FEATURE, null, null, "Can access permission management page", true, null);

        Set<String> adminRightIds = new HashSet<>();
        adminRightIds.add(readProfile.getId());
        adminRightIds.add(readAdmin.getId());
        adminRightIds.add(writeAdmin.getId());
        adminRightIds.add(readPermissions.getId());
        adminRightIds.add(manageUsers.getId());
        adminRightIds.add(managePermissions.getId());

        Set<String> userRightIds = new HashSet<>();
        userRightIds.add(readProfile.getId());

        Role adminRole = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("Admin")
                .description("Full system access")
                .rightIds(adminRightIds)
                .builtIn(true)
                .immutable(true)
                .build();

        Role userRole = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("User")
                .description("Standard access")
                .rightIds(new HashSet<>(userRightIds))
                .builtIn(true)
                .immutable(false)
                .build();

        Role visitorRole = Role.builder()
                .id(UUID.randomUUID().toString())
                .name("Visitor")
                .description("Limited access")
                .rightIds(new HashSet<>(userRightIds))
                .builtIn(true)
                .immutable(false)
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        roleRepository.save(visitorRole);

        User admin = User.builder()
                .id(UUID.randomUUID().toString())
                .username("admin")
                .displayName("Administrator")
                .passwordHash(passwordEncoder.encode("123"))
                .roleId(adminRole.getId())
                .createdAt(Instant.now())
                .build();
        userRepository.save(admin);
    }

    private Right createRight(String name, RightType type, String urlPattern, RightAction action,
                               String description, boolean builtIn, String featureId) {
        Right right = Right.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .type(type)
                .urlPattern(urlPattern)
                .action(action)
                .description(description)
                .source(builtIn ? "BUILT_IN" : "FEATURE_REGISTERED")
                .featureId(featureId)
                .builtIn(builtIn)
                .build();
        rightRepository.save(right);
        return right;
    }
}
