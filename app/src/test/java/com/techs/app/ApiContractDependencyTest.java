package com.techs.app;

import com.techs.api.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that app module correctly depends on API contract module.
 * @req FR-004 — Verify app depends on API (web → api → domain ← app)
 */
class ApiContractDependencyTest {

    @Test
    void appCompilesAgainstApiContract() throws Exception {
        UserDto dto = new UserDto("1", "Test", "test@example.com");
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(dto);
        UserDto deserialized = mapper.readValue(json, UserDto.class);

        assertEquals(dto, deserialized);
    }
}
