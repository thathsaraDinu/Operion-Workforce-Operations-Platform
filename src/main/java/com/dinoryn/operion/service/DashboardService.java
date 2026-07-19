package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.DashboardResponse;
import com.dinoryn.operion.entity.Role;

public interface DashboardService {
    DashboardResponse getDashboard(Role role, Long employeeId);
}
