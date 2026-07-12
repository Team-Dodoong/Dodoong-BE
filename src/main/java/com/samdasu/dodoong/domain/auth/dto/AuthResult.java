package com.samdasu.dodoong.domain.auth.dto;

//서비스-컨트롤러 내부 전달 객체
public record AuthResult(
        Long id,
        String loginId,
        String accessToken,
        String refreshToken
) {
}