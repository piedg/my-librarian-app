package com.pie.demo.exceptions;

public class NoCopiesAvailableException extends RuntimeException {
    public NoCopiesAvailableException(Long bookId) {
        super("Nessuna copia disponibile per il libro con id: " + bookId);
    }
}
