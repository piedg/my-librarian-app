package com.pie.demo.dtos.category;

import com.pie.demo.entities.CategoryEntity;

public record CategoryResponse(Long id, String name, String description) {

    public static CategoryResponse from(CategoryEntity entity) {
        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription());
    }

}
