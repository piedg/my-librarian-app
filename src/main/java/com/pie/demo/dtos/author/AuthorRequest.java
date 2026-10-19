package com.pie.demo.dtos.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
                @NotBlank (message = "Il nome è obbligatorio") @Size (max = 100, message = "Il nome può avere al massimo 100 caratteri") String name,
                @NotBlank (message = "Il cognome è obbligatorio") @Size (max = 100, message = "Il cognome può avere al massimo 100 caratteri") String surname,
                @NotNull  (message = "La biografia non è inserita") @Size (max = 1000, message = "La biografia può avere al massimo 1000 caratteri") String biography) {
}
