package com.pie.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pie.demo.dtos.loan.LoanRequest;
import com.pie.demo.dtos.loan.LoanResponse;
import com.pie.demo.enums.LoanState;
import com.pie.demo.services.LoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // Il MEMBER prenota per sé: l'id viene dal token, non dal client
    @PostMapping
    public ResponseEntity<List<LoanResponse>> reserve(@Valid @RequestBody LoanRequest request,
            Authentication auth) {
        List<LoanResponse> created = loanService.reserve(currentUserId(auth), request.bookIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/pickup")
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponse pickUp(@PathVariable Long id) {
        return loanService.pickUp(id);
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    @PatchMapping("/{id}/cancel")
    public LoanResponse cancel(@PathVariable Long id, Authentication auth) {
        return loanService.cancel(id, currentUserId(auth), isAdmin(auth));
    }

    @GetMapping("/{id}")
    public LoanResponse getLoanById(@PathVariable Long id, Authentication auth) {
        return loanService.findById(id, currentUserId(auth), isAdmin(auth));
    }

    // I miei prestiti
    @GetMapping("/me")
    public List<LoanResponse> getMyLoans(@RequestParam(required = false) LoanState state,
            Authentication auth) {
        Long userId = currentUserId(auth);
        return state != null
                ? loanService.findByUserAndState(userId, state)
                : loanService.findByUser(userId);
    }

    // Tutti i prestiti, con filtri: solo admin
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanResponse> getLoans(@RequestParam(required = false) Long userId,
            @RequestParam(required = false) LoanState state) {
        if (userId != null && state != null) {
            return loanService.findByUserAndState(userId, state);
        }
        if (userId != null) {
            return loanService.findByUser(userId);
        }
        if (state != null) {
            return loanService.findByState(state);
        }
        return loanService.findAll();
    }

    private Long currentUserId(Authentication auth) {
        return Long.valueOf(auth.getName());
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}