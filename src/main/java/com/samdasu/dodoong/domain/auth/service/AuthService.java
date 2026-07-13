package com.samdasu.dodoong.domain.auth.service;

import com.samdasu.dodoong.domain.auth.entity.RefreshToken;
import com.samdasu.dodoong.domain.auth.dto.AuthResult;
import com.samdasu.dodoong.domain.auth.dto.LoginRequest;
import com.samdasu.dodoong.domain.auth.dto.SignupRequest;
import com.samdasu.dodoong.domain.auth.dto.TokenResponse;
import com.samdasu.dodoong.domain.auth.repository.RefreshTokenRepository;
import com.samdasu.dodoong.domain.auth.security.JwtTokenProvider;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    //TODO: refreshToken redis에 저장하도록 변경 필요
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResult signup(SignupRequest request) {
        validateDuplicateLoginId(request.loginId());

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .loginId(request.loginId())
                .encodedPassword(encodedPassword)
                .build();

        Member savedMember = memberRepository.save(member);

        TokenResponse tokenResponse =
                jwtTokenProvider.issueTokens(savedMember);

        saveOrUpdateRefreshToken(
                savedMember,
                tokenResponse.refreshToken()
        );

        return createAuthResult(
                savedMember,
                tokenResponse
        );
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        Member member = memberRepository
                .findByLoginId(request.loginId())
                .orElseThrow(() ->
                        new CustomException(
                                ErrorCode.INVALID_CREDENTIALS
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )) {
            throw new CustomException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        TokenResponse tokenResponse =
                jwtTokenProvider.issueTokens(member);

        saveOrUpdateRefreshToken(
                member,
                tokenResponse.refreshToken()
        );

        return createAuthResult(
                member,
                tokenResponse
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_LOGIN_ID
            );
        }
    }

    private void saveOrUpdateRefreshToken(
            Member member,
            String refreshToken
    ) {
        refreshTokenRepository
                .findByMemberId(member.getId())
                .ifPresentOrElse(
                        savedToken ->
                                savedToken.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.create(
                                        member,
                                        refreshToken
                                )
                        )
                );
    }

    private AuthResult createAuthResult(
            Member member,
            TokenResponse tokenResponse
    ) {
        return new AuthResult(
                member.getId(),
                member.getLoginId(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken()
        );
    }
}