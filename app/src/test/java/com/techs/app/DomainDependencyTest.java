package com.techs.app;

import com.techs.domain.model.ProjectInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that app module correctly depends on domain module.
 * @req FR-004 — Verify app depends on domain (web → api → domain ← app)
 * @req SC-003 — Shared business logic in domain is consumed by app
 */
class DomainDependencyTest {

    @Test
    void appCompilesAgainstDomainModule() {
        ProjectInfo info = new ProjectInfo("test", "Test Project");

        assertNotNull(info);
        assertEquals("test", info.projectId());
        assertEquals("Test Project", info.name());
    }
}
