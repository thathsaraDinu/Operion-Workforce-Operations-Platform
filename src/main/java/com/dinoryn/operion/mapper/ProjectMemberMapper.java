package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.ProjectMemberResponse;
import com.dinoryn.operion.entity.ProjectMember;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberMapper {


    public ProjectMemberResponse toResponse(ProjectMember member) {

        ProjectMemberResponse response = new ProjectMemberResponse();
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());

        response.setId(member.getId());

        response.setEmployeeId(
                member.getEmployee().getId()
        );

        response.setEmployeeName(
                member.getEmployee().getFirstName()
                        + " "
                        + member.getEmployee().getLastName()
        );

        response.setProjectId(
                member.getProject().getId()
        );

        response.setProjectRole(
                member.getProjectRole()
        );

        response.setAssignedDate(
                member.getAssignedDate()
        );

        return response;
    }
}