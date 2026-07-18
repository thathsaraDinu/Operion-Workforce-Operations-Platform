package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.LeaveStatus;
import com.dinoryn.operion.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Leave request response payload")
public class LeaveResponse {

    @Schema(description = "Leave request ID", example = "1")
    private Long id;

    @Schema(description = "Employee ID", example = "1")
    private Long employeeId;

    @Schema(description = "Employee name", example = "John Doe")
    private String employeeName;

    @Schema(description = "Type of leave", example = "ANNUAL")
    private LeaveType leaveType;

    @Schema(description = "Leave start date", example = "2024-02-01")
    private LocalDate startDate;

    @Schema(description = "Leave end date", example = "2024-02-05")
    private LocalDate endDate;

    @Schema(description = "Reason for leave", example = "Family vacation")
    private String reason;

    @Schema(description = "Leave request status", example = "PENDING")
    private LeaveStatus status;

    @Schema(description = "ID of the approver", example = "2")
    private Long approvedById;

    @Schema(description = "Name of the approver", example = "Jane Smith")
    private String approvedByName;

    @Schema(description = "Date when leave request was created", example = "2024-01-20")
    private LocalDate createdDate;

    @Schema(description = "Record creation timestamp", example = "2024-01-20T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-01-20T10:30:00")
    private LocalDateTime updatedAt;
}