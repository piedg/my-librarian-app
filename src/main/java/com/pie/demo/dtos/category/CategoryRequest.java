package com.pie.demo.dtos.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Il nome è obbligatorio") @Size(max = 200, message = "Il nome può avere al massimo 200 caratteri") String name,
        @NotNull @Size(max = 500, message = "La descrizione può avere al massimo 500 caratteri") String description) {
}
