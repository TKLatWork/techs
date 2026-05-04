package me.dslProject.userInfo;

import me.ai_project.app.AppApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInfoAPIIntegrationTest extends UserInfoAPITest {

    @LocalServerPort
    private int port;

    @Override
    protected int getPort() {
        return port;
    }
}
