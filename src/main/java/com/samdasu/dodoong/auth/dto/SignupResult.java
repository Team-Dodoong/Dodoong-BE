package com.samdasu.dodoong.auth.dto;

public record SignupResult(
        Long memberId,
        String loginId,
        String accessToken,
        String refreshToken
) {
}