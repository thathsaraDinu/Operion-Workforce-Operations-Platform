package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.DepartmentCreateRequest;
import com.dinoryn.operion.dto.DepartmentResponse;
import com.dinoryn.operion.dto.DepartmentUpdateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponse saveDepartment(
            DepartmentCreateRequest request
    );


    Page<DepartmentResponse> getAllDepartments(
            Pageable pageable
    );


    DepartmentResponse getDepartmentById(
            Long id
    );


    DepartmentResponse updateDepartment(
            Long id,
            DepartmentUpdateRequest request
    );


    void deleteDepartment(
            Long id
    );


    Page<EmployeeResponse> getEmployeesByDepartment(
            Long departmentId,
            Pageable pageable
    );
}