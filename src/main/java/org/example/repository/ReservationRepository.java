package org.example.repository;

import org.example.entity.Reservation;
import java.util.List;

public interface ReservationRepository {
    void save(Reservation reservation);
    void update(Reservation reservation);
    void deleteById(int id);
    Reservation findById(int id);
    List<Reservation> findAll();
    List<Reservation> findByTableNumber(int tableNumber);
    List<Reservation> findByCustomerName(String customerName);
    List<Reservation> findByStatus(String status);
}