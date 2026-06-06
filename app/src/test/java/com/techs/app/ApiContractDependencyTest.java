package com.techs.app;

import com.techs.api.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
