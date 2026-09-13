package org.example.service.impl;

import org.example.entity.Reservation;
import org.example.event.DataChangeEventBus;
import org.example.repository.ReservationRepository;
import org.example.repository.impl.ReservationRepositoryImpl;
import org.example.service.ReservationService;

import java.util.List;

public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository repo;

    public ReservationServiceImpl() {
        this.repo = new ReservationRepositoryImpl();
    }

    @Override
    public void create(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation không được null");
        }
        repo.save(reservation);
        DataChangeEventBus.notifyChange();
    }

    @Override
    public void update(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation không được null");
        }
        repo.update(reservation);
        DataChangeEventBus.notifyChange();
    }

    @Override
    public void cancel(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }
        Reservation reservation = repo.findById(id);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            repo.update(reservation);
            DataChangeEventBus.notifyChange();
        }
    }

    @Override
    public List<Reservation> getAll() {
        return repo.findAll();
    }

    @Override
    public Reservation getById(int id) {
        if (id <= 0) {
            return null;
        }
        return repo.findById(id);
    }

    @Override
    public List<Reservation> getByTableNumber(int tableNumber) {
        return repo.findByTableNumber(tableNumber);
    }

    @Override
    public List<Reservation> getByCustomerName(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            return List.of();
        }
        return repo.findByCustomerName(customerName);
    }

    @Override
    public List<Reservation> getByStatus(String status) {
        if (status == null || status.isBlank()) {
            return List.of();
        }
        return repo.findByStatus(status);
    }
}
