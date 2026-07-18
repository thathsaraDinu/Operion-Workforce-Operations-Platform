package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.employee.id = :employeeId AND ph.createdAt > :since ORDER BY ph.createdAt DESC")
    List<PasswordHistory> findRecentPasswordsByEmployeeId(@Param("employeeId") Long employeeId, @Param("since") LocalDateTime since);

    boolean existsByEmployeeIdAndPasswordHash(Long employeeId, String passwordHash);
}
