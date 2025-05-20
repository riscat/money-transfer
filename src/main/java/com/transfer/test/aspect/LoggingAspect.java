package com.transfer.test.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.transfer.test.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[LOG BEFORE] Method: " + joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "execution(* com.transfer.test.service.*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("[LOG AFTER] Method: " + joinPoint.getSignature() + " completed.");
    }
}