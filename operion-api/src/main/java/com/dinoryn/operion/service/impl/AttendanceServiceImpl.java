package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.dto.AttendanceResponse;
import com.dinoryn.operion.dto.AttendanceUpdateRequest;
import com.dinoryn.operion.entity.Attendance;
import com.dinoryn.operion.entity.AttendanceStatus;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.exception.InvalidAttendanceException;
import com.dinoryn.operion.mapper.AttendanceMapper;
import com.dinoryn.operion.repository.AttendanceRepository;
import com.dinoryn.operion.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final AttendanceMapper attendanceMapper;


    @Override
    @Transactional
    public AttendanceResponse clockIn(Employee employee) {

        LocalDate today = LocalDate.now();

        attendanceRepository.findByEmployeeIdAndDate(
                employee.getId(),
                today
        ).ifPresent(attendance -> {
            throw new InvalidAttendanceException(
                    "Attendance already exists for today."
            );
        });


        Attendance attendance = new Attendance();

        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setClockIn(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);


        return attendanceMapper.toResponse(
                attendanceRepository.save(attendance)
        );
    }


    @Override
    @Transactional
    public AttendanceResponse clockOut(Employee employee) {

        Attendance attendance =
                attendanceRepository.findByEmployeeIdAndDate(
                                employee.getId(),
                                LocalDate.now()
                        )
                        .orElseThrow(() ->
                                new InvalidAttendanceException(
                                        "You have not clocked in today."
                                )
                        );


        if (attendance.getClockOut() != null) {
            throw new InvalidAttendanceException(
                    "You have already clocked out."
            );
        }


        attendance.setClockOut(LocalDateTime.now());


        return attendanceMapper.toResponse(
                attendanceRepository.save(attendance)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getMyAttendance(
            Employee employee,
            Pageable pageable
    ) {

        return attendanceRepository
                .findByEmployeeId(
                        employee.getId(),
                        pageable
                )
                .map(attendanceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAllAttendance(
            Pageable pageable
    ) {

        return attendanceRepository
                .findAll(pageable)
                .map(attendanceMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getEmployeeAttendance(
            Long employeeId,
            Pageable pageable
    ) {

        return attendanceRepository
                .findByEmployeeId(
                        employeeId,
                        pageable
                )
                .map(attendanceMapper::toResponse);
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(
            Long attendanceId,
            AttendanceUpdateRequest request
    ) {

        Attendance attendance =
                attendanceRepository.findById(attendanceId)
                        .orElseThrow(() ->
                                new InvalidAttendanceException(
                                        "Attendance record not found."
                                )
                        );


        if (request.getClockIn() != null) {
            attendance.setClockIn(request.getClockIn());
        }


        if (request.getClockOut() != null) {
            attendance.setClockOut(request.getClockOut());
        }


        if (attendance.getClockOut() != null
                && attendance.getClockOut()
                .isBefore(attendance.getClockIn())) {

            throw new InvalidAttendanceException(
                    "Clock out cannot be before clock in."
            );
        }


        return attendanceMapper.toResponse(
                attendanceRepository.save(attendance)
        );
    }
}