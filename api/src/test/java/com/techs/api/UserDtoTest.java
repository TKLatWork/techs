package com.techs.api;

import com.techs.api.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesToJson() throws Exception {
        UserDto dto = new UserDto("1", "Alice", "alice@example.com");

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"id\":\"1\""));
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"email\":\"alice@example.com\""));
    }

    @Test
    void deserializesFromJson() throws Exception {
        String json = """
            {"id":"2","name":"Bob","email":"bob@example.com"}
            """;

        UserDto dto = mapper.readValue(json, UserDto.class);

        assertEquals("2", dto.id());
        assertEquals("Bob", dto.name());
        assertEquals("bob@example.com", dto.email());
    }
}
