package com.example.demo.controller;

import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.ServiceStatusResponse;
import com.example.demo.service.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
@RequestMapping("/user/{userId}/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 同步接口保持不变
    @GetMapping("/{orderId}")
    public ServiceStatusResponse<OrderResponse> getUserOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.getUserOrder(userId, orderId);
    }

    // 异步接口使用 WebAsyncTask
    @GetMapping("/async/{orderId}")
    public WebAsyncTask<ServiceStatusResponse<OrderResponse>> getUserOrderAsync(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        // Optional: 设置超时和超时回调
        WebAsyncTask<ServiceStatusResponse<OrderResponse>> webAsyncTask =
                new WebAsyncTask<>(5000, () -> {
                    // 在此记录安全上下文
                    System.out.println("[Async Controller] In WebAsyncTask: " +
                            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
                    return orderService.getUserOrder(userId, orderId);
                });

        webAsyncTask.onTimeout(() -> {
            System.err.println("[Async Controller] Timeout occurred");
            return null;
        });
        webAsyncTask.onError(() -> {
            System.err.println("[Async Controller] Error occurred");
            return null;
        });

        return webAsyncTask;
    }
}
