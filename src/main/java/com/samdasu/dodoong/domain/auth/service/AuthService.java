package com.samdasu.dodoong.domain.auth.service;

import com.samdasu.dodoong.domain.auth.dto.AuthResult;
import com.samdasu.dodoong.domain.auth.dto.request.LoginRequest;
import com.samdasu.dodoong.domain.auth.dto.request.SignupRequest;
import com.samdasu.dodoong.domain.auth.dto.response.TokenResponse;
import com.samdasu.dodoong.domain.auth.repository.AccessTokenBlacklistRepository;
import com.samdasu.dodoong.domain.auth.repository.RefreshTokenRepository;
import com.samdasu.dodoong.domain.auth.security.JwtTokenProvider;
import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import com.samdasu.dodoong.domain.character.repository.CharacterItemRepository;
import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CharacterItemRepository characterItemRepository;
    private final MemberCharacterRepository memberCharacterRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    private static final Long DEFAULT_CHARACTER_ID = 1L;

    @Transactional
    public AuthResult signup(SignupRequest request) {
        validateDuplicateLoginId(request.loginId());

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .loginId(request.loginId())
                .encodedPassword(encodedPassword)
                .build();

        Member savedMember = memberRepository.save(member);

        //기본 캐릭터 지급
        CharacterItem defaultCharacter = characterItemRepository.findById(DEFAULT_CHARACTER_ID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        MemberCharacter memberCharacter = new MemberCharacter(savedMember, defaultCharacter);

        memberCharacter.equip();
        memberCharacterRepository.save(memberCharacter);
        TokenResponse tokenResponse = jwtTokenProvider.issueTokens(savedMember);

        saveRefreshToken(savedMember.getId(), tokenResponse.refreshToken());
        return createAuthResult(savedMember, tokenResponse);
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        Member member = memberRepository
                .findByLoginId(request.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        TokenResponse tokenResponse = jwtTokenProvider.issueTokens(member);

        saveRefreshToken(member.getId(), tokenResponse.refreshToken());

        return createAuthResult(member, tokenResponse);
    }

    @Transactional
    public void logout(Long authenticatedMemberId, String accessToken, String refreshToken) {
        blacklistAccessToken(accessToken);

        // Refresh Token이 없거나 잘못됐더라도 Access Token 로그아웃은 완료
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return;
        }

        Long tokenMemberId = jwtTokenProvider.getMemberId(refreshToken);

        // 요청자와 Refresh Token 주인이 같은지 확인
        if (!authenticatedMemberId.equals(tokenMemberId)) {
            return;
        }

        // Redis에 저장된 Refresh Token과 같은지 확인
        if (!refreshTokenRepository.matches(authenticatedMemberId, refreshToken)) {
            return;
        }

        refreshTokenRepository.deleteByMemberId(authenticatedMemberId
        );
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.save(memberId, refreshToken);
    }

    private AuthResult createAuthResult(Member member, TokenResponse tokenResponse) {
        return new AuthResult(
                member.getId(),
                member.getLoginId(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken()
        );
    }

    private void blacklistAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }

        Duration remainingExpiration = jwtTokenProvider.getRemainingExpiration(accessToken);

        if (remainingExpiration.isZero() || remainingExpiration.isNegative()) {
            return;
        }

        accessTokenBlacklistRepository.save(accessToken, remainingExpiration);
    }
}