package com.pie.demo.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Il nome è obbligatorio") String name,
        @NotBlank(message = "Il cognome è obbligatorio") String surname,
        @NotBlank @Email(message = "Email non valida") String email,
        @NotBlank @Size(min = 8, max = 72, message = "La password deve avere tra 8 e 72 caratteri") String password) {
}