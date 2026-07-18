package com.dinoryn.operion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;


    private String description;


    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;


    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;


    private LocalDate createdDate;


    @ManyToOne
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;


    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;
}