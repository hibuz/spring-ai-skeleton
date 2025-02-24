package com.hibuz.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Map;

@Slf4j
@Aspect
@Component
public class ApiLoggingAop {

    @Pointcut("execution(* com.hibuz.ai.controller..*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();
        Object result = joinPoint.proceed();
        watch.stop();
        try {
            if (result instanceof Map) {
                ((Map) result).put("stopWatch", (watch.getTotalTimeSeconds() + " seconds"));
            } else {
                log.info("stopWatch : {} seconds", watch.getTotalTimeSeconds());
            }
        } catch (Exception e) {
            log.error("stopWatch : {} seconds, Error: {}", watch.getTotalTimeSeconds(), e.getMessage());
        }
        return result;
    }
}
