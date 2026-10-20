package com.pie.demo.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email o password non corretti");
    }
}
