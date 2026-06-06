package com.techs.domain;

import com.techs.domain.model.ProjectInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
