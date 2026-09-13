package org.example.service.impl;

import org.example.entity.Shift;
import org.example.repository.ShiftRepository;
import org.example.repository.impl.ShiftRepositoryImpl;
import org.example.service.ShiftService;

import java.util.List;

public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository repo = new ShiftRepositoryImpl();

    @Override
    public List<Shift> findAll() {
        return repo.findAll();
    }

    @Override
    public void create(Shift s) {
        if (s == null) {
            throw new IllegalArgumentException("Ca làm không được null");
        }

        if (s.getName() == null || s.getName().isBlank()) {
            throw new IllegalArgumentException("Tên ca không được trống");
        }

        if (s.getStartTime() == null || s.getEndTime() == null) {
            throw new IllegalArgumentException("Giờ bắt đầu và kết thúc không được null");
        }

        if (s.getEndTime().isBefore(s.getStartTime())) {
            throw new IllegalArgumentException("Giờ kết thúc phải > giờ bắt đầu");
        }

        repo.save(s);
    }

    @Override
    public void update(Shift s) {
        if (s == null) {
            throw new IllegalArgumentException("Ca làm không được null");
        }

        if (s.getId() <= 0) {
            throw new IllegalArgumentException("ID ca làm không hợp lệ");
        }

        repo.update(s);
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID ca làm không hợp lệ");
        }
        repo.deleteById(id);
    }
}
