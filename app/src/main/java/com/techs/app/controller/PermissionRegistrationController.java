package com.techs.app.controller;

import com.techs.api.userauth.model.PermissionRegistrationRequest;
import com.techs.api.userauth.model.RightDto;
import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.service.AuthorizationServiceManagement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
public class PermissionRegistrationController {

    private final AuthorizationServiceManagement management;

    public PermissionRegistrationController(AuthorizationServiceManagement management) {
        this.management = management;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerPermissions(
            @RequestBody PermissionRegistrationRequest request) {
        List<AuthorizationServiceManagement.PermissionInput> inputs = request.getPermissions().stream()
                .map(p -> new AuthorizationServiceManagement.PermissionInput(
                        p.getName(), p.getType(), p.getUrlPattern(), p.getAction(), p.getDescription()))
                .toList();

        Map<String, Object> result = management.registerPermissions(request.getFeatureId(), inputs);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("registered", result.get("registered"));
        response.put("updated", result.get("updated"));
        @SuppressWarnings("unchecked")
        List<Right> rights = (List<Right>) result.get("rights");
        response.put("rights", rights.stream().map(this::toRightDto).toList());

        return ResponseEntity.ok(response);
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
