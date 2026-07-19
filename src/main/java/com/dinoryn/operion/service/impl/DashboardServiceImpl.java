package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.DashboardResponse;
import com.dinoryn.operion.entity.Attendance;
import com.dinoryn.operion.entity.LeaveRequest;
import com.dinoryn.operion.entity.LeaveStatus;
import com.dinoryn.operion.entity.ProjectStatus;
import com.dinoryn.operion.entity.Role;
import com.dinoryn.operion.entity.TaskStatus;
import com.dinoryn.operion.repository.AttendanceRepository;
import com.dinoryn.operion.repository.DepartmentRepository;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.repository.LeaveRequestRepository;
import com.dinoryn.operion.repository.ProjectRepository;
import com.dinoryn.operion.repository.TaskRepository;
import com.dinoryn.operion.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public DashboardResponse getDashboard(Role role, Long employeeId) {
        LocalDate today = LocalDate.now();
        
        DashboardResponse response = new DashboardResponse();
        response.setDate(today);
        
        switch (role) {
            case ADMIN:
                return getAdminDashboard(response, today);
            case HR:
                return getHrDashboard(response, today);
            case MANAGER:
                return getManagerDashboard(response, today, employeeId);
            case EMPLOYEE:
                return getEmployeeDashboard(response, today, employeeId);
            default:
                return response;
        }
    }
    
    private DashboardResponse getAdminDashboard(DashboardResponse response, LocalDate today) {
        response.setTotalEmployees(employeeRepository.count());
        response.setTotalDepartments(departmentRepository.count());
        response.setTotalProjects(projectRepository.count());
        response.setTotalTasks(taskRepository.count());
        response.setPendingLeaveRequests(leaveRequestRepository.countByStatus(LeaveStatus.PENDING));
        response.setActiveProjects(projectRepository.countByStatus(ProjectStatus.ACTIVE));
        response.setCompletedTasks(taskRepository.countByStatus(TaskStatus.DONE));
        response.setEmployeesOnLeaveToday(leaveRequestRepository.countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LeaveStatus.APPROVED, today, today));
        response.setRecentAttendance(getRecentAttendance(5));
        response.setRecentLeaves(getRecentLeaves(5));
        return response;
    }
    
    private DashboardResponse getHrDashboard(DashboardResponse response, LocalDate today) {
        response.setTotalEmployees(employeeRepository.count());
        response.setTotalDepartments(departmentRepository.count());
        response.setTotalProjects(projectRepository.count());
        response.setPendingLeaveRequests(leaveRequestRepository.countByStatus(LeaveStatus.PENDING));
        response.setActiveProjects(projectRepository.countByStatus(ProjectStatus.ACTIVE));
        response.setEmployeesOnLeaveToday(leaveRequestRepository.countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LeaveStatus.APPROVED, today, today));
        response.setRecentAttendance(getRecentAttendance(5));
        response.setRecentLeaves(getRecentLeaves(5));
        return response;
    }
    
    private DashboardResponse getManagerDashboard(DashboardResponse response, LocalDate today, Long employeeId) {
        response.setMyProjects(projectRepository.countByMembers_EmployeeId(employeeId));
        response.setMyTasks(taskRepository.countByAssignedEmployeeId(employeeId));
        response.setMyActiveProjects(projectRepository.countByMembers_EmployeeIdAndStatus(employeeId, ProjectStatus.ACTIVE));
        response.setMyCompletedTasks(taskRepository.countByAssignedEmployeeIdAndStatus(employeeId, TaskStatus.DONE));
        response.setTeamPendingLeaves(leaveRequestRepository.countByStatus(LeaveStatus.PENDING));
        response.setTeamOnLeaveToday(leaveRequestRepository.countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LeaveStatus.APPROVED, today, today));
        response.setRecentAttendance(getRecentAttendanceForEmployee(employeeId, 5));
        response.setRecentLeaves(getRecentLeaves(5));
        return response;
    }
    
    private DashboardResponse getEmployeeDashboard(DashboardResponse response, LocalDate today, Long employeeId) {
        response.setMyPendingLeaves(leaveRequestRepository.countByEmployeeIdAndStatus(employeeId, LeaveStatus.PENDING));
        response.setMyCompletedTasksEmployee(taskRepository.countByAssignedEmployeeIdAndStatus(employeeId, TaskStatus.DONE));
        response.setRecentAttendance(getRecentAttendanceForEmployee(employeeId, 5));
        response.setRecentLeaves(getRecentLeavesForEmployee(employeeId, 5));
        return response;
    }

    private List<DashboardResponse.AttendanceSummary> getRecentAttendance(int limit) {
        List<Attendance> attendances = attendanceRepository.findAllByOrderByDateDesc(PageRequest.of(0, limit));
        return attendances.stream()
                .map(a -> new DashboardResponse.AttendanceSummary(
                        a.getId(),
                        a.getDate(),
                        a.getClockIn() != null ? a.getClockIn().format(TIME_FORMATTER) : null,
                        a.getClockOut() != null ? a.getClockOut().format(TIME_FORMATTER) : null,
                        a.getStatus() != null ? a.getStatus().name() : null,
                        a.getEmployee() != null ? a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName() : null
                ))
                .collect(Collectors.toList());
    }

    private List<DashboardResponse.AttendanceSummary> getRecentAttendanceForEmployee(Long employeeId, int limit) {
        List<Attendance> attendances = attendanceRepository.findByEmployeeId(employeeId, PageRequest.of(0, limit)).getContent();
        return attendances.stream()
                .map(a -> new DashboardResponse.AttendanceSummary(
                        a.getId(),
                        a.getDate(),
                        a.getClockIn() != null ? a.getClockIn().format(TIME_FORMATTER) : null,
                        a.getClockOut() != null ? a.getClockOut().format(TIME_FORMATTER) : null,
                        a.getStatus() != null ? a.getStatus().name() : null,
                        a.getEmployee() != null ? a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName() : null
                ))
                .collect(Collectors.toList());
    }

    private List<DashboardResponse.LeaveSummary> getRecentLeaves(int limit) {
        List<LeaveRequest> leaves = leaveRequestRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
        return leaves.stream()
                .map(l -> new DashboardResponse.LeaveSummary(
                        l.getId(),
                        l.getEmployee() != null ? l.getEmployee().getFirstName() + " " + l.getEmployee().getLastName() : null,
                        l.getLeaveType() != null ? l.getLeaveType().name() : null,
                        l.getStartDate(),
                        l.getEndDate(),
                        l.getStatus() != null ? l.getStatus().name() : null
                ))
                .collect(Collectors.toList());
    }

    private List<DashboardResponse.LeaveSummary> getRecentLeavesForEmployee(Long employeeId, int limit) {
        List<LeaveRequest> leaves = leaveRequestRepository.findByEmployeeId(employeeId, PageRequest.of(0, limit)).getContent();
        return leaves.stream()
                .map(l -> new DashboardResponse.LeaveSummary(
                        l.getId(),
                        l.getEmployee() != null ? l.getEmployee().getFirstName() + " " + l.getEmployee().getLastName() : null,
                        l.getLeaveType() != null ? l.getLeaveType().name() : null,
                        l.getStartDate(),
                        l.getEndDate(),
                        l.getStatus() != null ? l.getStatus().name() : null
                ))
                .collect(Collectors.toList());
    }
}
