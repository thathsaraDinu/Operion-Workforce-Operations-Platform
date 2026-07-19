package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.LeaveRequest;
import com.dinoryn.operion.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    Page<LeaveRequest> findByStatus(
            LeaveStatus status,
            Pageable pageable
    );

    Optional<LeaveRequest> findByIdAndEmployeeId(
            Long leaveRequestId,
            Long employeeId
    );

    long count();

    List<LeaveRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(LeaveStatus status);

    long countByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
}