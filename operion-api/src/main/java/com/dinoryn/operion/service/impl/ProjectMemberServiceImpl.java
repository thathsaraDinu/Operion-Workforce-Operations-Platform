package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.ProjectMemberCreateRequest;
import com.dinoryn.operion.dto.ProjectMemberResponse;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.Project;
import com.dinoryn.operion.entity.ProjectMember;
import com.dinoryn.operion.exception.EmployeeNotFoundException;
import com.dinoryn.operion.exception.ProjectMemberNotFoundException;
import com.dinoryn.operion.exception.ProjectNotFoundException;
import com.dinoryn.operion.mapper.ProjectMemberMapper;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.repository.ProjectMemberRepository;
import com.dinoryn.operion.repository.ProjectRepository;
import com.dinoryn.operion.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectMemberMapper projectMemberMapper;


    @Override
    @Transactional
    public ProjectMemberResponse addMember(
            Long projectId,
            ProjectMemberCreateRequest request
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));


        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));


        ProjectMember member = new ProjectMember();

        member.setProject(project);
        member.setEmployee(employee);
        member.setProjectRole(request.getProjectRole());
        member.setAssignedDate(LocalDate.now());


        ProjectMember savedMember =
                projectMemberRepository.save(member);


        return projectMemberMapper.toResponse(savedMember);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberResponse> getProjectMembers(
            Long projectId,
            Pageable pageable
    ) {

        projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new ProjectNotFoundException(projectId)
                );


        return projectMemberRepository
                .findByProjectId(
                        projectId,
                        pageable
                )
                .map(projectMemberMapper::toResponse);
    }


    @Override
    @Transactional
    public void removeMember(
            Long projectId,
            Long memberId
    ) {

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(
                        () -> new ProjectMemberNotFoundException(memberId)
                );


        if (!member.getProject().getId().equals(projectId)) {
            throw new ProjectMemberNotFoundException(memberId);
        }


        projectMemberRepository.delete(member);
    }
}