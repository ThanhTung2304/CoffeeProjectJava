package org.example.service.impl;

import org.example.entity.Reservation;
import org.example.repository.ReservationRepository;
import org.example.repository.impl.ReservationRepositoryImpl;
import org.example.service.ReservationService;

import java.util.List;

public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository repo;

    // Constructor mặc định: dùng RepositoryImpl với connection tự tạo
    public ReservationServiceImpl() {
        this.repo = new ReservationRepositoryImpl();
    }

    @Override
    public void create(Reservation reservation) {
        repo.save(reservation);
    }

    @Override
    public void update(Reservation reservation) {
        repo.update(reservation);
    }

    @Override
    public void cancel(int id) {
        repo.deleteById(id);
    }

    @Override
    public List<Reservation> getAll() {
        return repo.findAll();
    }

    @Override
    public Reservation getById(int id) {
        return repo.findById(id);
    }

    @Override
    public List<Reservation> getByTableNumber(int tableNumber) {
        return repo.findByTableNumber(tableNumber);
    }

    @Override
    public List<Reservation> getByCustomerName(String customerName) {
        return repo.findByCustomerName(customerName);
    }

    @Override
    public List<Reservation> getByStatus(String status) {
        return repo.findByStatus(status);
    }
}