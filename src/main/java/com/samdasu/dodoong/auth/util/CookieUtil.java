package com.samdasu.dodoong.auth.util;

import com.samdasu.dodoong.global.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE_NAME =
            "accessToken";

    public static final String REFRESH_TOKEN_COOKIE_NAME =
            "refreshToken";

    private final JwtProperties jwtProperties;

    public ResponseCookie createAccessTokenCookie(
            String accessToken
    ) {
        return createCookie(
                ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                jwtProperties.accessTokenExpiration()
        );
    }

    public ResponseCookie createRefreshTokenCookie(
            String refreshToken
    ) {
        return createCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                jwtProperties.refreshTokenExpiration()
        );
    }

    private ResponseCookie createCookie(
            String name,
            String value,
            long expirationMillis
    ) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")        //TODO: 배포 후 secure, sameSite 변경 필요
                .path("/")
                .maxAge(Duration.ofMillis(expirationMillis))
                .build();
    }
}