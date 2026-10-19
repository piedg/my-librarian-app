package com.pie.demo.exceptions;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String fieldName, Object value) {
        super(String.format("Categoria non trovato con %s: %s", fieldName, value));
    }
}