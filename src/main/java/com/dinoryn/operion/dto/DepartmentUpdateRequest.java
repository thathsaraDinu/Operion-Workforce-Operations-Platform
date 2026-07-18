package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Department update request payload")
public class DepartmentUpdateRequest {

    @Schema(description = "Department name", example = "Engineering")
    private String name;

    @Schema(description = "Department code", example = "ENG")
    private String code;

    @Schema(description = "Department description", example = "Software Engineering Department")
    private String description;
}