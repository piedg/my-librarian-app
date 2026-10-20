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

import com.pie.demo.dtos.author.AuthorRequest;
import com.pie.demo.dtos.author.AuthorResponse;
import com.pie.demo.services.AuthorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorResponse> getAuthors(@RequestParam(required = false) String name,
            @RequestParam(required = false) String surname) {

        if (name != null)
            return authorService.findByName(name);
        if (surname != null)
            return authorService.findBySurname(surname);

        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public AuthorResponse getAuthorById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> addAuthor(
            @Valid @RequestBody AuthorRequest request) {
        AuthorResponse created = authorService.create(request);

        URI location = URI.create("/authors/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public AuthorResponse updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}