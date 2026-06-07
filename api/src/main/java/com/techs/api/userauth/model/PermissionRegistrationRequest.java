package com.techs.api.userauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRegistrationRequest {
    private String featureId;
    private List<PermissionEntry> permissions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionEntry {
        private String name;
        private String type;
        private String urlPattern;
        private String action;
        private String description;
    }
}
