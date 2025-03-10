package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private LocalDateTime time;
    private Double totalPrice;
    private List<OrderItemResponse> orderItemResponseList;
    private UserResponse userResponse;

    public OrderResponse() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<OrderItemResponse> getOrderItemResponseList() { return orderItemResponseList; }
    public void setOrderItemResponseList(List<OrderItemResponse> orderItemResponseList) { this.orderItemResponseList = orderItemResponseList; }

    public UserResponse getUserResponse() { return userResponse; }
    public void setUserResponse(UserResponse userResponse) { this.userResponse = userResponse; }
}
