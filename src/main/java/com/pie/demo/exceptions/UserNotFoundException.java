package com.pie.demo.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String fieldName, Object value) {
        super(String.format("Utente non trovato con %s: %s", fieldName, value));
    }
}
