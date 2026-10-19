package com.pie.demo.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email già registrata: " + email);
    }
}
