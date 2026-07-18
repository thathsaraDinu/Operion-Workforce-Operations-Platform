package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Project response payload")
public class ProjectResponse {

    @Schema(description = "Project ID", example = "1")
    private Long id;

    @Schema(description = "Project name", example = "Website Redesign")
    private String name;

    @Schema(description = "Project description", example = "Complete redesign of the company website")
    private String description;

    @Schema(description = "Project start date", example = "2024-02-01")
    private LocalDate startDate;

    @Schema(description = "Project end date", example = "2024-06-30")
    private LocalDate endDate;

    @Schema(description = "Project status", example = "ACTIVE")
    private ProjectStatus status;

    @Schema(description = "Record creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}