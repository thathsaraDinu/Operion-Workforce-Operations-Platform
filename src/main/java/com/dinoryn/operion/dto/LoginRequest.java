package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request payload")
public class LoginRequest {

    @Schema(description = "User email address", example = "user@example.com", required = true)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "User password", example = "password123", required = true)
    @NotBlank
    private String password;
}