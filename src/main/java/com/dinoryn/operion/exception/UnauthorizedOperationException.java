package com.dinoryn.operion.exception;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException() {
        super("You are not authorized to perform this operation.");
    }
}