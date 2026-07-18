package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.LeaveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Leave approval request payload")
public class LeaveApprovalRequest {

    @Schema(description = "Leave status (APPROVED or REJECTED)", example = "APPROVED", required = true)
    @NotNull
    private LeaveStatus status;
}