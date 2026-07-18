package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Employee creation request payload")
public class EmployeeCreateRequest {

    @Schema(description = "Employee first name", example = "John", required = true)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Employee last name", example = "Doe", required = true)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Employee email address", example = "john.doe@example.com", required = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Employee password", example = "SecurePass123", required = true)
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "Employee role", example = "EMPLOYEE", required = true)
    @NotNull(message = "Role is required")
    private Role role;

    @Schema(description = "Department ID to assign employee to", example = "1")
    private Long departmentId;

    @Schema(description = "Employee phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Employee address", example = "123 Main St, City, Country")
    private String address;

    @Schema(description = "Employee job position", example = "Software Engineer")
    private String position;

    @Schema(description = "Employee joining date", example = "2024-01-15")
    private LocalDate joiningDate;
}