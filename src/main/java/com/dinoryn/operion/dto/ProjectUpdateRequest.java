package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Project update request payload")
public class ProjectUpdateRequest {

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
}