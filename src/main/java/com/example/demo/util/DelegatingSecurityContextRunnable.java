package com.example.demo.util;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class DelegatingSecurityContextRunnable implements Runnable {
    private final Runnable delegate;
    private final SecurityContext context;

    public DelegatingSecurityContextRunnable(Runnable delegate) {
        this.delegate = delegate;
        // 捕获当前线程的SecurityContext
        this.context = SecurityContextHolder.getContext();
    }

    @Override
    public void run() {
        SecurityContext originalContext = SecurityContextHolder.getContext();
        try {
            SecurityContextHolder.setContext(context);
            delegate.run();
        } finally {
            SecurityContextHolder.setContext(originalContext);
        }
    }
}
