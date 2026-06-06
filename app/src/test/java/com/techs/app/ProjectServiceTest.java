package com.techs.app;

import com.techs.app.service.ProjectService;
import com.techs.domain.model.ProjectInfo;
import com.techs.api.model.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Test
    void returnsProjectInfo() {
        ProjectService service = new ProjectService();

        ProjectInfo info = service.getProjectInfo();

        assertNotNull(info);
        assertEquals("techs", info.projectId());
    }

    @Test
    void createsSampleUser() {
        ProjectService service = new ProjectService();

        UserDto user = service.createSampleUser("1", "Alice", "alice@example.com");

        assertEquals("1", user.id());
        assertEquals("Alice", user.name());
        assertEquals("alice@example.com", user.email());
    }
}
