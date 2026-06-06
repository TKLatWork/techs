package com.techs.domain;

import com.techs.domain.model.ProjectInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests domain module core entity behavior.
 * @req FR-001 — Verify domain module exists as a distinct top-level directory
 * @req FR-002 — Verify domain module has source code directory
 * @req FR-003 — Verify domain module has its own configuration
 * @req SC-003 — Shared business logic resides in domain module
 * @req SC-004 — Domain module can be independently tested
 */
class ProjectInfoTest {

    @Test
    void createsProjectInfoWithRequiredFields() {
        ProjectInfo info = new ProjectInfo("techs", "Techs Platform");

        assertEquals("techs", info.projectId());
        assertEquals("Techs Platform", info.name());
    }

    @Test
    void rejectsNullProjectId() {
        assertThrows(IllegalArgumentException.class, () ->
            new ProjectInfo(null, "name")
        );
    }

    @Test
    void rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () ->
            new ProjectInfo("id", "")
        );
    }
}
