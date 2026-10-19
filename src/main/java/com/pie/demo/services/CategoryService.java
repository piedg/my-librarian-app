package com.pie.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pie.demo.dtos.category.CategoryRequest;
import com.pie.demo.dtos.category.CategoryResponse;
import com.pie.demo.entities.CategoryEntity;
import com.pie.demo.exceptions.CategoryNotFoundException;
import com.pie.demo.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        CategoryEntity saved = categoryRepository
                .save(new CategoryEntity(request.name(), request.description()));

        log.info("Categoria creata: id = {}, nome = {}", saved.getId(), saved.getName());

        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        CategoryEntity category = getOrThrow(id);

        category.setName(request.name());
        category.setDescription(request.description());

        log.info("Categoria modificata: id = {}", id);

        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("id", id);
        }

        categoryRepository.deleteById(id);
        log.info("Categoria eliminata: id = {}", id);
    }

    @Transactional(readOnly = true)
    public java.util.List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findByName(String name) {
        return CategoryResponse.from(categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException("name", name)));
    }

    private CategoryEntity getOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("id", id));
    }
}