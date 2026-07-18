package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Department response payload")
public class DepartmentResponse {

    @Schema(description = "Department ID", example = "1")
    private Long id;

    @Schema(description = "Department name", example = "Engineering")
    private String name;

    @Schema(description = "Department code", example = "ENG")
    private String code;

    @Schema(description = "Department description", example = "Software Engineering Department")
    private String description;

    @Schema(description = "Number of employees in the department", example = "15")
    private long employeeCount;

    @Schema(description = "Record creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

}