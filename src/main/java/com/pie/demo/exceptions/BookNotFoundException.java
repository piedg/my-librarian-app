package com.pie.demo.exceptions;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String fieldName, Object value) {
        super(String.format("Libro non trovato con %s: %s", fieldName, value));
    }
}