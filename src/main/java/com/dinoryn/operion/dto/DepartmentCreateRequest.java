package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Department creation request payload")
public class DepartmentCreateRequest {

    @Schema(description = "Department name", example = "Engineering", required = true)
    @NotBlank
    private String name;

    @Schema(description = "Department code", example = "ENG", required = true)
    @NotBlank
    private String code;

    @Schema(description = "Department description", example = "Software Engineering Department")
    private String description;
}