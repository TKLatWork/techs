package me.domainService.server.repoImpls;

import me.domainService.application.entity.User;
import me.domainService.application.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserRepoImpl implements UserRepo {

    final Map<String, User> envMap = Map.of(
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
        return envMap.get(name);
    }

}
