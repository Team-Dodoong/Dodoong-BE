package com.samdasu.dodoong.domain.auth.security;

public record CustomUserPrincipal(
        Long memberId,
        String loginId
) {
}