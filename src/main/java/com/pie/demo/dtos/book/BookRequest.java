package com.pie.demo.dtos.book;

import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
                @NotBlank(message = "Il titolo è obbligatorio") @Size(max = 200, message = "Il titolo può avere al massimo 200 caratteri") String title,
                @NotBlank @Size(max = 13, message = "L'ISBN può avere al massimo 13 caratteri") String isbn,
                @NotNull @Size(max = 1000, message = "La descrizione può avere al massimo 1000 caratteri") String description,
                @NotNull @Size(max = 100, message = "L'editore può avere al massimo 100 caratteri") String publisher,
                @NotNull @Size(max = 4, message = "L'anno di pubblicazione può avere al massimo 4 caratteri") String publicationYear,
                @NotNull @Min(value = 0, message = "Il numero di copie totali non può essere negativo") Integer totalCopies,
                @NotNull Long categoryId,
                @NotEmpty(message = "Almeno un autore è obbligatorio") Set<Long> authorIds) {
}
