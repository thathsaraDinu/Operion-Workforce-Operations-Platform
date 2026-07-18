package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.ProjectCreateRequest;
import com.dinoryn.operion.dto.ProjectResponse;
import com.dinoryn.operion.dto.ProjectUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectService {

    ProjectResponse createProject(ProjectCreateRequest request);

    ProjectResponse getProjectById(Long id);

    Page<ProjectResponse> getAllProjects(Pageable pageable);

    Page<ProjectResponse> getProjectsByEmployee(Long employeeId, Pageable pageable);

    ProjectResponse updateProject(Long id, ProjectUpdateRequest request);

    void deleteProject(Long id);
}