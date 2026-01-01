package org.example.service.impl;

import org.example.entity.TableSeat;
import org.example.repository.TableRepository;
import org.example.service.TableService;

import java.util.List;

public class TableServiceImpl extends TableService {
    private final TableRepository repo;

    public TableServiceImpl(TableRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TableSeat> getAllTables() {
        return repo.findAll();
    }

    @Override
    public void addTable(TableSeat table) {
        repo.add(table);
    }

    @Override
    public void updateTable(TableSeat table) {
        repo.update(table);
    }

    @Override
    public void deleteTable(int tableNumber) {
        repo.delete(tableNumber);
    }
}