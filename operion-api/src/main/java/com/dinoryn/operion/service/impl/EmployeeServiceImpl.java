package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.EmployeeCreateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.dto.EmployeeUpdateRequest;
import com.dinoryn.operion.entity.Department;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.exception.DepartmentNotFoundException;
import com.dinoryn.operion.exception.EmailAlreadyExistsException;
import com.dinoryn.operion.exception.EmployeeNotFoundException;
import com.dinoryn.operion.mapper.EmployeeMapper;
import com.dinoryn.operion.repository.DepartmentRepository;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeResponse saveEmployee(EmployeeCreateRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        if(request.getDepartmentId() != null){

            Department department =
                    departmentRepository.findById(request.getDepartmentId())
                            .orElseThrow(
                                    () -> new DepartmentNotFoundException(
                                            request.getDepartmentId()
                                    )
                            );

            employee.setDepartment(department);
        }

        if(employeeRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        employee.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {

        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            employee.setRole(request.getRole());
        }

        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            employee.setAddress(request.getAddress());
        }

        if (request.getPosition() != null) {
            employee.setPosition(request.getPosition());
        }

        if (request.getJoiningDate() != null) {
            employee.setJoiningDate(request.getJoiningDate());
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse assignDepartment(
            Long employeeId,
            Long departmentId
    ){

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(employeeId)
                        );


        Department department =
                departmentRepository.findById(departmentId)
                        .orElseThrow(
                                () -> new DepartmentNotFoundException(departmentId)
                        );


        employee.setDepartment(department);


        Employee updatedEmployee =
                employeeRepository.save(employee);


        return employeeMapper.toResponse(updatedEmployee);
    }
}