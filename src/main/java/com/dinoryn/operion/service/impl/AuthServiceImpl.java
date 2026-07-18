package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.ChangePasswordRequest;
import com.dinoryn.operion.dto.ForgotPasswordRequest;
import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;
import com.dinoryn.operion.dto.ResetPasswordRequest;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.entity.PasswordHistory;
import com.dinoryn.operion.repository.EmployeeRepository;
import com.dinoryn.operion.repository.PasswordHistoryRepository;
import com.dinoryn.operion.security.JwtService;
import com.dinoryn.operion.service.AuthService;
import com.dinoryn.operion.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordHistoryRepository passwordHistoryRepository;


    @Override
    public LoginResponse login(LoginRequest request) {
        Employee employee =
                employeeRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("Employee not found")
                        );

        // Check if account is locked
        if (employee.getAccountLockedUntil() != null && 
            employee.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is temporarily locked due to too many failed login attempts. Please try again later.");
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            // Reset failed login attempts on successful login
            employee.setFailedLoginAttempts(0);
            employee.setAccountLockedUntil(null);
            employeeRepository.save(employee);

            String token =
                    jwtService.generateToken(employee);

            return new LoginResponse(
                    token,
                    employee.getId(),
                    employee.getEmail(),
                    employee.getRole().name()
            );
        } catch (Exception e) {
            // Increment failed login attempts
            employee.setFailedLoginAttempts(employee.getFailedLoginAttempts() + 1);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (employee.getFailedLoginAttempts() >= 5) {
                employee.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            
            employeeRepository.save(employee);
            throw e;
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found with this email"));

        String resetToken = UUID.randomUUID().toString();
        employee.setPasswordResetToken(resetToken);
        employee.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        employeeRepository.save(employee);

        emailService.sendPasswordResetEmail(request.getEmail(), resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Employee employee = employeeRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (employee.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        // Check password history - prevent reuse of last 5 passwords
        List<PasswordHistory> recentPasswords = passwordHistoryRepository
                .findRecentPasswordsByEmployeeId(employee.getId(), LocalDateTime.now().minusDays(365));
        
        for (PasswordHistory history : recentPasswords) {
            if (passwordEncoder.matches(request.getNewPassword(), history.getPasswordHash())) {
                throw new RuntimeException("Cannot reuse a password used in the last year");
            }
        }

        // Save current password to history before changing
        PasswordHistory history = new PasswordHistory();
        history.setEmployee(employee);
        history.setPasswordHash(employee.getPassword());
        history.setCreatedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        // Clean up old password history (keep only last 10)
        List<PasswordHistory> allHistory = passwordHistoryRepository
                .findByEmployeeIdOrderByCreatedAtDesc(employee.getId());
        if (allHistory.size() > 10) {
            List<PasswordHistory> toDelete = allHistory.subList(10, allHistory.size());
            passwordHistoryRepository.deleteAll(toDelete);
        }

        employee.setPassword(newPasswordHash);
        employee.setPasswordResetToken(null);
        employee.setPasswordResetTokenExpiry(null);
        employee.setPasswordVersion(employee.getPasswordVersion() + 1);

        employeeRepository.save(employee);

        // Send password change notification
        emailService.sendPasswordChangedNotification(employee.getEmail());
    }

    @Override
    public void changePassword(Long employeeId, ChangePasswordRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        // Check password history - prevent reuse of last 5 passwords
        List<PasswordHistory> recentPasswords = passwordHistoryRepository
                .findRecentPasswordsByEmployeeId(employee.getId(), LocalDateTime.now().minusDays(365));
        
        for (PasswordHistory history : recentPasswords) {
            if (passwordEncoder.matches(request.getNewPassword(), history.getPasswordHash())) {
                throw new RuntimeException("Cannot reuse a password used in the last year");
            }
        }

        // Save current password to history before changing
        PasswordHistory history = new PasswordHistory();
        history.setEmployee(employee);
        history.setPasswordHash(employee.getPassword());
        history.setCreatedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        // Clean up old password history (keep only last 10)
        List<PasswordHistory> allHistory = passwordHistoryRepository
                .findByEmployeeIdOrderByCreatedAtDesc(employee.getId());
        if (allHistory.size() > 10) {
            List<PasswordHistory> toDelete = allHistory.subList(10, allHistory.size());
            passwordHistoryRepository.deleteAll(toDelete);
        }

        employee.setPassword(newPasswordHash);
        employee.setPasswordVersion(employee.getPasswordVersion() + 1);
        employeeRepository.save(employee);

        // Send password change notification
        emailService.sendPasswordChangedNotification(employee.getEmail());
    }
}