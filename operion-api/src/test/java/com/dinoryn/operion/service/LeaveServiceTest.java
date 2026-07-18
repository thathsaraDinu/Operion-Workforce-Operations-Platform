package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.LeaveApprovalRequest;
import com.dinoryn.operion.dto.LeaveCreateRequest;
import com.dinoryn.operion.dto.LeaveResponse;
import com.dinoryn.operion.dto.LeaveUpdateRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.LeaveRequest;
import com.dinoryn.operion.entity.LeaveStatus;
import com.dinoryn.operion.entity.LeaveType;
import com.dinoryn.operion.entity.Role;
import com.dinoryn.operion.exception.InvalidLeaveRequestException;
import com.dinoryn.operion.exception.LeaveRequestNotFoundException;
import com.dinoryn.operion.exception.UnauthorizedOperationException;
import com.dinoryn.operion.repository.LeaveRequestRepository;
import com.dinoryn.operion.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private com.dinoryn.operion.mapper.LeaveMapper leaveMapper;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private Employee employee;
    private LeaveRequest leaveRequest;
    private LeaveCreateRequest createRequest;
    private LeaveUpdateRequest updateRequest;
    private LeaveApprovalRequest approvalRequest;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setRole(Role.EMPLOYEE);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(LeaveType.ANNUAL);
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(5));
        leaveRequest.setReason("Vacation");
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setCreatedDate(LocalDate.now());

        createRequest = new LeaveCreateRequest();
        createRequest.setLeaveType(LeaveType.ANNUAL);
        createRequest.setStartDate(LocalDate.now().plusDays(1));
        createRequest.setEndDate(LocalDate.now().plusDays(5));
        createRequest.setReason("Vacation");

        updateRequest = new LeaveUpdateRequest();
        updateRequest.setLeaveType(LeaveType.SICK);
        updateRequest.setStartDate(LocalDate.now().plusDays(10));
        updateRequest.setEndDate(LocalDate.now().plusDays(12));
        updateRequest.setReason("Medical appointment");

        approvalRequest = new LeaveApprovalRequest();
        approvalRequest.setStatus(LeaveStatus.APPROVED);
    }

    @Test
    void createLeaveRequest_ShouldReturnLeaveResponse_WhenValidRequest() {
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);

        LeaveResponse result = leaveService.createLeaveRequest(employee, createRequest);

        assertNotNull(result);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        assertEquals(LeaveStatus.PENDING, leaveRequest.getStatus());
    }

    @Test
    void createLeaveRequest_ShouldThrowException_WhenEndDateBeforeStartDate() {
        createRequest.setEndDate(LocalDate.now().minusDays(1));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.createLeaveRequest(employee, createRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void updateLeaveRequest_ShouldUpdateLeave_WhenOwnerAndPending() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);

        LeaveResponse result = leaveService.updateLeaveRequest(employee, 1L, updateRequest);

        assertNotNull(result);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        assertEquals(LeaveType.SICK, leaveRequest.getLeaveType());
    }

    @Test
    void updateLeaveRequest_ShouldThrowException_WhenNotOwner() {
        Employee otherEmployee = new Employee();
        otherEmployee.setId(2L);
        
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(UnauthorizedOperationException.class, 
            () -> leaveService.updateLeaveRequest(otherEmployee, 1L, updateRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void updateLeaveRequest_ShouldThrowException_WhenNotPending() {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.updateLeaveRequest(employee, 1L, updateRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void updateLeaveRequest_ShouldThrowException_WhenEndDateBeforeStartDate() {
        updateRequest.setEndDate(LocalDate.now().minusDays(1));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.updateLeaveRequest(employee, 1L, updateRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void approveLeaveRequest_ShouldApproveLeave_WhenPending() {
        Employee manager = new Employee();
        manager.setId(2L);
        manager.setRole(Role.MANAGER);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);

        LeaveResponse result = leaveService.approveLeaveRequest(manager, 1L, approvalRequest);

        assertNotNull(result);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        assertEquals(LeaveStatus.APPROVED, leaveRequest.getStatus());
        assertEquals(manager, leaveRequest.getApprovedBy());
    }

    @Test
    void approveLeaveRequest_ShouldRejectLeave_WhenPending() {
        Employee manager = new Employee();
        manager.setId(2L);
        manager.setRole(Role.MANAGER);
        
        approvalRequest.setStatus(LeaveStatus.REJECTED);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);

        LeaveResponse result = leaveService.approveLeaveRequest(manager, 1L, approvalRequest);

        assertNotNull(result);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
        assertEquals(LeaveStatus.REJECTED, leaveRequest.getStatus());
    }

    @Test
    void approveLeaveRequest_ShouldThrowException_WhenNotPending() {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        Employee manager = new Employee();
        manager.setId(2L);
        manager.setRole(Role.MANAGER);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.approveLeaveRequest(manager, 1L, approvalRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void approveLeaveRequest_ShouldThrowException_WhenStatusIsPending() {
        approvalRequest.setStatus(LeaveStatus.PENDING);
        Employee manager = new Employee();
        manager.setId(2L);
        manager.setRole(Role.MANAGER);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.approveLeaveRequest(manager, 1L, approvalRequest));
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void getLeaveRequestById_ShouldReturnLeave_WhenOwner() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());

        LeaveResponse result = leaveService.getLeaveRequestById(employee, 1L);

        assertNotNull(result);
        verify(leaveRequestRepository).findById(1L);
    }

    @Test
    void getLeaveRequestById_ShouldReturnLeave_WhenAdmin() {
        Employee admin = new Employee();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());

        LeaveResponse result = leaveService.getLeaveRequestById(admin, 1L);

        assertNotNull(result);
        verify(leaveRequestRepository).findById(1L);
    }

    @Test
    void getLeaveRequestById_ShouldThrowException_WhenNotOwnerAndNotAdmin() {
        Employee otherEmployee = new Employee();
        otherEmployee.setId(2L);
        otherEmployee.setRole(Role.EMPLOYEE);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(UnauthorizedOperationException.class, 
            () -> leaveService.getLeaveRequestById(otherEmployee, 1L));
    }

    @Test
    void deleteLeaveRequest_ShouldDeleteLeave_WhenOwnerAndPending() {
        when(leaveRequestRepository.findByIdAndEmployeeId(1L, 1L))
            .thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveRequestRepository).delete(any());

        leaveService.deleteLeaveRequest(employee, 1L);

        verify(leaveRequestRepository).delete(leaveRequest);
    }

    @Test
    void deleteLeaveRequest_ShouldThrowException_WhenNotPending() {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findByIdAndEmployeeId(1L, 1L))
            .thenReturn(Optional.of(leaveRequest));

        assertThrows(InvalidLeaveRequestException.class, 
            () -> leaveService.deleteLeaveRequest(employee, 1L));
        verify(leaveRequestRepository, never()).delete(any());
    }

    @Test
    void deleteLeaveRequest_ShouldThrowException_WhenNotFound() {
        when(leaveRequestRepository.findByIdAndEmployeeId(1L, 1L))
            .thenReturn(Optional.empty());

        assertThrows(LeaveRequestNotFoundException.class, 
            () -> leaveService.deleteLeaveRequest(employee, 1L));
        verify(leaveRequestRepository, never()).delete(any());
    }

    @Test
    void getAllLeaveRequests_ShouldReturnPageOfLeaves() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LeaveRequest> leavePage = new PageImpl<>(List.of(leaveRequest));
        
        when(leaveRequestRepository.findAll(pageable)).thenReturn(leavePage);
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());

        Page<LeaveResponse> result = leaveService.getAllLeaveRequests(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(leaveRequestRepository).findAll(pageable);
    }

    @Test
    void getMyLeaveRequests_ShouldReturnEmployeeLeaves() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LeaveRequest> leavePage = new PageImpl<>(List.of(leaveRequest));
        
        when(leaveRequestRepository.findByEmployeeId(1L, pageable)).thenReturn(leavePage);
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());

        Page<LeaveResponse> result = leaveService.getMyLeaveRequests(employee, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(leaveRequestRepository).findByEmployeeId(1L, pageable);
    }

    @Test
    void getLeaveRequestsByStatus_ShouldReturnFilteredLeaves() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LeaveRequest> leavePage = new PageImpl<>(List.of(leaveRequest));
        
        when(leaveRequestRepository.findByStatus(LeaveStatus.PENDING, pageable))
            .thenReturn(leavePage);
        when(leaveMapper.toResponse(any())).thenReturn(new LeaveResponse());

        Page<LeaveResponse> result = leaveService.getLeaveRequestsByStatus(LeaveStatus.PENDING, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(leaveRequestRepository).findByStatus(LeaveStatus.PENDING, pageable);
    }
}
