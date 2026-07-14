package com.samdasu.dodoong.domain.auth.security;

import com.samdasu.dodoong.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);

        if (
                accessToken != null &&
                        !accessToken.isBlank() &&
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() == null
        ) {
            setAuthentication(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        jwtTokenProvider
                .extractAccessTokenPrincipal(accessToken)
                .ifPresent(principal -> {
                    UsernamePasswordAuthenticationToken
                            authentication =
                            createAuthentication(principal);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                });
    }

    private UsernamePasswordAuthenticationToken
    createAuthentication(CustomUserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()
        );
    }

    private String resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie ->
                        CookieUtil.ACCESS_TOKEN_COOKIE_NAME
                                .equals(cookie.getName())
                )
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}