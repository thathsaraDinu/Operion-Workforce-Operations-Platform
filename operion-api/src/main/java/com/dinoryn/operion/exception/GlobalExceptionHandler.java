package com.dinoryn.operion.exception;

import com.dinoryn.operion.dto.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseBody.error("You are not authorized to perform this operation."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error("Endpoint not found: " + request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBody.error(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleInvalidJson(
            HttpMessageNotReadableException ex
    ) {

        String message = "Invalid request data";

        if (ex.getCause() instanceof InvalidFormatException cause) {

            String field = cause.getPath()
                    .get(0)
                    .getPropertyName();

            if (cause.getTargetType().isEnum()) {
                message = "Invalid value for field '" + field + "'";
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBody.error(message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleBadCredentials(
            BadCredentialsException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseBody.error("Invalid email or password"));
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleEmployeeNotFound(
            EmployeeNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleDepartmentNotFound(
            DepartmentNotFoundException exception
    ){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(DepartmentHasEmployeesException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleDepartmentHasEmployees(
            DepartmentHasEmployeesException exception
    ){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleProjectNotFound(
            ProjectNotFoundException exception
    ){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleTaskNotFound(
            TaskNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(LeaveRequestNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleLeaveRequestNotFound(
            LeaveRequestNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(InvalidLeaveRequestException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleInvalidLeaveRequest(
            InvalidLeaveRequestException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleUnauthorizedOperation(
            UnauthorizedOperationException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(InvalidAttendanceException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleInvalidAttendance(
            InvalidAttendanceException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBody.error(exception.getMessage()));
    }

    @ExceptionHandler(ProjectMemberNotFoundException.class)
    public ResponseEntity<ApiResponseBody<Void>> handleProjectMemberNotFound(
            ProjectMemberNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBody.error(exception.getMessage()));
    }
}