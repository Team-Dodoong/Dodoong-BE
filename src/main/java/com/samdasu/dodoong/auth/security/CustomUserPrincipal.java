package com.samdasu.dodoong.auth.security;

public record CustomUserPrincipal(
        Long memberId,
        String loginId
) {
}