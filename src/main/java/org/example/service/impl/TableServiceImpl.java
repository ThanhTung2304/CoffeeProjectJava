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
        repository.add(table);
    }

    @Override
    public void updateTable(TableSeat table) {
        repository.update(table);
    }

    @Override
    public void deleteTable(int tableNumber) {
        repository.delete(tableNumber);
    }
}