package org.example.service;

public interface StatisticService {
    int getCustomerCount();
    int getEmployeeCount();
    double getMonthlyRevenue();
    int getReservationCount();
    String getBestSellingProduct();

    int getTotalStock();
    int getTotalExported();
}
