package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.security.JwtService;
import com.dinoryn.operion.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;


    @Override
    public LoginResponse login(LoginRequest request) {


        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );


        Employee employee =
                employeeRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("Employee not found")
                        );


        String token =
                jwtService.generateToken(employee);


        return new LoginResponse(
                token,
                employee.getId(),
                employee.getEmail(),
                employee.getRole().name()
        );
    }
}