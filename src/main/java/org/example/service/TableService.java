package org.example.service;

import org.example.entity.TableSeat;
import org.example.repository.TableRepository;
import org.example.repository.impl.TableRepositoryImpl;

import java.util.List;

public class TableService {
    private final TableRepository repository = new TableRepositoryImpl();

    public List<TableSeat> getAllTables() {
        return repository.findAll();
    }

    public void addTable(TableSeat table) {
        repository.add(table);
    }

    public void updateTable(TableSeat table) {
        repository.update(table);
    }

    public void deleteTable(int tableNumber) {
        repository.delete(tableNumber);
    }
}