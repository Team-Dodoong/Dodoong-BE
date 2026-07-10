package com.samdasu.dodoong.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}