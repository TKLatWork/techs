package me.dslProject.userInfo;

import me.dslProject.userInfo.model.LoginRequest;
import me.dslProject.userInfo.model.LoginResponse;
import me.dslProject.userInfo.model.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
public interface UserInfoAPI {

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);

    @GetMapping("/info")
    ResponseEntity<UserInfo> getUserInfo(@RequestHeader("Authorization") String authHeader);
}
