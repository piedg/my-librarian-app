package com.pie.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pie.demo.dtos.auth.LoginRequest;
import com.pie.demo.dtos.auth.RegisterRequest;
import com.pie.demo.dtos.auth.TokenResponse;
import com.pie.demo.entities.UserEntity;
import com.pie.demo.enums.Role;
import com.pie.demo.exceptions.EmailAlreadyExistsException;
import com.pie.demo.exceptions.InvalidCredentialsException;
import com.pie.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.MEMBER); // SEMPRE MEMBER: il ruolo non arriva dal client
        // se hai un campo "state" (es. ACTIVE), impostalo qui

        UserEntity saved = userRepository.save(user);
        log.info("Utente registrato: id = {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        return toResponse(user);
    }

    private TokenResponse toResponse(UserEntity user) {
        return new TokenResponse(jwtService.generateToken(user), "Bearer",
                jwtService.getTtl().toSeconds());
    }
}