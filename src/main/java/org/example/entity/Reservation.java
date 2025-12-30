package org.example.entity;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private String customerName;
    private int tableNumber;
    private LocalDateTime time;
    private String status;
    private String note;

    public Reservation() {}

    public Reservation(String customerName, int tableNumber, LocalDateTime time, String status, String note) {
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.time = time;
        this.status = status;
        this.note = note;
    }

    public Reservation(int id, String customerName, int tableNumber, LocalDateTime time, String status, String note) {
        this.id = id;
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.time = time;
        this.status = status;
        this.note = note;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}