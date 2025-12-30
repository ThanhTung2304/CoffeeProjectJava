package org.example.controller;

import org.example.entity.WorkSchedule;
import org.example.service.WorkScheduleService;
import org.example.service.impl.WorkScheduleServiceImpl;

import java.util.List;

public class WorkScheduleController {

    private final WorkScheduleService service = new WorkScheduleServiceImpl();

    public List<WorkSchedule> getAll() {
        return service.findAll();
    }

    public void delete(int empId, int shiftId, String workDate) {
        service.delete(empId, shiftId, workDate);
    }
}
