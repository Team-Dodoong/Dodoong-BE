package com.samdasu.dodoong.auth.repository;

import com.samdasu.dodoong.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long id);

    Optional<RefreshToken> findByToken(String token);

    void deleteByMemberId(Long id);
}