package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.LeaveResponse;
import com.dinoryn.operion.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public LeaveResponse toResponse(LeaveRequest leaveRequest) {

        LeaveResponse response = new LeaveResponse();

        response.setId(leaveRequest.getId());
        response.setLeaveType(leaveRequest.getLeaveType());
        response.setStartDate(leaveRequest.getStartDate());
        response.setEndDate(leaveRequest.getEndDate());
        response.setReason(leaveRequest.getReason());
        response.setStatus(leaveRequest.getStatus());
        response.setCreatedDate(leaveRequest.getCreatedDate());
        response.setCreatedAt(leaveRequest.getCreatedAt());
        response.setUpdatedAt(leaveRequest.getUpdatedAt());

        if (leaveRequest.getEmployee() != null) {

            response.setEmployeeId(
                    leaveRequest.getEmployee().getId()
            );

            response.setEmployeeName(
                    leaveRequest.getEmployee().getFirstName()
                            + " "
                            + leaveRequest.getEmployee().getLastName()
            );
        }

        if (leaveRequest.getApprovedBy() != null) {

            response.setApprovedById(
                    leaveRequest.getApprovedBy().getId()
            );

            response.setApprovedByName(
                    leaveRequest.getApprovedBy().getFirstName()
                            + " "
                            + leaveRequest.getApprovedBy().getLastName()
            );
        }

        return response;
    }
}