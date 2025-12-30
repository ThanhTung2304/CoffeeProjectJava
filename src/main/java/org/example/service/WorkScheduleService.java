package org.example.service;

import org.example.entity.WorkSchedule;
import java.util.List;

public interface WorkScheduleService {
    List<WorkSchedule> findAll();
    void delete(int empId, int shiftId, String workDate);
}
