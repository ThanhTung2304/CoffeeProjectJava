package org.example.service.impl;

import org.example.entity.Voucher;
import org.example.repository.VoucherRepository;
import org.example.repository.impl.VoucherRepositoryImpl;
import org.example.service.VoucherService;

import java.time.LocalDate;
import java.util.List;

public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository repo = new VoucherRepositoryImpl();

    @Override
    public List<Voucher> search(String keyword, String status) {
        List<Voucher> list = repo.findAll(keyword, status);
        LocalDate today = LocalDate.now();

        boolean hasChanges = false;
        for (Voucher v : list) {
            if (today.isAfter(v.getEndDate()) && !"Hết hạn".equals(v.getStatus())) {
                v.setStatus("Hết hạn");
                hasChanges = true;
            }
        }

        if (hasChanges) {
            for (Voucher v : list) {
                if ("Hết hạn".equals(v.getStatus())) {
                    repo.update(v);
                }
            }
        }

        return list;
    }

    @Override
    public Voucher findByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return repo.findByCode(code);
    }

    @Override
    public void add(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher không được null");
        }

        if (voucher.getCode() == null || voucher.getCode().isBlank()) {
            throw new IllegalArgumentException("Mã voucher không được trống");
        }

        if (voucher.getStartDate() == null || voucher.getEndDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được null");
        }

        if (voucher.getEndDate().isBefore(voucher.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (repo.findByCode(voucher.getCode()) != null) {
            throw new RuntimeException("Mã Voucher này đã tồn tại trên hệ thống!");
        }
        repo.save(voucher);
    }

    @Override
    public void update(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher không được null");
        }
        repo.update(voucher);
    }

    @Override
    public void delete(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID voucher không hợp lệ");
        }
        repo.delete(id);
    }
}
