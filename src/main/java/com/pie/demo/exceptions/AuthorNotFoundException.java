package com.pie.demo.exceptions;

public class AuthorNotFoundException extends RuntimeException {

    public AuthorNotFoundException(String fieldName, Object value) {
        super(String.format("Autore non trovato con %s: %s", fieldName, value));
    }
}