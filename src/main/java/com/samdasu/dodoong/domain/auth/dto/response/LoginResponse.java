package com.samdasu.dodoong.domain.auth.dto.response;

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