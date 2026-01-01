package org.example.service.impl;

import org.example.repository.StatisticRepository;
import org.example.repository.impl.StatisticRepositoryImpl;
import org.example.service.StatisticService;

public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository reportRepository = new StatisticRepositoryImpl();

    @Override
    public int getCustomerCount() {
        return reportRepository.countCustomers();
    }

    @Override
    public int getEmployeeCount() {
        return reportRepository.countEmployees();
    }

    @Override
    public double getMonthlyRevenue() {
        return reportRepository.getMonthlyRevenue();
    }

    @Override
    public int getReservationCount() {
        return reportRepository.countMonthlyReservations();
    }

    @Override
    public String getBestSellingProduct() {
        return reportRepository.getBestSellingProduct();
    }

    @Override
    public int getTotalStock() {
        return reportRepository.getTotalStock();
    }

    @Override
    public int getTotalExported() {
        return reportRepository.getTotalExported();
    }
}
