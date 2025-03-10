//package com.example.demo.controller;
//
//import com.example.demo.dto.OrderResponse;
//import com.example.demo.dto.ServiceStatusResponse;
//import com.example.demo.service.OrderService;
//import org.springframework.web.bind.annotation.*;
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//public class OrderAsyncController {
//
//    private final OrderService orderService;
//
//    public OrderAsyncController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    // 异步接口：GET /async/user/{userId}/order/{orderId}
//    @GetMapping("/async/user/{userId}/order/{orderId}")
//    public CompletableFuture<ServiceStatusResponse<OrderResponse>> getUserOrderAsync(
//            @PathVariable Long userId,
//            @PathVariable Long orderId) {
//        return orderService.getUserOrderAsync(userId, orderId);
//    }
//}
