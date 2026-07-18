package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Project creation request payload")
public class ProjectCreateRequest {

    @Schema(description = "Project name", example = "Website Redesign", required = true)
    @NotBlank
    private String name;

    @Schema(description = "Project start date", example = "2024-02-01", required = true)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "Project status", example = "PLANNING", required = true)
    @NotNull
    private ProjectStatus status;

    @Schema(description = "Project description", example = "Complete redesign of the company website")
    private String description;

    @Schema(description = "Project end date", example = "2024-06-30")
    private LocalDate endDate;

}