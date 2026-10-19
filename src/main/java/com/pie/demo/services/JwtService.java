package com.pie.demo.services;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.pie.demo.entities.UserEntity;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final Duration ttl;

    public JwtService(JwtEncoder encoder,
            @Value("${app.security.jwt.expiration-minutes:60}") long minutes) {
        this.encoder = encoder;
        this.ttl = Duration.ofMinutes(minutes);
    }

    public Duration getTtl() {
        return ttl;
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString()) // sub = id utente
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim("role", user.getRole().name()) // ADMIN | MEMBER
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}