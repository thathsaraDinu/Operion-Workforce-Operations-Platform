package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.TaskPriority;
import com.dinoryn.operion.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Task creation request payload")
public class TaskCreateRequest {

    @Schema(description = "Task title", example = "Implement user authentication", required = true)
    @NotBlank
    private String title;

    @Schema(description = "Task priority", example = "HIGH", required = true)
    @NotNull
    private TaskPriority priority;

    @Schema(description = "Task description", example = "Implement JWT-based authentication for the application")
    private String description;

    @Schema(description = "Task status", example = "TODO", required = true)
    @NotNull
    private TaskStatus status;

    @Schema(description = "Project ID to assign task to", example = "1", required = true)
    @NotNull
    private Long projectId;

    @Schema(description = "Employee ID to assign task to", example = "1", required = true)
    @NotNull
    private Long assignedEmployeeId;
}