package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Login response payload containing JWT token and user information")
public class LoginResponse {


    @Schema(
            description = "JWT authentication token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;


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
}