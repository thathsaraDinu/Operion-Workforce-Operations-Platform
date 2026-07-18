package com.dinoryn.operion.service;

import com.dinoryn.operion.dto.AttendanceResponse;
import com.dinoryn.operion.dto.AttendanceUpdateRequest;
import com.dinoryn.operion.entity.Attendance;
import com.dinoryn.operion.entity.AttendanceStatus;
import com.dinoryn.operion.entity.Employee;
import com.dinoryn.operion.exception.InvalidAttendanceException;
import com.dinoryn.operion.repository.AttendanceRepository;
import com.dinoryn.operion.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private com.dinoryn.operion.mapper.AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Employee employee;
    private Attendance attendance;
    private AttendanceUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");

        attendance = new Attendance();
        attendance.setId(1L);
        attendance.setEmployee(employee);
        attendance.setDate(LocalDate.now());
        attendance.setClockIn(LocalDateTime.now().withHour(9).withMinute(0));
        attendance.setClockOut(LocalDateTime.now().withHour(17).withMinute(0));
        attendance.setStatus(AttendanceStatus.PRESENT);

        updateRequest = new AttendanceUpdateRequest();
        updateRequest.setClockIn(LocalDateTime.now().withHour(8).withMinute(30));
        updateRequest.setClockOut(LocalDateTime.now().withHour(17).withMinute(30));
    }

    @Test
    void clockIn_ShouldCreateAttendance_WhenNoAttendanceForToday() {
        when(attendanceRepository.findByEmployeeIdAndDate(1L, LocalDate.now()))
            .thenReturn(Optional.empty());
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        AttendanceResponse result = attendanceService.clockIn(employee);

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
        assertEquals(AttendanceStatus.PRESENT, attendance.getStatus());
    }

    @Test
    void clockIn_ShouldThrowException_WhenAttendanceAlreadyExists() {
        when(attendanceRepository.findByEmployeeIdAndDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(attendance));

        assertThrows(InvalidAttendanceException.class, () -> attendanceService.clockIn(employee));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void clockOut_ShouldUpdateAttendance_WhenClockedIn() {
        attendance.setClockOut(null);
        when(attendanceRepository.findByEmployeeIdAndDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(attendance));
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        AttendanceResponse result = attendanceService.clockOut(employee);

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
        assertNotNull(attendance.getClockOut());
    }

    @Test
    void clockOut_ShouldThrowException_WhenNotClockedIn() {
        when(attendanceRepository.findByEmployeeIdAndDate(1L, LocalDate.now()))
            .thenReturn(Optional.empty());

        assertThrows(InvalidAttendanceException.class, () -> attendanceService.clockOut(employee));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void clockOut_ShouldThrowException_WhenAlreadyClockedOut() {
        when(attendanceRepository.findByEmployeeIdAndDate(1L, LocalDate.now()))
            .thenReturn(Optional.of(attendance));

        assertThrows(InvalidAttendanceException.class, () -> attendanceService.clockOut(employee));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void getMyAttendance_ShouldReturnEmployeeAttendance() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance));
        
        when(attendanceRepository.findByEmployeeId(1L, pageable)).thenReturn(attendancePage);
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());

        Page<AttendanceResponse> result = attendanceService.getMyAttendance(employee, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(attendanceRepository).findByEmployeeId(1L, pageable);
    }

    @Test
    void getAllAttendance_ShouldReturnAllAttendance() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance));
        
        when(attendanceRepository.findAll(pageable)).thenReturn(attendancePage);
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());

        Page<AttendanceResponse> result = attendanceService.getAllAttendance(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(attendanceRepository).findAll(pageable);
    }

    @Test
    void getEmployeeAttendance_ShouldReturnEmployeeAttendance_WhenEmployeeExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance));
        
        when(attendanceRepository.findByEmployeeId(1L, pageable)).thenReturn(attendancePage);
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());

        Page<AttendanceResponse> result = attendanceService.getEmployeeAttendance(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(attendanceRepository).findByEmployeeId(1L, pageable);
    }

    @Test
    void updateAttendance_ShouldUpdateClockInAndClockOut_WhenValid() {
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        AttendanceResponse result = attendanceService.updateAttendance(1L, updateRequest);

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
        assertEquals(updateRequest.getClockIn(), attendance.getClockIn());
        assertEquals(updateRequest.getClockOut(), attendance.getClockOut());
    }

    @Test
    void updateAttendance_ShouldUpdateOnlyClockIn_WhenClockOutIsNull() {
        updateRequest.setClockOut(null);
        attendance.setClockOut(null);
        
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        AttendanceResponse result = attendanceService.updateAttendance(1L, updateRequest);

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
        assertEquals(updateRequest.getClockIn(), attendance.getClockIn());
        assertNull(attendance.getClockOut());
    }

    @Test
    void updateAttendance_ShouldUpdateOnlyClockOut_WhenClockInIsNull() {
        updateRequest.setClockIn(null);
        
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(attendanceMapper.toResponse(any())).thenReturn(new AttendanceResponse());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        AttendanceResponse result = attendanceService.updateAttendance(1L, updateRequest);

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
        assertEquals(updateRequest.getClockOut(), attendance.getClockOut());
    }

    @Test
    void updateAttendance_ShouldThrowException_WhenClockOutBeforeClockIn() {
        updateRequest.setClockIn(LocalDateTime.now().withHour(17).withMinute(0));
        updateRequest.setClockOut(LocalDateTime.now().withHour(9).withMinute(0));
        
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        assertThrows(InvalidAttendanceException.class, 
            () -> attendanceService.updateAttendance(1L, updateRequest));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void updateAttendance_ShouldThrowException_WhenAttendanceNotFound() {
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InvalidAttendanceException.class, 
            () -> attendanceService.updateAttendance(999L, updateRequest));
        verify(attendanceRepository, never()).save(any());
    }
}
