package org.example.entity;

import java.time.LocalDateTime;

public class Order {
<<<<<<< HEAD

    private int id;
    private Integer customerId;
    private String customerName;

    private int totalAmount;
    private LocalDateTime createdTime;

    public Order() {}

    public Order(Integer customerId, int totalAmount) {
=======
    private int id;
    private int customerId;
    private double totalAmount;
    private LocalDateTime createdTime;



    public Order(int customerId, double totalAmount) {
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> 75b2654ef090967cfaa14355968f604362be0df8
}
