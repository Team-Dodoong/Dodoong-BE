package com.samdasu.dodoong.auth.dto;

public record LoginResponse(
        Long memberId,
        String loginId,
        String accessToken,
        String tokenType
) {
    public static LoginResponse of(
            Long memberId,
            String loginId,
            String accessToken
    ) {
        return new LoginResponse(
                memberId,
                loginId,
                accessToken,
                "Bearer"
        );
    }
}