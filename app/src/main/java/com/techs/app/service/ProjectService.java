package com.techs.app.service;

import com.techs.domain.model.ProjectInfo;
import com.techs.api.model.UserDto;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    public ProjectInfo getProjectInfo() {
        return new ProjectInfo("techs", "Techs Platform");
    }

    public UserDto createSampleUser(String id, String name, String email) {
        return new UserDto(id, name, email);
    }
}
