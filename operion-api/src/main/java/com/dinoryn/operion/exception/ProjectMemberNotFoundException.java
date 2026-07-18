package com.dinoryn.operion.exception;

public class ProjectMemberNotFoundException extends RuntimeException {

    public ProjectMemberNotFoundException(Long id) {
        super("Project member not found with id: " + id);
    }
}