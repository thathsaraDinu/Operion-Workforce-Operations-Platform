package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ApiResponseBody;
import com.dinoryn.operion.dto.AttendanceResponse;
import com.dinoryn.operion.dto.AttendanceUpdateRequest;
import com.dinoryn.operion.security.EmployeeUserDetails;
import com.dinoryn.operion.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "Attendance tracking and management endpoints")
public class AttendanceController {

    private final AttendanceService attendanceService;


    @PostMapping("/clock-in")
    @Operation(summary = "Clock in", description = "Record clock-in time for the authenticated employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clock-in recorded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Already clocked in today")
    })
    public ResponseEntity<ApiResponseBody<AttendanceResponse>> clockIn(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.clockIn(
                                user.employee()
                        ),
                        "Clock-in recorded successfully"
                )
        );
    }


    @PatchMapping("/clock-out")
    @Operation(summary = "Clock out", description = "Record clock-out time for the authenticated employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clock-out recorded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Not clocked in or already clocked out")
    })
    public ResponseEntity<ApiResponseBody<AttendanceResponse>> clockOut(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.clockOut(
                                user.employee()
                        ),
                        "Clock-out recorded successfully"
                )
        );
    }


    @GetMapping("/me")
    @Operation(summary = "Get my attendance", description = "Retrieve attendance records for the authenticated employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseBody<Page<AttendanceResponse>>> getMyAttendance(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.getMyAttendance(
                                user.employee(),
                                pageable
                        )
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    @Operation(summary = "Get all attendance", description = "Retrieve all attendance records (admin/manager view). Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<Page<AttendanceResponse>>> getAllAttendance(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.getAllAttendance(
                                pageable
                        )
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee attendance", description = "Retrieve attendance records for a specific employee. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<Page<AttendanceResponse>>> getEmployeeAttendance(
            @Parameter(description = "Employee ID") @PathVariable Long employeeId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.getEmployeeAttendance(
                                employeeId,
                                pageable
                        )
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{attendanceId}")
    @Operation(summary = "Update attendance", description = "Update attendance record (admin/HR only). Requires ADMIN or HR role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance updated successfully"),
            @ApiResponse(responseCode = "404", description = "Attendance record not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<AttendanceResponse>> updateAttendance(
            @Parameter(description = "Attendance record ID") @PathVariable Long attendanceId,
            @Valid @RequestBody AttendanceUpdateRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        attendanceService.updateAttendance(
                                attendanceId,
                                request
                        ),
                        "Attendance updated successfully"
                )
        );
    }
}