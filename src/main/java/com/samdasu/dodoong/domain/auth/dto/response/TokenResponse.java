package com.samdasu.dodoong.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}