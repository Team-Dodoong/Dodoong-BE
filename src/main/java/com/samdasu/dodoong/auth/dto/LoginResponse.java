package com.samdasu.dodoong.auth.dto;

public record LoginResponse(
        Long id,
        String loginId
) {
    public static LoginResponse of(
            Long id,
            String loginId
    ) {
        return new LoginResponse(id, loginId);
    }
}