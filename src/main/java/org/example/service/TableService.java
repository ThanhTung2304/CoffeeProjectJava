package org.example.service;

import org.example.entity.TableSeat;
import java.util.List;

public interface TableService {
    List<TableSeat> getAllTables();
    void addTable(TableSeat table);
    void updateTable(TableSeat table);
    void deleteTable(int tableNumber);
}