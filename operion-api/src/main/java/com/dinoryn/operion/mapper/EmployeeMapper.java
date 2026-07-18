package com.dinoryn.operion.mapper;

import com.dinoryn.operion.dto.EmployeeCreateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request) {
        Employee employee = new Employee();

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setPosition(request.getPosition());
        employee.setJoiningDate(request.getJoiningDate());

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        response.setPhone(employee.getPhone());
        response.setAddress(employee.getAddress());
        response.setPosition(employee.getPosition());
        response.setJoiningDate(employee.getJoiningDate());
        response.setStatus(employee.getStatus());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());

        if(employee.getDepartment() != null){

            response.setDepartmentId(
                    employee.getDepartment().getId()
            );

            response.setDepartmentName(
                    employee.getDepartment().getName()
            );
        }

        return response;
    }
}