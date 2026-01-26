package me.domainService.server.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TaskByEnvQueueAspect {

    @Around("execution(* me.domainService.application.service.EnvTaskService.runTask(..))")
    public void runTaskAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Aspect triggered before running task in EnvTaskService.runTask");

    }

}
