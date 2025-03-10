package com.example.demo.controller;

import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.ServiceStatusResponse;
import com.example.demo.service.OrderService;
import com.example.demo.aspect.LogExecutionTime;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user/{userId}/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Synchronous endpoint with AOP logging execution time
    @GetMapping("/{orderId}")
    @LogExecutionTime
    public ServiceStatusResponse<OrderResponse> getUserOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.getUserOrder(userId, orderId);
    }

    // Asynchronous endpoint using @Async and CompletableFuture
    @GetMapping("/async/{orderId}")
    public CompletableFuture<ServiceStatusResponse<OrderResponse>> getUserOrderAsync(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.getUserOrderAsync(userId, orderId);
    }
}
