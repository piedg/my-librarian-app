package com.pie.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pie.demo.dtos.author.AuthorRequest;
import com.pie.demo.dtos.author.AuthorResponse;
import com.pie.demo.entities.AuthorEntity;
import com.pie.demo.exceptions.AuthorNotFoundException;
import com.pie.demo.repositories.AuthorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        AuthorEntity saved = authorRepository
                .save(new AuthorEntity(request.name(), request.surname(), request.biography()));

        log.info("Autore creato: id = {}, nome = {}, cognome = {}", saved.getId(), saved.getName(), saved.getSurname());

        return AuthorResponse.from(saved);
    }

    @Transactional
    public AuthorResponse update(Long id, AuthorRequest request) {
        AuthorEntity author = getOrThrow(id);

        author.setName(request.name());
        author.setSurname(request.surname());
        author.setBiography(request.biography());

        log.info("Autore modificato: id = {}", id);

        return AuthorResponse.from(author);
    }

    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new AuthorNotFoundException("id", id);
        }

        authorRepository.deleteById(id);
        log.info("Autore eliminato: id = {}", id);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream().map(AuthorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findByName(String name) {
        return authorRepository.findByNameIgnoreCase(name).stream().map(AuthorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findBySurname(String surname) {
        return authorRepository.findBySurnameIgnoreCase(surname).stream().map(AuthorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return AuthorResponse.from(getOrThrow(id));
    }

    private AuthorEntity getOrThrow(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("id", id));
    }
}