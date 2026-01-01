package org.example.repository;

public interface StatisticRepository {
    int countCustomers();

    int countEmployees();

    double getMonthlyRevenue();

    int countMonthlyReservations();

    int getTotalInventory();

    int getTotalExported();

    int countMonthlyReservedTables();

}
