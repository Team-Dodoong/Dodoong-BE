package com.samdasu.dodoong.auth.util;

import com.samdasu.dodoong.global.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME =
            "refreshToken";

    private final JwtProperties jwtProperties;

    public ResponseCookie createRefreshTokenCookie(
            String refreshToken
    ) {
        return ResponseCookie.from(
                        REFRESH_TOKEN_COOKIE_NAME,
                        refreshToken
                )
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")    //TODO: 배포 후 secure, sameSite 변경 필요
                .path("/")
                .maxAge(Duration.ofMillis(
                        jwtProperties.refreshTokenExpiration()
                ))
                .build();
    }
}