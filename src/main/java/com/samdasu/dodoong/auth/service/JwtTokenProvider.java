package com.samdasu.dodoong.auth.service;

import com.samdasu.dodoong.auth.dto.TokenResponse;
import com.samdasu.dodoong.global.config.JwtProperties;
import com.samdasu.dodoong.member.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration =
                jwtProperties.accessTokenExpiration();
        this.refreshTokenExpiration =
                jwtProperties.refreshTokenExpiration();
    }

    public TokenResponse issueTokens(Member member) {
        String accessToken = createAccessToken(member);
        String refreshToken = createRefreshToken(member);

        return new TokenResponse(
                accessToken,
                refreshToken
        );
    }

    private String createAccessToken(Member member) {
        Instant now = Instant.now();
        Instant expiration =
                now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .subject(member.getMemberId().toString())
                .claim("loginId", member.getLoginId())
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(Member member) {
        Instant now = Instant.now();
        Instant expiration =
                now.plusMillis(refreshTokenExpiration);

        return Jwts.builder()
                .subject(member.getMemberId().toString())
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
}