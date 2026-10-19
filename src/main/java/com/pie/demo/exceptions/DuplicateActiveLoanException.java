package com.pie.demo.exceptions;

public class DuplicateActiveLoanException extends RuntimeException {
    public DuplicateActiveLoanException(Long userId, Long bookId) {
        super(String.format("L'utente %d ha già un prestito attivo per il libro %d", userId, bookId));
    }
}
