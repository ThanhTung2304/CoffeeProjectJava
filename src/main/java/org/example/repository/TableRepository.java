package org.example.repository;

import org.example.entity.TableSeat;
import java.util.List;

public interface TableRepository {
    List<TableSeat> findAll();
    void add(TableSeat table);
    void update(TableSeat table);
    void delete(int tableNumber);
}