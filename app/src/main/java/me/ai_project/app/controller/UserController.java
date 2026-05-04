package me.ai_project.app.controller;

import me.dslProject.userInfo.UserInfoAPI;
import me.dslProject.userInfo.model.LoginRequest;
import me.dslProject.userInfo.model.LoginResponse;
import me.dslProject.userInfo.model.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController implements UserInfoAPI {

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password";
    private static final String MOCK_JWT_TOKEN = "mock.jwt.token." + UUID.randomUUID().toString();

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (VALID_USERNAME.equals(request.getUsername()) && VALID_PASSWORD.equals(request.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(MOCK_JWT_TOKEN));
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @Override
    @GetMapping("/info")
    public ResponseEntity<UserInfo> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        if (!isValidToken(token)) {
            return ResponseEntity.status(401).build();
        }

        UserInfo userInfo = new UserInfo("1", "admin", "ADMIN");
        return ResponseEntity.ok(userInfo);
    }

    private boolean isValidToken(String token) {
        return token != null && token.equals(MOCK_JWT_TOKEN);
    }
}
