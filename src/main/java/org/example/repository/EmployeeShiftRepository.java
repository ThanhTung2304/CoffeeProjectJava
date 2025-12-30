package org.example.repository;

import java.time.LocalDate;

public interface EmployeeShiftRepository {

    void assign(int empId, int shiftId, LocalDate date);

    boolean exists(int empId, int shiftId, LocalDate date);
}
