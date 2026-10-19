package com.pie.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pie.demo.entities.AuthorEntity;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    List<AuthorEntity> findByNameIgnoreCase(String name);

    List<AuthorEntity> findBySurnameIgnoreCase(String surname);
}
