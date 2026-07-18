package com.dinoryn.operion.security;

import com.dinoryn.operion.dto.ErrorResponse;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.repository.EmployeeRepository;
import tools.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final EmployeeUserDetailsService userDetailsService;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/login") ||
               path.equals("/api/auth/forgot-password") ||
               path.equals("/api/auth/reset-password");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader(AUTH_HEADER);


        if (!hasValidBearerToken(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwt = authHeader.substring(BEARER_PREFIX.length());


        try {

            authenticateUser(jwt);

        } catch (ExpiredJwtException e) {

            sendErrorResponse(
                    response,
                    401,
                    "Unauthorized",
                    "JWT token expired"
            );

            return;

        } catch (JwtException e) {

            sendErrorResponse(
                    response,
                    401,
                    "Unauthorized",
                    "Invalid JWT token"
            );

            return;

        } catch (Exception e) {

            sendErrorResponse(
                    response,
                    401,
                    "Unauthorized",
                    "Authentication failed"
            );

            return;
        }


        filterChain.doFilter(request, response);
    }


    private void authenticateUser(String jwt) {


        String username = jwtService.extractUsername(jwt);


        if (username == null ||
                SecurityContextHolder.getContext()
                        .getAuthentication() != null) {

            return;
        }


        Employee employee =
                employeeRepository.findByEmail(username)
                        .orElseThrow();


        if (jwtService.isTokenValid(jwt, employee)) {


            var userDetails =
                    userDetailsService.loadUserByUsername(username);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );


            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
    }


    private boolean hasValidBearerToken(String authHeader) {

        return authHeader != null &&
                authHeader.startsWith(BEARER_PREFIX);
    }


    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message
    ) throws IOException {


        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(),
                        status,
                        error,
                        message
                );


        response.setStatus(status);
        response.setContentType("application/json");


        response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
    }
}