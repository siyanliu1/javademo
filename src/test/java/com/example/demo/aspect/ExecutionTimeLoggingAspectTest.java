package com.example.demo.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutionTimeLoggingAspectTest {

    public static class DummyController {
        public String testMethod() throws InterruptedException {
            Thread.sleep(100);
            return "result";
        }
    }

    @Test
    public void testExecutionTimeLoggingAspect() throws Throwable {
        DummyController target = new DummyController();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(new ExecutionTimeLoggingAspect());
        DummyController proxy = factory.getProxy();
        String result = proxy.testMethod();
        assertEquals("result", result);
    }
}
