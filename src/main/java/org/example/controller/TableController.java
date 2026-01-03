package org.example.controller;

import org.example.entity.TableSeat;
import org.example.service.TableService;
import org.example.service.impl.TableServiceImpl;

import java.util.List;

public class TableController {
    private final TableService service = new TableServiceImpl();

    public List<TableSeat> getAllTables() {
        return service.getAllTables();
    }

    public void addTable(TableSeat table) {
        service.addTable(table);
    }

    public void updateTable(TableSeat table) {
        service.updateTable(table);
    }

    public void deleteTable(int tableNumber) {
        service.deleteTable(tableNumber);
    }
}