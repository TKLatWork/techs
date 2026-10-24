package com.example.workflow.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.workflow")
public class WorkflowWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowWebApplication.class, args);
    }
}