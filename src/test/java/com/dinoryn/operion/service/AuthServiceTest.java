package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.Role;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.security.JwtService;
import com.dinoryn.operion.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {


    @Mock
    private AuthenticationManager authenticationManager;


    @Mock
    private JwtService jwtService;


    @Mock
    private EmployeeRepository employeeRepository;


    @Mock
    private Authentication authentication;


    @Mock
    private Employee employee;


    @InjectMocks
    private AuthServiceImpl authService;


    private LoginRequest loginRequest;


    @BeforeEach
    void setUp() {

        loginRequest = new LoginRequest();

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");
    }


    @Test
    void login_ShouldReturnLoginResponse_WhenValidCredentials() {


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        ))
                .thenReturn(authentication);


        when(employeeRepository.findByEmail(
                loginRequest.getEmail()
        ))
                .thenReturn(Optional.of(employee));


        when(employee.getId())
                .thenReturn(1L);

        when(employee.getEmail())
                .thenReturn("john.doe@example.com");

        when(employee.getRole())
                .thenReturn(Role.ADMIN);


        when(jwtService.generateToken(employee))
                .thenReturn("jwt-token");


        LoginResponse result =
                authService.login(loginRequest);


        assertNotNull(result);

        assertEquals(
                "jwt-token",
                result.getToken()
        );

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "john.doe@example.com",
                result.getEmail()
        );

        assertEquals(
                "ADMIN",
                result.getRole()
        );


        verify(jwtService)
                .generateToken(employee);
    }



    @Test
    void login_ShouldThrowException_WhenInvalidCredentials() {


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        ))
                .thenThrow(
                        new BadCredentialsException("Invalid credentials")
                );


        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );


        verify(jwtService, never())
                .generateToken(any());
    }



    @Test
    void login_ShouldUseCorrectEmailAndPassword() {


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        ))
                .thenReturn(authentication);


        when(employeeRepository.findByEmail(
                loginRequest.getEmail()
        ))
                .thenReturn(Optional.of(employee));


        when(employee.getRole())
                .thenReturn(Role.ADMIN);


        when(jwtService.generateToken(employee))
                .thenReturn("jwt-token");


        authService.login(loginRequest);


        verify(authenticationManager)
                .authenticate(
                        argThat(auth -> {

                            UsernamePasswordAuthenticationToken token =
                                    (UsernamePasswordAuthenticationToken) auth;


                            return token.getPrincipal()
                                    .equals("john.doe@example.com")

                                    &&

                                    token.getCredentials()
                                            .equals("password123");
                        })
                );
    }



    @Test
    void login_ShouldGenerateTokenWithEmployee() {


        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        ))
                .thenReturn(authentication);


        when(employeeRepository.findByEmail(
                loginRequest.getEmail()
        ))
                .thenReturn(Optional.of(employee));


        when(employee.getRole())
                .thenReturn(Role.ADMIN);


        when(jwtService.generateToken(employee))
                .thenReturn("jwt-token");


        LoginResponse result =
                authService.login(loginRequest);


        assertNotNull(result);

        assertEquals(
                "jwt-token",
                result.getToken()
        );


        verify(jwtService)
                .generateToken(employee);
    }
}