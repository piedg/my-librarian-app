package com.pie.demo.dtos.loan;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Richiesta di prenotazione")
public record LoanRequest(
                @NotEmpty(message = "Almeno un libro è obbligatorio") Set<@NotNull Long> bookIds) {
}