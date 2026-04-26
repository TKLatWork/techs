package me.vibe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"me.vibe"})
public class VibeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibeApiApplication.class, args);
    }
}