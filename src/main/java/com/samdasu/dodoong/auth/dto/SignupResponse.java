package com.samdasu.dodoong.auth.dto;

public record SignupResponse(
        Long id,
        String loginId
) {
    public static SignupResponse of(
            Long id,
            String loginId
    ) {
        return new SignupResponse(id, loginId);
    }
}