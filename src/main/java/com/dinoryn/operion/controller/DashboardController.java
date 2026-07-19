package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ApiResponseBody;
import com.dinoryn.operion.dto.DashboardResponse;
import com.dinoryn.operion.entity.Role;
import com.dinoryn.operion.security.EmployeeUserDetails;
import com.dinoryn.operion.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and metrics")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get dashboard statistics", description = "Retrieves role-appropriate dashboard statistics based on the authenticated user's role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal EmployeeUserDetails employeeUserDetails
    ) {
        Role role = employeeUserDetails.employee().getRole();
        Long employeeId = employeeUserDetails.employee().getId();

        DashboardResponse response = dashboardService.getDashboard(role, employeeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseBody.success(response, "Dashboard statistics retrieved successfully"));
    }
}
