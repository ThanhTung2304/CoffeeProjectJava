package org.example.repository;

public interface StatisticRepository {

    int countCustomers();
    int countEmployees();

//    int countMonthlyReservedTables();

    int getExportedTotal();

    int countMonthlyReservations();
    double getMonthlyRevenue();
    int getTotalInventory();
    int getTotalExported();
}
