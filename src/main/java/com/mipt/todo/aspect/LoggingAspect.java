package com.mipt.todo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect для логирования входа и выхода из методов сервисного слоя.
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(* com.mipt.todo.service.*.*(..))")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();
    if (log.isDebugEnabled()) {
      log.debug(">>> Entering: {} with args: {}", methodName, Arrays.toString(args));
    }
    Object result = joinPoint.proceed();
    if (log.isDebugEnabled()) {
      if (result == null) {
        log.debug("<<< Exiting:  {} with result: void", methodName);
      } else {
        log.debug("<<< Exiting:  {} with result: {}", methodName, result);
      }
    }
    return result;
  }
}