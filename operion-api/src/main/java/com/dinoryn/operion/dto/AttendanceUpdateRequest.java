package com.dinoryn.operion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Attendance update request payload")
public class AttendanceUpdateRequest {

    @Schema(description = "Clock-in time", example = "2024-01-15T09:00:00")
    private LocalDateTime clockIn;

    @Schema(description = "Clock-out time", example = "2024-01-15T17:00:00")
    private LocalDateTime clockOut;
}