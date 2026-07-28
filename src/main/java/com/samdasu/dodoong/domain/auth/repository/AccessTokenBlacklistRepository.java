package com.samdasu.dodoong.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Repository
@RequiredArgsConstructor
public class AccessTokenBlacklistRepository {

    private static final String KEY_PREFIX = "blacklist:access-token:";

    private final StringRedisTemplate redisTemplate;

    public void save(String accessToken, Duration remainingExpiration) {
        if (remainingExpiration.isZero() || remainingExpiration.isNegative()) {
            return;
        }

        redisTemplate.opsForValue().set(createKey(accessToken), "logout", remainingExpiration);
    }

    public boolean exists(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(createKey(accessToken)));
    }

    private String createKey(String accessToken) {
        return KEY_PREFIX + hash(accessToken);
    }

    private String hash(String accessToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(accessToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", exception);
        }
    }
}