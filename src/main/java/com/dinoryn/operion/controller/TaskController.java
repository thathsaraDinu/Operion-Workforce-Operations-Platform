package com.dinoryn.operion.controller;

import com.dinoryn.operion.dto.TaskCreateRequest;
import com.dinoryn.operion.dto.TaskResponse;
import com.dinoryn.operion.dto.TaskUpdateRequest;
import com.dinoryn.operion.entity.TaskStatus;
import com.dinoryn.operion.service.TaskService;
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
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Task CRUD operations and filtering")
public class TaskController {

    private final TaskService taskService;


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request
    ) {

        return new ResponseEntity<>(
                taskService.createTask(request),
                HttpStatus.CREATED
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by ID. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<TaskResponse> getTask(
            @Parameter(description = "Task ID") @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                taskService.getTaskById(id)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks with pagination. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                taskService.getAllTasks(pageable)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update task information. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request
    ) {

        return ResponseEntity.ok(
                taskService.updateTask(id, request)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a task by ID. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id
    ) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project", description = "Retrieve all tasks for a specific project. Accessible by all authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TaskResponse>> getTasksByProject(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                taskService.getTasksByProject(
                        projectId,
                        pageable
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get tasks by employee", description = "Retrieve all tasks assigned to a specific employee. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<TaskResponse>> getTasksByEmployee(
            @Parameter(description = "Employee ID") @PathVariable Long employeeId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                taskService.getTasksByEmployee(
                        employeeId,
                        pageable
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks filtered by status. Requires ADMIN, HR, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Task status (TODO|IN_PROGRESS|DONE)") @PathVariable TaskStatus status,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                taskService.getTasksByStatus(
                        status,
                        pageable
                )
        );
    }
}