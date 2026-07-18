package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.LeaveApprovalRequest;
import com.dinoryn.operion.dto.LeaveCreateRequest;
import com.dinoryn.operion.dto.LeaveResponse;
import com.dinoryn.operion.dto.LeaveUpdateRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.LeaveRequest;
import com.dinoryn.operion.entity.LeaveStatus;
import com.dinoryn.operion.exception.InvalidLeaveRequestException;
import com.dinoryn.operion.exception.LeaveRequestNotFoundException;
import com.dinoryn.operion.exception.UnauthorizedOperationException;
import com.dinoryn.operion.mapper.LeaveMapper;
import com.dinoryn.operion.repository.LeaveRequestRepository;
import com.dinoryn.operion.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveMapper leaveMapper;


    @Override
    @Transactional
    public LeaveResponse createLeaveRequest(
            Employee employee,
            LeaveCreateRequest request
    ) {

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidLeaveRequestException(
                    "End date cannot be before start date."
            );
        }

        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());

        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setCreatedDate(LocalDate.now());

        return leaveMapper.toResponse(
                leaveRequestRepository.save(leaveRequest)
        );
    }


    @Override
    @Transactional
    public LeaveResponse updateLeaveRequest(
            Employee currentUser,
            Long leaveRequestId,
            LeaveUpdateRequest request
    ) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(leaveRequestId)
                        .orElseThrow(() ->
                                new LeaveRequestNotFoundException(leaveRequestId));


        if (!leaveRequest.getEmployee()
                .getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedOperationException();
        }


        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {

            throw new InvalidLeaveRequestException(
                    "Only pending leave requests can be updated."
            );
        }


        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new InvalidLeaveRequestException(
                    "End date cannot be before start date."
            );
        }


        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());


        return leaveMapper.toResponse(
                leaveRequestRepository.save(leaveRequest)
        );
    }


    @Override
    @Transactional
    public LeaveResponse approveLeaveRequest(
            Employee approvalUser,
            Long leaveRequestId,
            LeaveApprovalRequest request
    ) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(leaveRequestId)
                        .orElseThrow(() ->
                                new LeaveRequestNotFoundException(leaveRequestId));


        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {

            throw new InvalidLeaveRequestException(
                    "Only pending leave requests can be approved or rejected."
            );
        }


        if (request.getStatus() == LeaveStatus.PENDING) {

            throw new InvalidLeaveRequestException(
                    "Approval status must be APPROVED or REJECTED."
            );
        }


        leaveRequest.setStatus(request.getStatus());
        leaveRequest.setApprovedBy(approvalUser);


        return leaveMapper.toResponse(
                leaveRequestRepository.save(leaveRequest)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public LeaveResponse getLeaveRequestById(
            Employee currentUser,
            Long leaveRequestId
    ) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(leaveRequestId)
                        .orElseThrow(() ->
                                new LeaveRequestNotFoundException(leaveRequestId));


        boolean isOwner =
                leaveRequest.getEmployee()
                        .getId()
                        .equals(currentUser.getId());


        boolean canViewAll =
                currentUser.getRole().name().equals("ADMIN")
                        || currentUser.getRole().name().equals("HR")
                        || currentUser.getRole().name().equals("MANAGER");


        if (!isOwner && !canViewAll) {
            throw new UnauthorizedOperationException();
        }


        return leaveMapper.toResponse(leaveRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getAllLeaveRequests(
            Pageable pageable
    ) {

        return leaveRequestRepository.findAll(pageable)
                .map(leaveMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getMyLeaveRequests(
            Employee employee,
            Pageable pageable
    ) {

        return leaveRequestRepository
                .findByEmployeeId(
                        employee.getId(),
                        pageable
                )
                .map(leaveMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getLeaveRequestsByStatus(
            LeaveStatus status,
            Pageable pageable
    ) {

        return leaveRequestRepository
                .findByStatus(status, pageable)
                .map(leaveMapper::toResponse);
    }


    @Override
    @Transactional
    public void deleteLeaveRequest(
            Employee employee,
            Long leaveRequestId
    ) {

        LeaveRequest leaveRequest =
                leaveRequestRepository
                        .findByIdAndEmployeeId(
                                leaveRequestId,
                                employee.getId()
                        )
                        .orElseThrow(() ->
                                new LeaveRequestNotFoundException(leaveRequestId));


        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {

            throw new InvalidLeaveRequestException(
                    "Only pending leave requests can be deleted."
            );
        }
        
        leaveRequestRepository.delete(leaveRequest);
    }
}