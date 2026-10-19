package com.pie.demo.dtos.loan;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "L'utente è obbligatorio") Long userId,
        @NotEmpty(message = "Almeno un libro è obbligatorio") Set<@NotNull Long> bookIds) {
}