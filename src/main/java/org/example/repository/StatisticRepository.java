package org.example.repository;

public interface StatisticRepository {
    int countCustomers();
    int countEmployees();
    double getMonthlyRevenue();
    int countMonthlyReservations();
    String getBestSellingProduct();
    int getTotalStock();
    int getTotalExported();
}
