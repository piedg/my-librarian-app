package com.pie.demo.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
                @Schema(example = "Mario") @NotBlank(message = "Il nome è obbligatorio") String name,
                @Schema(example = "Rossi") @NotBlank(message = "Il cognome è obbligatorio") String surname,
                @Schema(example = "mario@test.it") @NotBlank @Email(message = "Email non valida") String email,
                @Schema(example = "Password123", description = "Da 8 a 72 caratteri") @NotBlank @Size(min = 8, max = 72, message = "La password deve avere tra 8 e 72 caratteri") String password) {
}