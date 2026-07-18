package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.EmployeeCreateRequest;
import com.dinoryn.operion.dto.EmployeeResponse;
import com.dinoryn.operion.dto.EmployeeUpdateRequest;
import com.dinoryn.operion.entity.Department;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.EmployeeStatus;
import com.dinoryn.operion.entity.Role;
import com.dinoryn.operion.exception.DepartmentNotFoundException;
import com.dinoryn.operion.exception.EmailAlreadyExistsException;
import com.dinoryn.operion.exception.EmployeeNotFoundException;
import com.dinoryn.operion.repository.DepartmentRepository;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.dinoryn.operion.mapper.EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;
    private EmployeeCreateRequest createRequest;
    private EmployeeUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPassword("encodedPassword");
        employee.setRole(Role.EMPLOYEE);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartment(department);

        createRequest = new EmployeeCreateRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPassword("password123");
        createRequest.setRole(Role.EMPLOYEE);
        createRequest.setDepartmentId(1L);

        updateRequest = new EmployeeUpdateRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setEmail("jane.doe@example.com");
    }

    @Test
    void saveEmployee_ShouldReturnEmployeeResponse_WhenValidRequest() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(employeeMapper.toEntity(any())).thenReturn(employee);
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        EmployeeResponse result = employeeService.saveEmployee(createRequest);

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void saveEmployee_ShouldThrowException_WhenDepartmentNotFound() {
        createRequest.setDepartmentId(999L);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.saveEmployee(createRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_ShouldThrowException_WhenEmailAlreadyExists() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.saveEmployee(createRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_ShouldSaveWithoutDepartment_WhenDepartmentIdIsNull() {
        createRequest.setDepartmentId(null);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(employeeMapper.toEntity(any())).thenReturn(employee);
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        EmployeeResponse result = employeeService.saveEmployee(createRequest);

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
        verify(departmentRepository, never()).findById(any());
    }

    @Test
    void getEmployeeById_ShouldReturnEmployeeResponse_WhenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        EmployeeResponse result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        verify(employeeRepository).findById(1L);
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
    }

    @Test
    void updateEmployee_ShouldUpdateEmployee_WhenValidRequest() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        EmployeeResponse result = employeeService.updateEmployee(1L, updateRequest);

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
        assertEquals("Jane", employee.getFirstName());
        assertEquals("jane.doe@example.com", employee.getEmail());
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(999L, updateRequest));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployee_WhenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(any());

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(999L));
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void assignDepartment_ShouldAssignDepartment_WhenBothExist() {
        Department newDepartment = new Department();
        newDepartment.setId(2L);
        newDepartment.setName("HR");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDepartment));
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        EmployeeResponse result = employeeService.assignDepartment(1L, 2L);

        assertNotNull(result);
        verify(employeeRepository).save(employee);
        assertEquals(newDepartment, employee.getDepartment());
    }

    @Test
    void assignDepartment_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.assignDepartment(999L, 1L));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void assignDepartment_ShouldThrowException_WhenDepartmentNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.assignDepartment(1L, 999L));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getAllEmployees_ShouldReturnPageOfEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toResponse(any())).thenReturn(new EmployeeResponse());

        Page<EmployeeResponse> result = employeeService.getAllEmployees(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository).findAll(pageable);
    }
}
