package com.pie.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pie.demo.entities.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    public Optional<CategoryEntity> findById(Long id);

    public Optional<CategoryEntity> findByName(String name);

}
