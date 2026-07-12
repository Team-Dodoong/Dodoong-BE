package com.samdasu.dodoong.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}