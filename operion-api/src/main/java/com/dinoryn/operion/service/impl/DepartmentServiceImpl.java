package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.DepartmentCreateRequest;
import com.dinoryn.operion.dto.DepartmentResponse;
import com.dinoryn.operion.dto.DepartmentUpdateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.entity.Department;
import com.dinoryn.operion.exception.DepartmentHasEmployeesException;
import com.dinoryn.operion.exception.DepartmentNotFoundException;
import com.dinoryn.operion.mapper.DepartmentMapper;
import com.dinoryn.operion.mapper.EmployeeMapper;
import com.dinoryn.operion.repository.DepartmentRepository;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {


    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;


    @Override
    @Transactional
    public DepartmentResponse saveDepartment(
            DepartmentCreateRequest request
    ){

        Department department =
                departmentMapper.toEntity(request);

        Department savedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(savedDepartment);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(
            Pageable pageable
    ) {

        return departmentRepository.findAll(pageable)
                .map(department -> {

                    DepartmentResponse response =
                            departmentMapper.toResponse(department);

                    response.setEmployeeCount(
                            employeeRepository.countByDepartmentId(
                                    department.getId()
                            )
                    );

                    return response;
                });
    }


    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id){

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(
                                () -> new DepartmentNotFoundException(id)
                        );

        DepartmentResponse response =
                departmentMapper.toResponse(department);

        response.setEmployeeCount(
                employeeRepository.countByDepartmentId(id)
        );

        return response;
    }


    @Override
    @Transactional
    public DepartmentResponse updateDepartment(
            Long id,
            DepartmentUpdateRequest request
    ){

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(
                                () -> new DepartmentNotFoundException(id)
                        );


        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());


        Department updatedDepartment =
                departmentRepository.save(department);


        return departmentMapper.toResponse(updatedDepartment);
    }


    @Override
    @Transactional
    public void deleteDepartment(Long id){

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(
                                () -> new DepartmentNotFoundException(id)
                        );


        if (employeeRepository.existsByDepartmentId(id)) {
            throw new DepartmentHasEmployeesException(id);
        }

        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployeesByDepartment(
            Long departmentId,
            Pageable pageable
    ) {

        departmentRepository.findById(departmentId)
                .orElseThrow(
                        () -> new DepartmentNotFoundException(departmentId)
                );


        return employeeRepository
                .findByDepartmentId(
                        departmentId,
                        pageable
                )
                .map(employeeMapper::toResponse);
    }
}