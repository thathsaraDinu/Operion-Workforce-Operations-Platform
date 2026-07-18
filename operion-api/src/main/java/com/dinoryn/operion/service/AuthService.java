package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.ChangePasswordRequest;
import com.dinoryn.operion.dto.ForgotPasswordRequest;
import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;
import com.dinoryn.operion.dto.ResetPasswordRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(Long employeeId, ChangePasswordRequest request);

}