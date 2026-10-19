package com.pie.demo.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pie.demo.dtos.book.BookRequest;
import com.pie.demo.dtos.book.BookResponse;
import com.pie.demo.services.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getBooks(@RequestParam(required = false) String title,
            @RequestParam(defaultValue = "false") boolean available) {

        boolean hasTitle = title != null && !title.isBlank();

        if (hasTitle && available) {
            return bookService.findAvailableByTitle(title);
        }

        if (hasTitle) {
            return bookService.findByTitle(title);
        }

        if (available) {
            return bookService.findAvailable();
        }

        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<BookResponse> getBooksByCategoryId(@PathVariable Long categoryId) {
        return bookService.findByCategoryId(categoryId);
    }

    @GetMapping("/author/{authorId}")
    public List<BookResponse> getBooksByAuthorId(@PathVariable Long authorId) {
        return bookService.findByAuthorId(authorId);
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        BookResponse created = bookService.create(request);

        URI location = URI.create("/books/" + created.id());

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
