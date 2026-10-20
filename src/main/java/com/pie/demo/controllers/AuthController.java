package com.pie.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pie.demo.dtos.auth.LoginRequest;
import com.pie.demo.dtos.auth.RegisterRequest;
import com.pie.demo.dtos.auth.TokenResponse;
import com.pie.demo.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticazione", description = "Registrazione e login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registra un nuovo utente", description = "Crea sempre un utente con ruolo MEMBER e restituisce subito un token.", security = {})
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Esegui il login come Admin o Membro", security = {})
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
