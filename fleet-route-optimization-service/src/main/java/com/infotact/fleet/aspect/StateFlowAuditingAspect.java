package com.infotact.fleet.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class StateFlowAuditingAspect {

    private static final Logger log = LoggerFactory.getLogger(StateFlowAuditingAspect.class);

    /**
     * 🎯 METRIC POINTCUT:
     * Targets any method execution inside service implementations that handles status or workflow state updates.
     */
    @Pointcut("execution(* com.infotact.fleet.service.*Workflow*.*(..)) || execution(* com.infotact.fleet.service.*State*.*(..))")
    public void stateModificationMethods() {}

    /**
     * Intercepts execution tracks to profile performance runtimes and log tracking states cleanly.
     */
    @Around("stateModificationMethods()")
    public Object profileStateFlowTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.info("📊 [STATE AUDIT START] Triggering transition tracker: {}.{}() | Payload Parameters: {}", 
                className, methodName, arguments);

        long startTime = System.currentTimeMillis();
        
        try {
            // Proceed with the actual service logic execution
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("✅ [STATE AUDIT SUCCESS] Completed transition tracker: {}.{}() | Performance Metric: {}ms", 
                    className, methodName, executionTime);
            
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ [STATE AUDIT FAILURE] Aborted transition tracker: {}.{}() | Error Msg: {} | Elapsed Time: {}ms", 
                    className, methodName, throwable.getMessage(), executionTime);
            throw throwable;
        }
    }
}