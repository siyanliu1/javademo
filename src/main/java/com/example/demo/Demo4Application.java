package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.example.auth"})
public class Demo4Application {
    static {
        // 设置可继承模式
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo4Application.class, args);
    }
}
