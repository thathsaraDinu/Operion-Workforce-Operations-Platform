package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Project member creation request payload")
public class ProjectMemberCreateRequest {

    @Schema(description = "Employee ID to add to project", example = "1", required = true)
    @NotNull
    private Long employeeId;

    @Schema(description = "Role of the employee in the project", example = "BACKEND_DEVELOPER", required = true)
    @NotNull
    private ProjectRole projectRole;
}