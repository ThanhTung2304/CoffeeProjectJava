package org.example.controller;

import org.example.entity.Reservation;
import org.example.service.ReservationService;
import org.example.service.impl.ReservationServiceImpl;

import java.util.List;

public class ReservationController {
    private final ReservationService service = new ReservationServiceImpl();

    // ===== Thêm mới =====
    public void addReservation(Reservation reservation) {
        service.create(reservation);
    }

    // ===== Cập nhật =====
    public void updateReservation(Reservation reservation) {
        service.update(reservation);
    }

    // ===== Xóa =====
    public void deleteReservation(int id) {
        service.cancel(id); // dùng cancel như delete
    }

    // ===== Lấy tất cả =====
    public List<Reservation> getAllReservations() {
        return service.getAll();
    }

    // ===== Lấy theo ID =====
    public Reservation getReservationById(int id) {
        return service.getById(id);
    }
}