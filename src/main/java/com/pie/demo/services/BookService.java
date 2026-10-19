package com.pie.demo.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pie.demo.dtos.book.BookRequest;
import com.pie.demo.dtos.book.BookResponse;
import com.pie.demo.entities.AuthorEntity;
import com.pie.demo.entities.BookEntity;
import com.pie.demo.entities.CategoryEntity;
import com.pie.demo.exceptions.AuthorNotFoundException;
import com.pie.demo.exceptions.BookNotFoundException;
import com.pie.demo.exceptions.CategoryNotFoundException;
import com.pie.demo.exceptions.InvalidCopiesException;
import com.pie.demo.exceptions.InvalidLoanStateException;
import com.pie.demo.exceptions.IsbnAlreadyExistsException;
import com.pie.demo.repositories.AuthorRepository;
import com.pie.demo.repositories.BookRepository;
import com.pie.demo.repositories.CategoryRepository;
import com.pie.demo.repositories.LoanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public BookResponse create(BookRequest request) {

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new IsbnAlreadyExistsException(request.isbn());
        }

        CategoryEntity category = getCategoryOrThrow(request.categoryId());
        Set<AuthorEntity> authors = resolveAuthors(request.authorIds());

        BookEntity book = BookEntity.builder()
                .isbn(request.isbn())
                .title(request.title())
                .description(request.description())
                .publisher(request.publisher())
                .publicationYear(request.publicationYear())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .category(category)
                .authors(authors)
                .build();

        BookEntity saved = bookRepository.save(book);

        log.info("Libro creato: id = {}, titolo = {}", saved.getId(), saved.getTitle());

        return BookResponse.from(saved);
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        BookEntity book = getOrThrow(id);

        if (bookRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new IsbnAlreadyExistsException(request.isbn());
        }

        int loanedCopies = book.getTotalCopies() - book.getAvailableCopies();

        if (request.totalCopies() < loanedCopies) {
            throw InvalidCopiesException.belowLoanedCopies(request.totalCopies(), loanedCopies);
        }

        int newAvailable = request.totalCopies() - loanedCopies;

        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setPublisher(request.publisher());
        book.setPublicationYear(request.publicationYear());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(newAvailable);
        book.setCategory(getCategoryOrThrow(request.categoryId()));
        book.setAuthors(resolveAuthors(request.authorIds()));

        log.info("Libro modificato: id = {}", id);

        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("id", id);
        }

        if (loanRepository.existsByBookId(id)) {
            throw new InvalidLoanStateException(
                    "Impossibile eliminare il libro " + id + ": esistono prestiti associati");
        }

        bookRepository.deleteById(id);
        log.info("Libro eliminato: id = {}", id);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAvailable() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAvailableByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCaseAndAvailableCopiesGreaterThan(title, 0).stream()
                .map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("id", categoryId);
        }
        return bookRepository.findByCategoryId(categoryId).stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findByAuthorId(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new AuthorNotFoundException("id", authorId);
        }

        return bookRepository.findByAuthorsId(authorId).stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return BookResponse.from(getOrThrow(id));
    }

    private Set<AuthorEntity> resolveAuthors(Set<Long> authorIds) {
        List<AuthorEntity> found = authorRepository.findAllById(authorIds);

        if (found.size() != authorIds.size()) {
            Set<Long> foundIds = found.stream()
                    .map(AuthorEntity::getId)
                    .collect(Collectors.toSet());

            authorIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .ifPresent(missing -> {
                        throw new AuthorNotFoundException("id", missing);
                    });
        }

        return new HashSet<>(found);
    }

    private BookEntity getOrThrow(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("id", id));
    }

    private CategoryEntity getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("id", id));
    }
}
