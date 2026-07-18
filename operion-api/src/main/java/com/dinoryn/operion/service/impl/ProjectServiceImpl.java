package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.ProjectCreateRequest;
import com.dinoryn.operion.dto.ProjectResponse;
import com.dinoryn.operion.dto.ProjectUpdateRequest;
import com.dinoryn.operion.entity.Project;
import com.dinoryn.operion.exception.ProjectNotFoundException;
import com.dinoryn.operion.mapper.ProjectMapper;
import com.dinoryn.operion.repository.ProjectRepository;
import com.dinoryn.operion.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


    @Override
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {

        Project project = projectMapper.toEntity(request);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }


    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        return projectMapper.toResponse(project);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(
            Pageable pageable
    ) {

        return projectRepository.findAll(pageable)
                .map(projectMapper::toResponse);
    }


    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));


        if (request.getName() != null) {
            project.setName(request.getName());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }

        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }


        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponse(updatedProject);
    }


    @Override
    @Transactional
    public void deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        projectRepository.delete(project);
    }
}