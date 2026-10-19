package com.pie.demo.exceptions;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String fieldName, Object value) {
        super(String.format("Prestito non trovato con %s: %s", fieldName, value));
    }
}
