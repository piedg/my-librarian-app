package com.pie.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<List<LoanResponse>> reserve(@Valid @RequestBody LoanRequest request) {
        // TODO (ruoli): userId verrà preso dall'utente autenticato, non dal body
        List<LoanResponse> created = loanService.reserve(request.userId(), request.bookIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/pickup")
    public LoanResponse pickUp(@PathVariable Long id) {
        return loanService.pickUp(id);
    }

    @PatchMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return loanService.returnLoan(id);
    }

    @PatchMapping("/{id}/cancel")
    public LoanResponse cancel(@PathVariable Long id) {
        return loanService.cancel(id);
    }

    @GetMapping("/{id}")
    public LoanResponse getLoanById(@PathVariable Long id) {
        return loanService.findById(id);
    }

    @GetMapping
    public List<LoanResponse> getLoans(
            @RequestParam(required = false) Long userId,
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
}