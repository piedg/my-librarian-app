package com.pie.demo.exceptions;

public class InvalidLoanStateException extends RuntimeException {
    public InvalidLoanStateException(String message) {
        super(message);
    }
}
