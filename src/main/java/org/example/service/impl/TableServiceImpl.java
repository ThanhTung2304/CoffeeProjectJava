package org.example.service.impl;

import org.example.entity.TableSeat;
import org.example.repository.TableRepository;
import org.example.repository.impl.TableRepositoryImpl;
import org.example.service.TableService;

import java.util.List;

public class TableServiceImpl implements TableService {
    private final TableRepository repository = new TableRepositoryImpl();

    @Override
    public List<TableSeat> getAllTables() {
        return repository.findAll();
    }

    @Override
    public void addTable(TableSeat table) {
        if (table == null) {
            throw new IllegalArgumentException("Bàn không được null");
        }
        if (table.getName() == null || table.getName().isBlank()) {
            throw new IllegalArgumentException("Tên bàn không được trống");
        }
        if (table.getCapacity() <= 0) {
            throw new IllegalArgumentException("Sức chứa phải > 0");
        }
        repository.add(table);
    }

    @Override
    public void updateTable(TableSeat table) {
        if (table == null) {
            throw new IllegalArgumentException("Bàn không được null");
        }
        if (table.getTableNumber() <= 0) {
            throw new IllegalArgumentException("Số bàn không hợp lệ");
        }
        repository.update(table);
    }

    @Override
    public void deleteTable(int tableNumber) {
        if (tableNumber <= 0) {
            throw new IllegalArgumentException("Số bàn không hợp lệ");
        }
        repository.delete(tableNumber);
    }
}
