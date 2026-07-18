package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.TaskCreateRequest;
import com.dinoryn.operion.dto.TaskResponse;
import com.dinoryn.operion.dto.TaskUpdateRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.Project;
import com.dinoryn.operion.entity.Task;
import com.dinoryn.operion.entity.TaskStatus;
import com.dinoryn.operion.exception.EmployeeNotFoundException;
import com.dinoryn.operion.exception.ProjectNotFoundException;
import com.dinoryn.operion.exception.TaskNotFoundException;
import com.dinoryn.operion.mapper.TaskMapper;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.repository.ProjectRepository;
import com.dinoryn.operion.repository.TaskRepository;
import com.dinoryn.operion.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskMapper taskMapper;


    @Override
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));


        Employee employee = employeeRepository.findById(request.getAssignedEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getAssignedEmployeeId()));


        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setCreatedDate(LocalDate.now());

        task.setProject(project);
        task.setAssignedEmployee(employee);


        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }


    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toResponse(task);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(
            Pageable pageable
    ) {

        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponse);
    }


    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));


        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());


        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }


    @Override
    @Transactional
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProject(Long projectId,
                                                Pageable pageable) {
        return taskRepository.findByProjectId(
                        projectId,
                        pageable
                )
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByEmployee(Long employeeId, Pageable pageable) {
        return taskRepository.findByAssignedEmployeeId(
                        employeeId,
                        pageable
                )
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByStatus(TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(
                        status,
                        pageable
                )
                .map(taskMapper::toResponse);
    }
}