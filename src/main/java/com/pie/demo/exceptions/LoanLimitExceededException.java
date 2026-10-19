package com.pie.demo.exceptions;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(int max, long current, int requested) {
        super(String.format("Limite di %d prestiti attivi superato (attivi: %d, richiesti: %d)",
                max, current, requested));
    }
}
