package org.example.service;

import java.time.LocalDate;

public interface EmployeeShiftService {
    void assignShift(int empId, int shiftId, LocalDate date);
}
