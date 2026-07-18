package com.dinoryn.operion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(
            name = "employee_id",
            nullable = false
    )
    private Employee employee;


    private LocalDate date;


    private LocalDateTime clockIn;


    private LocalDateTime clockOut;


    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
}