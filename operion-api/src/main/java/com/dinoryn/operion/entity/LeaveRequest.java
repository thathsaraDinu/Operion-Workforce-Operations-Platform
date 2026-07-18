package com.dinoryn.operion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class LeaveRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;


    private LocalDate startDate;


    private LocalDate endDate;


    private String reason;


    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;


    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;


    private LocalDate createdDate;
}