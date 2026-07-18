package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.LoginRequest;
import com.dinoryn.operion.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}