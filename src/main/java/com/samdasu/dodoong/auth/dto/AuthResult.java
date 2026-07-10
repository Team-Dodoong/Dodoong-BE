package com.samdasu.dodoong.auth.dto;

public record AuthResult(
        Long memberId,
        String loginId,
        String accessToken,
        String refreshToken
) {
}