package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Project member response payload")
public class ProjectMemberResponse {

    @Schema(description = "Project member ID", example = "1")
    private Long id;

    @Schema(description = "Employee ID", example = "1")
    private Long employeeId;

    @Schema(description = "Employee name", example = "John Doe")
    private String employeeName;

    @Schema(description = "Project ID", example = "1")
    private Long projectId;

    @Schema(description = "Role of the employee in the project", example = "BACKEND_DEVELOPER")
    private ProjectRole projectRole;

    @Schema(description = "Date when employee was assigned to project", example = "2024-02-01")
    private LocalDate assignedDate;

    @Schema(description = "Record creation timestamp", example = "2024-02-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-02-01T10:30:00")
    private LocalDateTime updatedAt;
}