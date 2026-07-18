package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.DepartmentCreateRequest;
import com.dinoryn.operion.dto.DepartmentResponse;
import com.dinoryn.operion.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {


    public Department toEntity(DepartmentCreateRequest request){

        Department department = new Department();

        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());

        return department;
    }


    public DepartmentResponse toResponse(Department department){

        DepartmentResponse response = new DepartmentResponse();

        response.setId(department.getId());
        response.setName(department.getName());
        response.setCode(department.getCode());
        response.setDescription(department.getDescription());
        response.setCreatedAt(department.getCreatedAt());
        response.setUpdatedAt(department.getUpdatedAt());

        return response;
    }
}