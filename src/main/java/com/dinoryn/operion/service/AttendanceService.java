package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.AttendanceResponse;
import com.dinoryn.operion.dto.AttendanceUpdateRequest;
import com.dinoryn.operion.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AttendanceService {

    AttendanceResponse clockIn(Employee employee);

    AttendanceResponse clockOut(Employee employee);

    Page<AttendanceResponse> getMyAttendance(
            Employee employee,
            Pageable pageable
    );


    Page<AttendanceResponse> getAllAttendance(
            Pageable pageable
    );


    Page<AttendanceResponse> getEmployeeAttendance(
            Long employeeId,
            Pageable pageable
    );

    AttendanceResponse updateAttendance(
            Long attendanceId,
            AttendanceUpdateRequest request
    );
}