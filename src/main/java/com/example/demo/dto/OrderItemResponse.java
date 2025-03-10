package com.example.demo.dto;

public class OrderItemResponse {
    private String itemName;
    private Integer quantity;

    public OrderItemResponse() {}

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
