package org.example.entity;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int customerId;
    private double totalAmount;
    private LocalDateTime createdTime;



    public Order(int customerId, double totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }

    public Order() {

    }

    public int getCustomerId() {
        return customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setId(int id) {
    }

    public void setCustomerId(int customerId) {
    }

    public void setTotalAmount(double totalAmount) {
    }
}
