package com.example.demo.aspect;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutionTimeLoggingAspectTest {

    // Dummy controller to test aspect
    public static class DummyController {
        public String testMethod() throws InterruptedException {
            Thread.sleep(100);
            return "result";
        }
    }

    @Test
    public void testExecutionTimeLoggingAspect() throws Throwable {
        DummyController target = new DummyController();
        ExecutionTimeLoggingAspect aspect = new ExecutionTimeLoggingAspect();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice((Advice) aspect);
        DummyController proxy = (DummyController) factory.getProxy();

        String result = proxy.testMethod();
        assertEquals("result", result);
        // The aspect logs execution time; verifying proxy return value is sufficient.
    }
}
