package com.pie.demo.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pie.demo.dtos.category.CategoryRequest;
import com.pie.demo.dtos.category.CategoryResponse;
import com.pie.demo.exceptions.MissingSearchParameterException;
import com.pie.demo.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping
    public CategoryResponse getCategory(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name) {
        if (name != null) {
            return categoryService.findByName(name);
        }
        if (id != null) {
            return categoryService.findById(id);
        }

        throw new MissingSearchParameterException("Almeno uno dei parametri (name o id) deve essere specificato");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);

        URI location = URI.create("/categories/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
