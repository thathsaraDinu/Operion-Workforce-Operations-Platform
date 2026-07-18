package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic API response wrapper for consistent response structure")
public class ApiResponseBody<T> {

    @Schema(description = "Operation success indicator", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response payload data")
    private T data;

    @Schema(description = "Response timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponseBody<T> success(T data) {
        return new ApiResponseBody<>(true, "Operation completed successfully", data, LocalDateTime.now());
    }

    public static <T> ApiResponseBody<T> success(T data, String message) {
        return new ApiResponseBody<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponseBody<T> error(String message) {
        return new ApiResponseBody<>(false, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponseBody<T> error(String message, T data) {
        return new ApiResponseBody<>(false, message, data, LocalDateTime.now());
    }
}
