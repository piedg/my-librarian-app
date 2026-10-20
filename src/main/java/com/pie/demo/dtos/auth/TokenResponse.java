package com.pie.demo.dtos.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
