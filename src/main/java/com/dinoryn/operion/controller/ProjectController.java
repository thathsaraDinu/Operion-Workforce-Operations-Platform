package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ProjectCreateRequest;
import com.dinoryn.operion.dto.ProjectResponse;
import com.dinoryn.operion.dto.ProjectUpdateRequest;
import com.dinoryn.operion.service.ProjectService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "Project CRUD operations")
public class ProjectController {

    private final ProjectService projectService;


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    @Operation(summary = "Create project", description = "Create a new project. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request
    ) {

        return new ResponseEntity<>(
                projectService.createProject(request),
                HttpStatus.CREATED
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project by ID. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ProjectResponse> getProject(
            @Parameter(description = "Project ID") @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieve all projects with pagination. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                projectService.getAllProjects(pageable)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Update project information. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {

        return ResponseEntity.ok(
                projectService.updateProject(id, request)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Delete a project by ID. Requires ADMIN or HR role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project ID") @PathVariable Long id
    ) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}