package org.example.entity;

import java.time.LocalDateTime;

public class Order {

    private int id;
    private Integer customerId;
    private String customerName;

    private int totalAmount;
    private LocalDateTime createdTime;

    public Order() {}

    public Order(Integer customerId, int totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }

    // ===== GETTER & SETTER =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
