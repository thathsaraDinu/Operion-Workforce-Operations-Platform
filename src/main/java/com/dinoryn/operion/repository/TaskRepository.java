package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.Task;
import com.dinoryn.operion.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(
            Long projectId,
            Pageable pageable
    );


    Page<Task> findByAssignedEmployeeId(
            Long employeeId,
            Pageable pageable
    );


    Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    );
}