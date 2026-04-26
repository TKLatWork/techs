package me.vibe.service;

import me.vibe.domain.HelloResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Sample service that creates hello responses.
 */
@Service
public class HelloService {

    /**
     * Creates a greeting response for the given name.
     *
     * @param name the name to greet (defaults to "World" if blank)
     * @return HelloResponse with message and timestamp
     */
    public HelloResponse greet(String name) {
        String displayName = StringUtils.isBlank(name) ? "World" : StringUtils.trim(name);
        String message = "Hello, " + displayName + "!";

        return HelloResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}