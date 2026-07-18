package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.TaskPriority;
import com.dinoryn.operion.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Task response payload")
public class TaskResponse {

    @Schema(description = "Task ID", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

    @Schema(description = "Task description", example = "Implement JWT-based authentication for the application")
    private String description;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Task creation date", example = "2024-02-01")
    private LocalDate createdDate;

    @Schema(description = "Project ID", example = "1")
    private Long projectId;

    @Schema(description = "Project name", example = "Website Redesign")
    private String projectName;

    @Schema(description = "Assigned employee ID", example = "1")
    private Long assignedEmployeeId;

    @Schema(description = "Assigned employee name", example = "John Doe")
    private String assignedEmployeeName;

    @Schema(description = "Record creation timestamp", example = "2024-02-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-02-01T10:30:00")
    private LocalDateTime updatedAt;
}