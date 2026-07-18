package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Attendance response payload")
public class AttendanceResponse {

    @Schema(description = "Attendance record ID", example = "1")
    private Long id;

    @Schema(description = "Employee ID", example = "1")
    private Long employeeId;

    @Schema(description = "Employee name", example = "John Doe")
    private String employeeName;

    @Schema(description = "Attendance date", example = "2024-01-15")
    private LocalDate date;

    @Schema(description = "Clock-in time", example = "2024-01-15T09:00:00")
    private LocalDateTime clockIn;

    @Schema(description = "Clock-out time", example = "2024-01-15T17:00:00")
    private LocalDateTime clockOut;

    @Schema(description = "Attendance status", example = "PRESENT")
    private AttendanceStatus status;

    @Schema(description = "Record creation timestamp", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-01-15T17:00:00")
    private LocalDateTime updatedAt;
}