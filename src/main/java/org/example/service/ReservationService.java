package org.example.service;

import org.example.entity.Reservation;
import java.util.List;

public interface ReservationService {
    void create(Reservation reservation);
    void update(Reservation reservation);
    void cancel(int id);
    List<Reservation> getAll();
    Reservation getById(int id);
    List<Reservation> getByTableNumber(int tableNumber);
    List<Reservation> getByCustomerName(String customerName);
    List<Reservation> getByStatus(String status);
}