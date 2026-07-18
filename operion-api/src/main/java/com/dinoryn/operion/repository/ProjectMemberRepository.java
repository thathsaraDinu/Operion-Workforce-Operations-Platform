package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.ProjectMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, Long> {

    Page<ProjectMember> findByProjectId(
            Long projectId,
            Pageable pageable
    );
}