package com.samdasu.dodoong.domain.auth.security;

import com.samdasu.dodoong.domain.auth.dto.response.TokenResponse;
import com.samdasu.dodoong.global.config.JwtProperties;
import com.samdasu.dodoong.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private static final String LOGIN_ID_CLAIM = "loginId";

    private static final String ACCESS_TOKEN_TYPE = "ACCESS";

    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = jwtProperties.accessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.refreshTokenExpiration();
    }

    public TokenResponse issueTokens(Member member) {
        return new TokenResponse(
                createToken(member, ACCESS_TOKEN_TYPE, accessTokenExpiration),
                createToken(member, REFRESH_TOKEN_TYPE, refreshTokenExpiration)
        );
    }

    public Optional<CustomUserPrincipal> extractAccessTokenPrincipal(String token) {
        try {
            Claims claims = parseClaims(token);

            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                return Optional.empty();
            }

            Long memberId = Long.valueOf(claims.getSubject());

            String loginId = claims.get(LOGIN_ID_CLAIM, String.class);

            return Optional.of(new CustomUserPrincipal(memberId, loginId));
        } catch (
                JwtException |
                IllegalArgumentException exception
        ) {
            return Optional.empty();
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);

            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

            return REFRESH_TOKEN_TYPE.equals(tokenType);
        } catch (
                JwtException |
                IllegalArgumentException exception
        ) {
            return false;
        }
    }

    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(Member member, String tokenType, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .subject(member.getId().toString())
                .claim(LOGIN_ID_CLAIM, member.getLoginId())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
}