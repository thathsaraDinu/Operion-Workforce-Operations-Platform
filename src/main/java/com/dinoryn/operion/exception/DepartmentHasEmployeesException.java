package com.dinoryn.operion.exception;

public class DepartmentHasEmployeesException extends RuntimeException {

    public DepartmentHasEmployeesException(Long id) {
        super("Cannot delete department with id " + id +
                " because employees are assigned to it.");
    }
}