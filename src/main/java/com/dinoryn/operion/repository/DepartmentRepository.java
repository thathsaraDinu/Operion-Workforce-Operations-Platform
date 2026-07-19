package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    long count();
}