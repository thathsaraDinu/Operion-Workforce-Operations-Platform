package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.EmployeeCreateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.dto.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface EmployeeService {

    EmployeeResponse saveEmployee(EmployeeCreateRequest request);

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    void deleteEmployee(Long id);

    EmployeeResponse assignDepartment(
            Long employeeId,
            Long departmentId
    );
}

