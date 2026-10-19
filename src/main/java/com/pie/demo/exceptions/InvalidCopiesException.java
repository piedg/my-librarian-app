package com.pie.demo.exceptions;

public class InvalidCopiesException extends RuntimeException {
    public InvalidCopiesException(String message) {
        super(message);
    }

    public static InvalidCopiesException belowLoanedCopies(int requestedTotal, int loanedCopies) {
        return new InvalidCopiesException(
                "Impossibile impostare le copie totali a " + requestedTotal
                        + ": ci sono " + loanedCopies + " copie attualmente in prestito");
    }
}
