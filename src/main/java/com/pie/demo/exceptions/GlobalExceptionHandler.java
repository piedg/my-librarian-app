package com.pie.demo.exceptions;

import org.springframework.security.access.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ---------- 400: validazione @Valid ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));

        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Dati non validi",
                "Uno o più campi non sono validi");
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(MissingSearchParameterException.class)
    public ProblemDetail handleMissingSearchParameter(MissingSearchParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parametri mancanti", ex.getMessage());
    }

    // ---------- 401 - 403: autenticazione e permessi ----------

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Credenziali non valide", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Accesso negato", "Non hai i permessi per questa operazione");
    }

    // -------- 404: Risorse non trovate --------
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Utente non trovato", ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail handleBookNotFound(BookNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Libro non trovato", ex.getMessage());
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ProblemDetail handleAuthorNotFound(AuthorNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Autore non trovato", ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Categoria non trovata", ex.getMessage());
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ProblemDetail handleLoanNotFound(LoanNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Prestito non trovato", ex.getMessage());
    }

    // -------- 409: Conflitti con lo stato attuale --------

    @ExceptionHandler(NoCopiesAvailableException.class)
    public ProblemDetail handleNoCopies(NoCopiesAvailableException ex) {
        return build(HttpStatus.CONFLICT, "Copie esaurite", ex.getMessage());
    }

    @ExceptionHandler(DuplicateActiveLoanException.class)
    public ProblemDetail handleDuplicateLoan(DuplicateActiveLoanException ex) {
        return build(HttpStatus.CONFLICT, "Prestito già attivo", ex.getMessage());
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    public ProblemDetail handleLoanLimit(LoanLimitExceededException ex) {
        return build(HttpStatus.CONFLICT, "Limite prestiti superato", ex.getMessage());
    }

    @ExceptionHandler(InvalidLoanStateException.class)
    public ProblemDetail handleInvalidState(InvalidLoanStateException ex) {
        return build(HttpStatus.CONFLICT, "Operazione non consentita", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violazione di integrità dei dati", ex);
        return build(HttpStatus.CONFLICT, "Operazione non consentita",
                "L'operazione viola un vincolo di integrità dei dati");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailExists(EmailAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "Email già registrata", ex.getMessage());
    }

    @ExceptionHandler(IsbnAlreadyExistsException.class)
    public ProblemDetail handleIsbnExists(IsbnAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "ISBN già esistente", ex.getMessage());
    }

    @ExceptionHandler(InvalidCopiesException.class)
    public ProblemDetail handleInvalidCopies(InvalidCopiesException ex) {
        return build(HttpStatus.CONFLICT, "Copie non valide", ex.getMessage());
    }

    // ---------- 500: rete di sicurezza ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Errore imprevisto", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno",
                "Si è verificato un errore imprevisto");
    }

    private ProblemDetail build(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }

}