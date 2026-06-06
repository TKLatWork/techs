package com.techs.app;

import com.techs.app.service.ProjectService;
import com.techs.domain.model.ProjectInfo;
import com.techs.api.model.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests app module service layer that uses both domain and API dependencies.
 * @req FR-001 — Verify app module exists as a distinct top-level directory
 * @req FR-002 — Verify app module has source code directory
 * @req FR-003 — Verify app module has its own configuration
 * @req FR-004 — Verify app depends on both domain and API modules
 * @req FR-006 — Verify app module has entry point awareness
 * @req SC-004 — App module can be independently tested
 */
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
