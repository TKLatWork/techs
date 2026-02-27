package me.domainService.server.common.service;

import me.domainService.server.common.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    Map<String, User> envMap = Map.of(
        "ADMIN", new User(){{
            setName("ADMIN");
            setRole("User");
        }},
        "USER", new User(){{
            setName("USER");
            setRole("User");
        }}
    );

    public User get(String name){
        User user = new User();
        user.setName(name);
        user.setRole("User");
        return user;
    }

}
