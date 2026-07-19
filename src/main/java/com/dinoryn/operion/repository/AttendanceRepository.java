package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(
            Long employeeId,
            LocalDate date
    );

    Page<Attendance> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    List<Attendance> findAllByOrderByDateDesc(Pageable pageable);
}