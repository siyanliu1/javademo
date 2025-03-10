package com.example.demo.dto;

public class ServiceStatusResponse<T> {
    private boolean success;
    private T orderResponse;

    public ServiceStatusResponse() {}

    public ServiceStatusResponse(boolean success, T orderResponse) {
        this.success = success;
        this.orderResponse = orderResponse;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getOrderResponse() { return orderResponse; }
    public void setOrderResponse(T orderResponse) { this.orderResponse = orderResponse; }
}
