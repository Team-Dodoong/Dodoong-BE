package com.samdasu.dodoong.domain.auth.repository;

import com.samdasu.dodoong.global.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    public void save(Long memberId, String refreshToken) {
        String key = createKey(memberId);

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(jwtProperties.refreshTokenExpiration())
        );
    }

    public Optional<String> findByMemberId(Long memberId) {
        String refreshToken = redisTemplate.opsForValue().get(createKey(memberId));

        return Optional.ofNullable(refreshToken);
    }

    public boolean matches(Long memberId, String refreshToken) {
        return findByMemberId(memberId)
                .map(savedToken -> savedToken.equals(refreshToken))
                .orElse(false);
    }

    public void deleteByMemberId(Long memberId) {
        redisTemplate.delete(createKey(memberId));
    }

    private String createKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}