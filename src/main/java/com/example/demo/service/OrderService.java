package com.example.demo.service;

import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.ServiceStatusResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.repository.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ServiceStatusResponse<OrderResponse> getUserOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for userId " + userId + " and orderId " + orderId));

        OrderResponse orderResponse = convertOrderToResponse(order);
        return new ServiceStatusResponse<>(true, orderResponse);
    }

    @Async
    public CompletableFuture<ServiceStatusResponse<OrderResponse>> getUserOrderAsync(Long userId, Long orderId) {
        return CompletableFuture.completedFuture(getUserOrder(userId, orderId));
    }

    private OrderResponse convertOrderToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setTime(order.getOrderTime());
        response.setTotalPrice(order.getTotalPrice());

        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream().map(oi -> {
            OrderItemResponse oir = new OrderItemResponse();
            oir.setItemName(oi.getItem().getName());
            oir.setQuantity(oi.getQuantity());
            return oir;
        }).collect(Collectors.toList());
        response.setOrderItemResponseList(orderItemResponses);

        // Build user response from the existing user and its details
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(order.getUser().getUsername());
        userResponse.setFirstname(order.getUser().getUserDetail().getFirstname());
        userResponse.setLastname(order.getUser().getUserDetail().getLastname());
        userResponse.setEmail(order.getUser().getUserDetail().getEmail());
        response.setUserResponse(userResponse);

        return response;
    }
}
