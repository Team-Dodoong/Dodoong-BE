package com.samdasu.dodoong.global.websocket;

import com.samdasu.dodoong.domain.auth.repository.AccessTokenBlacklistRepository;
import com.samdasu.dodoong.domain.auth.security.JwtTokenProvider;
import com.samdasu.dodoong.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompHandshakeInterceptor implements HandshakeInterceptor {

    public static final String MEMBER_ID_ATTRIBUTE = "memberId";

    private final JwtTokenProvider jwtTokenProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String accessToken = resolveAccessToken(servletRequest.getServletRequest());

        if (accessToken == null
                || accessToken.isBlank()
                || accessTokenBlacklistRepository.exists(accessToken)) {
            return false;
        }

        return jwtTokenProvider.extractAccessTokenPrincipal(accessToken)
                .map(principal -> {
                    attributes.put(MEMBER_ID_ATTRIBUTE, principal.memberId());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {

    }

    private String resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> CookieUtil.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
