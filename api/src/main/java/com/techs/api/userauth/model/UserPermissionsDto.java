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
public class UserPermissionsDto {
    private String userId;
    private String username;
    private String displayName;
    private RoleDto role;
    private List<RightDto> effectiveRights;
    private List<RightDto> individualRights;
}
