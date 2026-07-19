package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.Project;
import com.dinoryn.operion.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    long count();

    long countByStatus(ProjectStatus status);

    long countByMembers_EmployeeId(Long employeeId);

    long countByMembers_EmployeeIdAndStatus(Long employeeId, ProjectStatus status);
}