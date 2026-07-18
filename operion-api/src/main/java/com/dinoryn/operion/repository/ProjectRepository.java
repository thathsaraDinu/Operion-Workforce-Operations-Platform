package com.dinoryn.operion.repository;

import com.dinoryn.operion.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}