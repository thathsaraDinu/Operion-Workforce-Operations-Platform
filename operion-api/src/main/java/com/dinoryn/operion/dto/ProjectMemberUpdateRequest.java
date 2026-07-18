package com.dinoryn.operion.dto;

import com.dinoryn.operion.entity.ProjectRole;
import lombok.Data;

@Data
public class ProjectMemberUpdateRequest {

    private ProjectRole projectRole;
}