package com.samdasu.dodoong.auth.dto;

import com.samdasu.dodoong.member.domain.Member;

public record SignupResponse(
        Long memberId,
        String loginId,
        String accessToken,
        String tokenType
) {
    public static SignupResponse of(
            Long memberId,
            String loginId,
            String accessToken
    ) {
        return new SignupResponse(
                memberId,
                loginId,
                accessToken,
                "Bearer"
        );
    }
}