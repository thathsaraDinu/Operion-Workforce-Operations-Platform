package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.ProjectMemberCreateRequest;
import com.dinoryn.operion.dto.ProjectMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectMemberService {

    ProjectMemberResponse addMember(
            Long projectId,
            ProjectMemberCreateRequest request
    );

    Page<ProjectMemberResponse> getProjectMembers(
            Long projectId,
            Pageable pageable
    );

    void removeMember(Long projectId, Long memberId);
}