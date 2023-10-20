package ru.skypro.homework.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AdsLoggingAspect {
    @Pointcut("within(ru.skypro.homework.service.impl.*) || within(ru.skypro.homework.security.SecurityAnnotationMethods)")
    public void adsLogging() {
    }

    @After("adsLogging()")
    public void adsAfterMethod(JoinPoint jp) {
        Logger logger = LoggerFactory.getLogger(jp.getTarget().getClass());
        String methodName = jp.getSignature().getName();

        Object[] args = jp.getArgs();
        CodeSignature codeSignature = (CodeSignature) jp.getSignature();
        StringBuilder argsString = new StringBuilder();
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            argsString.append(codeSignature.getParameterNames()[argIndex]).append("=").append(args[argIndex].toString());
            if (argIndex < args.length - 1) {
                argsString.append(", ");
            }
        }

        logger.debug("Was invoked method: " + methodName + (argsString.length() == 0 ? " without arguments." : " with arguments: " + argsString));
    }

    @AfterThrowing(value = "adsLogging()", throwing = "exception")
    public void adsAfterThrowing(JoinPoint jp, Exception exception) {
        Logger logger = LoggerFactory.getLogger(jp.getTarget().getClass());
        logger.error(exception.getMessage());
    }
}
