package com.techs.app;

import com.techs.domain.model.ProjectInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainDependencyTest {

    @Test
    void appCompilesAgainstDomainModule() {
        ProjectInfo info = new ProjectInfo("test", "Test Project");

        assertNotNull(info);
        assertEquals("test", info.projectId());
        assertEquals("Test Project", info.name());
    }
}
