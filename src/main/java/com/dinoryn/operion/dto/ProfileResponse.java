package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response containing user information")
public class ProfileResponse {
    
    @Schema(
            description = "Authenticated employee ID",
            example = "1"
    )
    private Long id;
    
    @Schema(
            description = "Authenticated employee email",
            example = "admin@operion.com"
    )
    private String email;
    
    @Schema(
            description = "User role",
            example = "ADMIN"
    )
    private String role;
    
    @Schema(
            description = "Employee first name",
            example = "John"
    )
    private String firstName;
    
    @Schema(
            description = "Employee last name",
            example = "Doe"
    )
    private String lastName;
    
    @Schema(
            description = "Department ID if assigned",
            example = "1"
    )
    private Long departmentId;
    
    @Schema(
            description = "Department name if assigned",
            example = "Engineering"
    )
    private String departmentName;
    
    @Schema(
            description = "Employee position",
            example = "Software Engineer"
    )
    private String position;
    
    @Schema(
            description = "Employee phone number",
            example = "+1234567890"
    )
    private String phone;
}
