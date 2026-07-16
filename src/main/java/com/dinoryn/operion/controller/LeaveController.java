package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.LeaveApprovalRequest;
import com.dinoryn.operion.dto.LeaveCreateRequest;
import com.dinoryn.operion.dto.LeaveResponse;
import com.dinoryn.operion.dto.LeaveUpdateRequest;
import com.dinoryn.operion.entity.LeaveStatus;
import com.dinoryn.operion.security.EmployeeUserDetails;
import com.dinoryn.operion.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Management", description = "Leave request and approval management endpoints")
public class LeaveController {

    private final LeaveService leaveService;


    @PostMapping
    @Operation(summary = "Create leave request", description = "Submit a new leave request for the authenticated employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Leave request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<LeaveResponse> createLeaveRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Valid @RequestBody LeaveCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        leaveService.createLeaveRequest(
                                user.employee(),
                                request
                        )
                );
    }


    @GetMapping("/me")
    @Operation(summary = "Get my leave requests", description = "Retrieve leave requests for the authenticated employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<LeaveResponse>> getMyLeaveRequests(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                leaveService.getMyLeaveRequests(
                        user.employee(),
                        pageable
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/{leaveRequestId:\\d+}")
    @Operation(summary = "Get leave request by ID", description = "Retrieve a specific leave request by ID. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave request found"),
            @ApiResponse(responseCode = "404", description = "Leave request not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<LeaveResponse> getLeaveRequestById(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Parameter(description = "Leave request ID") @PathVariable Long leaveRequestId
    ) {

        return ResponseEntity.ok(
                leaveService.getLeaveRequestById(
                        user.employee(),
                        leaveRequestId
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    @Operation(summary = "Get all leave requests", description = "Retrieve all leave requests (admin/manager view). Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave requests retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<LeaveResponse>> getAllLeaveRequests(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                leaveService.getAllLeaveRequests(pageable)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/status/{status}")
    @Operation(summary = "Get leave requests by status", description = "Retrieve leave requests filtered by status. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave requests retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<LeaveResponse>> getLeaveRequestsByStatus(
            @Parameter(description = "Leave status (PENDING|APPROVED|REJECTED)") @PathVariable LeaveStatus status,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                leaveService.getLeaveRequestsByStatus(
                        status,
                        pageable
                )
        );
    }


    @PutMapping("/{leaveRequestId}")
    @Operation(summary = "Update leave request", description = "Update an existing leave request. Employees can only update their own requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave request updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found"),
            @ApiResponse(responseCode = "403", description = "Cannot update other's requests or already approved requests")
    })
    public ResponseEntity<LeaveResponse> updateLeaveRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Parameter(description = "Leave request ID") @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveUpdateRequest request
    ) {

        return ResponseEntity.ok(
                leaveService.updateLeaveRequest(
                        user.employee(),
                        leaveRequestId,
                        request
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PatchMapping("/{leaveRequestId}/approval")
    @Operation(summary = "Approve/Reject leave request", description = "Approve or reject a leave request. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leave request status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<LeaveResponse> approveLeaveRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Parameter(description = "Leave request ID") @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveApprovalRequest request
    ) {

        return ResponseEntity.ok(
                leaveService.approveLeaveRequest(
                        user.employee(),
                        leaveRequestId,
                        request
                )
        );
    }


    @DeleteMapping("/{leaveRequestId}")
    @Operation(summary = "Delete leave request", description = "Delete a leave request. Employees can only delete their own pending requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Leave request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found"),
            @ApiResponse(responseCode = "403", description = "Cannot delete other's requests or already processed requests")
    })
    public ResponseEntity<Void> deleteLeaveRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Parameter(description = "Leave request ID") @PathVariable Long leaveRequestId
    ) {

        leaveService.deleteLeaveRequest(
                user.employee(),
                leaveRequestId
        );

        return ResponseEntity.noContent().build();
    }
}