package com.dinoryn.operion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    // Admin/HR stats
    private Long totalEmployees;
    private Long totalDepartments;
    private Long totalProjects;
    private Long totalTasks;
    private Long pendingLeaveRequests;
    private Long activeProjects;
    private Long completedTasks;
    private Long employeesOnLeaveToday;
    
    // Manager stats
    private Long myProjects;
    private Long myTasks;
    private Long myActiveProjects;
    private Long myCompletedTasks;
    private Long teamPendingLeaves;
    private Long teamOnLeaveToday;
    
    // Employee stats
    private Long myPendingLeaves;
    private Long myCompletedTasksEmployee;
    
    // Common
    private LocalDate date;
    
    // Recent data (optional, for lists)
    private List<AttendanceSummary> recentAttendance;
    private List<LeaveSummary> recentLeaves;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceSummary {
        private Long id;
        private LocalDate date;
        private String clockIn;
        private String clockOut;
        private String status;
        private String employeeName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveSummary {
        private Long id;
        private String employeeName;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
    }
}
