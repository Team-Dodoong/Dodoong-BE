package com.samdasu.dodoong.domain.auth.repository;

import com.samdasu.dodoong.global.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    public void save(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(
                createKey(memberId),
                hash(refreshToken),
                Duration.ofMillis(
                        jwtProperties.refreshTokenExpiration()
                )
        );
    }

    public Optional<String> findByMemberId(Long memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(createKey(memberId)));
    }

    public boolean matches(Long memberId, String refreshToken) {
        String inputTokenHash = hash(refreshToken);

        return findByMemberId(memberId)
                .map(savedTokenHash -> MessageDigest.isEqual(savedTokenHash.getBytes(StandardCharsets.UTF_8),
                                inputTokenHash.getBytes(StandardCharsets.UTF_8)))
                .orElse(false);
    }

    public void deleteByMemberId(Long memberId) {
        redisTemplate.delete(createKey(memberId));
    }

    private String createKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }

    private String hash(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] tokenHash = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(tokenHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}