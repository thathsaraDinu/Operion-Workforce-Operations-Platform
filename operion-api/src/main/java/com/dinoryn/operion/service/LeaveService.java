package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.LeaveApprovalRequest;
import com.dinoryn.operion.dto.LeaveCreateRequest;
import com.dinoryn.operion.dto.LeaveResponse;
import com.dinoryn.operion.dto.LeaveUpdateRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveService {

    LeaveResponse createLeaveRequest(
            Employee employee,
            LeaveCreateRequest request
    );


    LeaveResponse updateLeaveRequest(
            Employee employee,
            Long leaveRequestId,
            LeaveUpdateRequest request
    );


    LeaveResponse approveLeaveRequest(
            Employee approvalUser,
            Long leaveRequestId,
            LeaveApprovalRequest request
    );


    LeaveResponse getLeaveRequestById(
            Employee currentUser,
            Long leaveRequestId
    );


    Page<LeaveResponse> getAllLeaveRequests(
            Pageable pageable
    );


    Page<LeaveResponse> getMyLeaveRequests(
            Employee employee,
            Pageable pageable
    );


    Page<LeaveResponse> getLeaveRequestsByStatus(
            LeaveStatus status,
            Pageable pageable
    );


    void deleteLeaveRequest(
            Employee employee,
            Long leaveRequestId
    );
}