package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ProjectMemberCreateRequest;
import com.dinoryn.operion.dto.ProjectMemberResponse;
import com.dinoryn.operion.security.EmployeeUserDetails;
import com.dinoryn.operion.service.ProjectMemberService;
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
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
@Tag(name = "Project Member Management", description = "Project member assignment and management endpoints")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    @Operation(summary = "Add member to project", description = "Add an employee to a project. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Project or employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ProjectMemberResponse> addMember(
            @Parameter(hidden = true) @AuthenticationPrincipal EmployeeUserDetails user,
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberCreateRequest request
    ) {

        return new ResponseEntity<>(
                projectMemberService.addMember(
                        projectId,
                        request
                ),
                HttpStatus.CREATED
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping
    @Operation(summary = "Get project members", description = "Retrieve all members of a specific project. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project members retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ProjectMemberResponse>> getProjectMembers(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                projectMemberService.getProjectMembers(
                        projectId,
                        pageable
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove member from project", description = "Remove a member from a project. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member removed successfully"),
            @ApiResponse(responseCode = "404", description = "Project or member not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> removeMember(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @Parameter(description = "Project member ID") @PathVariable Long memberId
    ) {

        projectMemberService.removeMember(
                projectId,
                memberId
        );

        return ResponseEntity.noContent().build();
    }
}