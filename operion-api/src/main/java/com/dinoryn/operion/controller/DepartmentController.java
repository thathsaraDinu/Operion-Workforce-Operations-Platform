package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.ApiResponseBody;
import com.dinoryn.operion.dto.DepartmentCreateRequest;
import com.dinoryn.operion.dto.DepartmentResponse;
import com.dinoryn.operion.dto.DepartmentUpdateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.service.DepartmentService;

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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "Department CRUD operations and employee listing")
public class DepartmentController {

    private final DepartmentService departmentService;


    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PostMapping
    @Operation(summary = "Create department", description = "Create a new department. Requires ADMIN or HR role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentCreateRequest request
    ){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseBody.success(departmentService.saveDepartment(request), "Department created successfully"));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieve all departments with pagination. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseBody<Page<DepartmentResponse>>> getAllDepartments(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponseBody.success(departmentService.getAllDepartments(pageable))
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve a specific department by ID. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseBody<DepartmentResponse>> getDepartmentById(
            @Parameter(description = "Department ID") @PathVariable Long id
    ){

        return ResponseEntity.ok(
                ApiResponseBody.success(departmentService.getDepartmentById(id))
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update department", description = "Update department information. Requires ADMIN or HR role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<DepartmentResponse>> updateDepartment(
            @Parameter(description = "Department ID") @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateRequest request
    ){

        return ResponseEntity.ok(
                ApiResponseBody.success(departmentService.updateDepartment(id, request), "Department updated successfully")
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Delete a department by ID. Requires ADMIN role only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<Void>> deleteDepartment(
            @Parameter(description = "Department ID") @PathVariable Long id
    ){

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                ApiResponseBody.success(null, "Department deleted successfully")
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/{id}/employees")
    @Operation(summary = "Get department employees", description = "Retrieve all employees in a specific department. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<ApiResponseBody<Page<EmployeeResponse>>> getDepartmentEmployees(
            @Parameter(description = "Department ID") @PathVariable Long id,
            Pageable pageable
    ){

        return ResponseEntity.ok(
                ApiResponseBody.success(
                        departmentService.getEmployeesByDepartment(
                                id,
                                pageable
                        )
                )
        );
    }
}