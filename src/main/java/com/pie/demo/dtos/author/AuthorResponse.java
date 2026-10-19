package com.pie.demo.dtos.author;

import com.pie.demo.entities.AuthorEntity;

public record AuthorResponse(Long id, String name, String surname, String biography) {

    public static AuthorResponse from(AuthorEntity entity) {
        return new AuthorResponse(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getBiography());
    }
}