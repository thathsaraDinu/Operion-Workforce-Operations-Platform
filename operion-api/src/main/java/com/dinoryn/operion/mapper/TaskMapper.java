package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.TaskResponse;
import com.dinoryn.operion.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {


    public TaskResponse toResponse(Task task) {

        TaskResponse response = new TaskResponse();

        response.setId(task.getId());

        response.setTitle(task.getTitle());

        response.setDescription(task.getDescription());

        response.setPriority(task.getPriority());

        response.setStatus(task.getStatus());

        response.setCreatedDate(task.getCreatedDate());

        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());


        response.setProjectId(
                task.getProject().getId()
        );

        response.setProjectName(
                task.getProject().getName()
        );


        response.setAssignedEmployeeId(
                task.getAssignedEmployee().getId()
        );

        response.setAssignedEmployeeName(
                task.getAssignedEmployee().getFirstName()
                        + " "
                        + task.getAssignedEmployee().getLastName()
        );


        return response;
    }
}