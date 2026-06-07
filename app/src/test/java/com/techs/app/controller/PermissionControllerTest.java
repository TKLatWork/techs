package com.techs.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        String body = """
                {"username":"admin","password":"123"}
                """;
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = response.get("token").asText();
    }

    @Test
    void listRolesReturnsAllRoles() throws Exception {
        mockMvc.perform(get("/api/permissions/roles")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[?(@.name == 'Admin')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'User')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Visitor')]").exists());
    }

    @Test
    void createCustomRoleReturns201() throws Exception {
        String body = """
                {"name":"Editor","description":"Can edit content","rightIds":[]}
                """;

        mockMvc.perform(post("/api/permissions/roles")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Editor"))
                .andExpect(jsonPath("$.builtIn").value(false))
                .andExpect(jsonPath("$.immutable").value(false));
    }

    @Test
    void createRoleWithDuplicateNameReturns409() throws Exception {
        String body = """
                {"name":"Admin","description":"Duplicate","rightIds":[]}
                """;

        mockMvc.perform(post("/api/permissions/roles")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ROLE_NAME_TAKEN"));
    }

    @Test
    void updateImmutableAdminRoleReturns403() throws Exception {
        MvcResult rolesResult = mockMvc.perform(get("/api/permissions/roles")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode roles = objectMapper.readTree(rolesResult.getResponse().getContentAsString());
        String adminRoleId = null;
        for (JsonNode role : roles) {
            if ("Admin".equals(role.get("name").asText())) {
                adminRoleId = role.get("id").asText();
                break;
            }
        }

        String body = """
                {"name":"Admin","description":"Modified","rightIds":[]}
                """;

        mockMvc.perform(put("/api/permissions/roles/" + adminRoleId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ROLE_IMMUTABLE"));
    }

    @Test
    void deleteBuiltInRoleReturns403() throws Exception {
        MvcResult rolesResult = mockMvc.perform(get("/api/permissions/roles")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode roles = objectMapper.readTree(rolesResult.getResponse().getContentAsString());
        String visitorRoleId = null;
        for (JsonNode role : roles) {
            if ("Visitor".equals(role.get("name").asText())) {
                visitorRoleId = role.get("id").asText();
                break;
            }
        }

        mockMvc.perform(delete("/api/permissions/roles/" + visitorRoleId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ROLE_BUILT_IN"));
    }

    @Test
    void listRightsReturnsAllRights() throws Exception {
        mockMvc.perform(get("/api/permissions/rights")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))));
    }

    @Test
    void deleteBuiltInRightReturns403() throws Exception {
        MvcResult rightsResult = mockMvc.perform(get("/api/permissions/rights")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rights = objectMapper.readTree(rightsResult.getResponse().getContentAsString());
        String builtInRightId = null;
        for (JsonNode right : rights) {
            if ("BUILT_IN".equals(right.get("source").asText())) {
                builtInRightId = right.get("id").asText();
                break;
            }
        }

        mockMvc.perform(delete("/api/permissions/rights/" + builtInRightId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("RIGHT_BUILT_IN"));
    }

    @Test
    void listUsersReturnsAllUsers() throws Exception {
        mockMvc.perform(get("/api/permissions/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.username == 'admin')]").exists());
    }

    @Test
    void listFeaturesReturnsEmptyWhenNoFeaturesRegistered() throws Exception {
        mockMvc.perform(get("/api/permissions/features")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void registerPermissionsCreatesNewRights() throws Exception {
        String body = """
                {
                    "featureId": "test-feature",
                    "permissions": [
                        {"name": "test_right", "type": "FEATURE", "description": "Test right"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/permissions/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registered").value(1))
                .andExpect(jsonPath("$.updated").value(0))
                .andExpect(jsonPath("$.rights", hasSize(1)));
    }

    @Test
    void registerPermissionsIdempotentUpsert() throws Exception {
        String body = """
                {
                    "featureId": "idempotent-feature",
                    "permissions": [
                        {"name": "idempotent_right", "type": "FEATURE", "description": "First"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/permissions/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registered").value(1));

        String body2 = """
                {
                    "featureId": "idempotent-feature",
                    "permissions": [
                        {"name": "idempotent_right", "type": "FEATURE", "description": "Updated"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/permissions/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registered").value(0))
                .andExpect(jsonPath("$.updated").value(1));
    }
}
