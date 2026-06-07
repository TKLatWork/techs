package com.techs.domain.userauth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String displayName;
    private String passwordHash;
    private String roleId;
    @Builder.Default
    private Set<String> individualRightIds = new HashSet<>();
    private Instant createdAt;
}
