package com.techs.domain.userauth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Role {
    private String id;
    private String name;
    private String description;
    @Builder.Default
    private Set<String> rightIds = new HashSet<>();
    private boolean builtIn;
    private boolean immutable;
}
