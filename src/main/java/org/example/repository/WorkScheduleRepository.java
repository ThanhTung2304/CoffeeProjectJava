package org.example.repository;

import org.example.entity.WorkSchedule;
import java.util.List;

public interface WorkScheduleRepository {

    List<WorkSchedule> findAll();

    void delete(int employeeId, int shiftId, String workDate);
}
