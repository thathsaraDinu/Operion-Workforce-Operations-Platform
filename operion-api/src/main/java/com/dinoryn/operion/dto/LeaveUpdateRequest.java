package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Leave request update payload")
public class LeaveUpdateRequest {

    @Schema(description = "Type of leave", example = "ANNUAL")
    private LeaveType leaveType;

    @Schema(description = "Leave start date", example = "2024-02-01")
    private LocalDate startDate;

    @Schema(description = "Leave end date", example = "2024-02-05")
    private LocalDate endDate;

    @Schema(description = "Reason for leave", example = "Family vacation")
    private String reason;
}