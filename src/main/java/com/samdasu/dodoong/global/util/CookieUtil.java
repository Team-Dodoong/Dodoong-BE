package com.samdasu.dodoong.global.util;

import com.samdasu.dodoong.global.config.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final JwtProperties jwtProperties;

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return createCookie(
                ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                jwtProperties.accessTokenExpiration()
        );
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return createCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                jwtProperties.refreshTokenExpiration()
        );
    }

    public ResponseCookie deleteAccessTokenCookie() {
        return deleteCookie(ACCESS_TOKEN_COOKIE_NAME);
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return deleteCookie(REFRESH_TOKEN_COOKIE_NAME);
    }

    public void addAuthCookies(HttpServletResponse response,
                               String accessToken,
                               String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createAccessTokenCookie(accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createRefreshTokenCookie(refreshToken).toString());
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

    private ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")    //TODO: 배포 후 secure, sameSite 변경 필요
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}