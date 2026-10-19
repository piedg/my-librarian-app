package com.pie.demo.dtos.book;

import java.util.List;

import com.pie.demo.dtos.author.AuthorResponse;
import com.pie.demo.entities.BookEntity;

public record BookResponse(Long id, String isbn, String title, String description, String publisher,
        String publicationYear, Integer totalCopies, Integer availableCopies, Long categoryId,
        List<AuthorResponse> authors) {

    public static BookResponse from(BookEntity entity) {
        return new BookResponse(
                entity.getId(),
                entity.getIsbn(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPublisher(),
                entity.getPublicationYear(),
                entity.getTotalCopies(),
                entity.getAvailableCopies(),
                entity.getCategory().getId(),
                entity.getAuthors().stream().map(AuthorResponse::from).toList());
    }
}