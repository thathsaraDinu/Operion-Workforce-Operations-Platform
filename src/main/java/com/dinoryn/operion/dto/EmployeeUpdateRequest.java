package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Employee update request payload")
public class EmployeeUpdateRequest {

    @Schema(description = "Employee first name", example = "John")
    private String firstName;

    @Schema(description = "Employee last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Employee role", example = "EMPLOYEE")
    private Role role;

    @Schema(description = "Employee phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Employee address", example = "123 Main St, City, Country")
    private String address;

    @Schema(description = "Employee job position", example = "Software Engineer")
    private String position;

    @Schema(description = "Employee joining date", example = "2024-01-15")
    private LocalDate joiningDate;
}