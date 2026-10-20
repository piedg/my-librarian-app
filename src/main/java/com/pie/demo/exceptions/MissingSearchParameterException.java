package com.pie.demo.exceptions;

public class MissingSearchParameterException extends RuntimeException {
    public MissingSearchParameterException(String message) {
        super(message);
    }
}
