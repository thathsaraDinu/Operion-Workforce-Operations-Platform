package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.AttendanceResponse;
import com.dinoryn.operion.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {


    public AttendanceResponse toResponse(Attendance attendance) {

        AttendanceResponse response = new AttendanceResponse();

        response.setId(attendance.getId());

        response.setDate(attendance.getDate());

        response.setClockIn(attendance.getClockIn());

        response.setClockOut(attendance.getClockOut());

        response.setStatus(attendance.getStatus());

        response.setCreatedAt(attendance.getCreatedAt());
        response.setUpdatedAt(attendance.getUpdatedAt());


        if (attendance.getEmployee() != null) {

            response.setEmployeeId(
                    attendance.getEmployee().getId()
            );

            response.setEmployeeName(
                    attendance.getEmployee().getFirstName()
                            + " "
                            + attendance.getEmployee().getLastName()
            );
        }

        return response;
    }
}