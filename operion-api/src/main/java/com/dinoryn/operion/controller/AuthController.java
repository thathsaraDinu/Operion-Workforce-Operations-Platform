package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ApiResponseBody;
import com.dinoryn.operion.dto.ChangePasswordRequest;
import com.dinoryn.operion.dto.ForgotPasswordRequest;
import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;
import com.dinoryn.operion.dto.ResetPasswordRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.security.EmployeeUserDetails;
import com.dinoryn.operion.security.RateLimiter;
import com.dinoryn.operion.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final EmployeeRepository employeeRepository;
    private final RateLimiter rateLimiter;


    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password to receive JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<ApiResponseBody<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ){

        return ResponseEntity.ok(
                ApiResponseBody.success(authService.login(request), "Login successful")
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request a password reset link via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "429", description = "Too many requests - rate limit exceeded")
    })
    public ResponseEntity<ApiResponseBody<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ){
        // Rate limit by email and IP
        String rateLimitKey = request.getEmail() + ":" + getClientIp(httpRequest);
        if (!rateLimiter.tryConsume(rateLimitKey)) {
            return ResponseEntity.status(429).body(
                    ApiResponseBody.error("Too many password reset requests. Please try again later.")
            );
        }

        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponseBody.success(null, "Password reset link sent to your email")
        );
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using the token received via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<ApiResponseBody<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ){
        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponseBody.success(null, "Password reset successful")
        );
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password when logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid current password or request body")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseBody<Void>> changePassword(
            @AuthenticationPrincipal EmployeeUserDetails employeeUserDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        authService.changePassword(employeeUserDetails.employee().getId(), request);
        return ResponseEntity.ok(
                ApiResponseBody.success(null, "Password changed successfully")
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}