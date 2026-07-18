package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Leave request creation payload")
public class LeaveCreateRequest {

    @Schema(description = "Type of leave", example = "ANNUAL", required = true)
    @NotNull
    private LeaveType leaveType;

    @Schema(description = "Leave start date", example = "2024-02-01", required = true)
    @NotNull
    private LocalDate startDate;

    @Schema(description = "Leave end date", example = "2024-02-05", required = true)
    @NotNull
    private LocalDate endDate;

    @Schema(description = "Reason for leave", example = "Family vacation", required = true)
    @NotBlank
    private String reason;
}